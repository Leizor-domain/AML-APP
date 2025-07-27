package com.leizo.admin.controller;

import com.leizo.admin.service.market.TwelveDataStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockMarketController {
    
    @Autowired
    private TwelveDataStockService stockService;
    
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockData(@PathVariable String symbol) {
        try {
            TwelveDataStockService.StockDataDTO stockData = stockService.getStockData(symbol);
            
            if (stockData.getError() != null) {
                return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", stockData.getError()));
            }
            
            return ResponseEntity.ok(stockData);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(java.util.Map.of("error", "Failed to fetch stock data: " + e.getMessage()));
        }
    }
} 