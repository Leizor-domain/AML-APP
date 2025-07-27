package com.leizo.admin.controller;

import com.leizo.admin.service.currency.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyConversionController {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
    
    @Autowired
    private CurrencyConversionService currencyService;
    
    /**
     * GET /api/currency?base=USD&symbols=EUR,GBP
     * Fetch latest exchange rates for a base currency
     */
    @GetMapping
    public ResponseEntity<?> getLatestRates(
            @RequestParam(defaultValue = "USD") String base,
            @RequestParam(required = false) String symbols) {
        
        try {
            logger.info("Fetching exchange rates for base: {}, symbols: {}", base, symbols);
            
            CurrencyConversionService.CurrencyRateDTO rates = currencyService.getLatestRates(base, symbols);
            
            if (rates.getError() != null) {
                logger.error("Failed to fetch rates: {}", rates.getError());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", rates.getError()));
            }
            
            return ResponseEntity.ok(rates);
            
        } catch (Exception e) {
            logger.error("Unexpected error in getLatestRates: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch exchange rates: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/currency/convert?from=USD&to=EUR&amount=100
     * Convert amount from one currency to another
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        
        try {
            logger.info("Converting {} {} to {}", amount, from, to);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Amount must be greater than zero"));
            }
            
            BigDecimal convertedAmount = currencyService.convertCurrency(from, to, amount);
            
            Map<String, Object> response = Map.of(
                "from", from,
                "to", to,
                "amount", amount,
                "convertedAmount", convertedAmount,
                "rate", convertedAmount.divide(amount, 6, BigDecimal.ROUND_HALF_UP)
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid conversion request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in convertCurrency: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to convert currency: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/currency/cache/clear
     * Clear the rates cache (admin utility)
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<?> clearCache() {
        try {
            logger.info("Clearing currency rates cache");
            currencyService.clearCache();
            return ResponseEntity.ok(Map.of("message", "Cache cleared successfully"));
        } catch (Exception e) {
            logger.error("Failed to clear cache: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to clear cache: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/currency/cache/stats
     * Get cache statistics (admin utility)
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<?> getCacheStats() {
        try {
            logger.debug("Getting currency cache statistics");
            Map<String, Object> stats = currencyService.getCacheStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to get cache stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get cache statistics: " + e.getMessage()));
        }
    }
} 