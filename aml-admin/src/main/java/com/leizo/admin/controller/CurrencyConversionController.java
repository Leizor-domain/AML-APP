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
            @RequestParam(required = false) String base,
            @RequestParam(required = false) String symbols) {
        base = (base == null || base.isBlank()) ? "USD" : base.trim().toUpperCase();
        if (base.isBlank()) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", "Missing base currency",
                "rates", Map.of()
            ));
        }
        if (symbols != null && !symbols.isBlank() && !symbols.matches("^[A-Z,]+$")) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", "Invalid symbols format. Must be comma-separated uppercase codes.",
                "rates", Map.of()
            ));
        }
        // If symbols is missing, return all supported currencies
        CurrencyConversionService.CurrencyRateDTO rates = currencyService.getLatestRates(base, symbols);
        if (rates.getError() != null) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", rates.getError(),
                "rates", rates.getRates() != null ? rates.getRates() : Map.of()
            ));
        }
        return ResponseEntity.ok(Map.of(
            "base", rates.getBase(),
            "date", rates.getDate(),
            "rates", rates.getRates(),
            "error", false
        ));
    }
    
    /**
     * GET /api/currency/convert?from=USD&to=EUR&amount=100
     * Convert amount from one currency to another
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) BigDecimal amount) {
        from = (from == null || from.isBlank()) ? "USD" : from.trim().toUpperCase();
        to = (to == null || to.isBlank()) ? "EUR" : to.trim().toUpperCase();
        if (!from.matches("^[A-Z]{3}$") || !to.matches("^[A-Z]{3}$")) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", "Invalid currency code. Must be 3 uppercase letters.",
                "convertedAmount", null
            ));
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", "Amount must be a positive number.",
                "convertedAmount", null
            ));
        }
        try {
            BigDecimal convertedAmount = currencyService.convertCurrency(from, to, amount);
            return ResponseEntity.ok(Map.of(
                "from", from,
                "to", to,
                "amount", amount,
                "convertedAmount", convertedAmount,
                "error", false
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "error", true,
                "message", e.getMessage(),
                "convertedAmount", null
            ));
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