package com.leizo.admin.controller;

import com.leizo.admin.service.market.TwelveDataStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockMarketController {
    
    private static final Logger logger = LoggerFactory.getLogger(StockMarketController.class);
    
    @Autowired(required = false)
    private TwelveDataStockService stockService;
    
    /**
     * GET /api/stocks/{symbol}
     * Fetch stock market data for a given symbol
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockData(@PathVariable String symbol) {
        try {
            logger.info("Fetching stock data for symbol: {}", symbol);
            
            // Fallback if service is not available
            if (stockService == null) {
                logger.warn("TwelveDataStockService not available, returning fallback data");
                return ResponseEntity.ok(createFallbackStockData(symbol));
            }
            
            TwelveDataStockService.StockDataDTO stockData = stockService.getStockData(symbol);
            
            if (stockData.getError() != null) {
                logger.error("Failed to fetch stock data: {}", stockData.getError());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", stockData.getError()));
            }
            
            return ResponseEntity.ok(stockData);
            
        } catch (Exception e) {
            logger.error("Unexpected error in getStockData: {}", e.getMessage(), e);
            
            // Return fallback data instead of error
            return ResponseEntity.ok(createFallbackStockData(symbol));
        }
    }
    
    /**
     * Create fallback stock data when external service is unavailable
     */
    private Map<String, Object> createFallbackStockData(String symbol) {
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("symbol", symbol);
        fallbackData.put("message", "Using fallback data - external service unavailable");
        fallbackData.put("timestamp", java.time.LocalDateTime.now().toString());
        
        List<Map<String, Object>> dataPoints = new ArrayList<>();
        
        // Create mock data points
        for (int i = 0; i < 5; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("datetime", java.time.LocalDateTime.now().minusMinutes(i).toString());
            point.put("close", 150.0 + (Math.random() * 10.0)); // Random price around 150
            dataPoints.add(point);
        }
        
        fallbackData.put("data", dataPoints);
        return fallbackData;
    }
} 