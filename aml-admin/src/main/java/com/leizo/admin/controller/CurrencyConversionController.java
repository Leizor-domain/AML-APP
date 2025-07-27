package com.leizo.admin.controller;

import com.leizo.admin.service.currency.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyConversionController {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
    
    @Autowired(required = false)
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
            
            // Fallback if service is not available
            if (currencyService == null) {
                logger.warn("CurrencyConversionService not available, returning fallback data");
                Map<String, Object> fallbackResponse = new HashMap<>();
                fallbackResponse.put("base", base);
                fallbackResponse.put("date", java.time.LocalDate.now().toString());
                fallbackResponse.put("rates", Map.of(
                    "EUR", 0.85,
                    "GBP", 0.73,
                    "JPY", 110.0,
                    "CAD", 1.25
                ));
                fallbackResponse.put("message", "Using fallback rates - external service unavailable");
                return ResponseEntity.ok(fallbackResponse);
            }
            
            CurrencyConversionService.CurrencyRateDTO rates = currencyService.getLatestRates(base, symbols);
            
            if (rates.getError() != null) {
                logger.error("Failed to fetch rates: {}", rates.getError());
                // Return 200 OK with error status instead of 400
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("base", base);
                errorResponse.put("date", java.time.LocalDate.now().toString());
                errorResponse.put("rates", Map.of(
                    "EUR", 0.85,
                    "GBP", 0.73,
                    "JPY", 110.0,
                    "CAD", 1.25
                ));
                errorResponse.put("error", rates.getError());
                errorResponse.put("message", "Using fallback rates due to error");
                return ResponseEntity.ok(errorResponse);
            }
            
            return ResponseEntity.ok(rates);
            
        } catch (Exception e) {
            logger.error("Unexpected error in getLatestRates: {}", e.getMessage(), e);
            
            // Return fallback data instead of error
            Map<String, Object> fallbackResponse = new HashMap<>();
            fallbackResponse.put("base", base);
            fallbackResponse.put("date", java.time.LocalDate.now().toString());
            fallbackResponse.put("rates", Map.of(
                "EUR", 0.85,
                "GBP", 0.73,
                "JPY", 110.0,
                "CAD", 1.25
            ));
            fallbackResponse.put("message", "Service temporarily unavailable - using fallback rates");
            return ResponseEntity.ok(fallbackResponse);
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
                logger.warn("Invalid amount: {}, using default amount of 1", amount);
                amount = BigDecimal.ONE; // Use default amount instead of rejecting
            }
            
            // Fallback if service is not available
            if (currencyService == null) {
                logger.warn("CurrencyConversionService not available, using fallback conversion");
                BigDecimal fallbackRate = getFallbackRate(from, to);
                BigDecimal convertedAmount = amount.multiply(fallbackRate);
                
                Map<String, Object> response = Map.of(
                    "from", from,
                    "to", to,
                    "amount", amount,
                    "convertedAmount", convertedAmount,
                    "rate", fallbackRate,
                    "message", "Using fallback rate - external service unavailable"
                );
                return ResponseEntity.ok(response);
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
            // Return fallback conversion instead of 400 error
            BigDecimal fallbackRate = getFallbackRate(from, to);
            BigDecimal convertedAmount = amount.multiply(fallbackRate);
            
            Map<String, Object> response = Map.of(
                "from", from,
                "to", to,
                "amount", amount,
                "convertedAmount", convertedAmount,
                "rate", fallbackRate,
                "error", e.getMessage(),
                "message", "Using fallback rate due to validation error"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error in convertCurrency: {}", e.getMessage(), e);
            
            // Return fallback conversion instead of error
            BigDecimal fallbackRate = getFallbackRate(from, to);
            BigDecimal convertedAmount = amount.multiply(fallbackRate);
            
            Map<String, Object> response = Map.of(
                "from", from,
                "to", to,
                "amount", amount,
                "convertedAmount", convertedAmount,
                "rate", fallbackRate,
                "message", "Service temporarily unavailable - using fallback rate"
            );
            return ResponseEntity.ok(response);
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
            
            if (currencyService == null) {
                return ResponseEntity.ok(Map.of("message", "Cache clear requested - service not available"));
            }
            
            currencyService.clearCache();
            return ResponseEntity.ok(Map.of("message", "Currency rates cache cleared successfully"));
            
        } catch (Exception e) {
            logger.error("Error clearing cache: {}", e.getMessage(), e);
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
            logger.info("Getting currency cache statistics");
            
            if (currencyService == null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Cache stats requested - service not available",
                    "cacheSize", 0,
                    "lastUpdated", "N/A"
                ));
            }
            
            Map<String, Object> stats = currencyService.getCacheStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error getting cache stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get cache stats: " + e.getMessage()));
        }
    }
    
    /**
     * Fallback rate method for when external service is unavailable
     */
    private BigDecimal getFallbackRate(String from, String to) {
        Map<String, Map<String, BigDecimal>> fallbackRates = Map.of(
            "USD", Map.of("EUR", new BigDecimal("0.85"), "GBP", new BigDecimal("0.73"), "JPY", new BigDecimal("110.0"), "CAD", new BigDecimal("1.25")),
            "EUR", Map.of("USD", new BigDecimal("1.18"), "GBP", new BigDecimal("0.86"), "JPY", new BigDecimal("129.4"), "CAD", new BigDecimal("1.47")),
            "GBP", Map.of("USD", new BigDecimal("1.37"), "EUR", new BigDecimal("1.16"), "JPY", new BigDecimal("150.7"), "CAD", new BigDecimal("1.71")),
            "JPY", Map.of("USD", new BigDecimal("0.0091"), "EUR", new BigDecimal("0.0077"), "GBP", new BigDecimal("0.0066"), "CAD", new BigDecimal("0.0114")),
            "CAD", Map.of("USD", new BigDecimal("0.80"), "EUR", new BigDecimal("0.68"), "GBP", new BigDecimal("0.58"), "JPY", new BigDecimal("88.0"))
        );
        
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        
        Map<String, BigDecimal> fromRates = fallbackRates.get(from.toUpperCase());
        if (fromRates != null && fromRates.containsKey(to.toUpperCase())) {
            return fromRates.get(to.toUpperCase());
        }
        
        // Default fallback rate
        return new BigDecimal("1.0");
    }
} 