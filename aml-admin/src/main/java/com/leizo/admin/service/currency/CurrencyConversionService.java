package com.leizo.admin.service.currency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyConversionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionService.class);
    private static final String BASE_URL = "https://api.exchangerate.host/latest";
    private static final int CACHE_DURATION_MINUTES = 15;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, CachedRates> ratesCache;
    
    public CurrencyConversionService() {
        this.restTemplate = createRestTemplateWithTimeouts();
        this.objectMapper = new ObjectMapper();
        this.ratesCache = new ConcurrentHashMap<>();
    }
    
    private RestTemplate createRestTemplateWithTimeouts() {
        RestTemplate template = new RestTemplate();
        
        // Configure timeout settings
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(10000);   // 10 seconds
        
        template.setRequestFactory(factory);
        return template;
    }
    
    /**
     * Fetch latest exchange rates for a base currency
     * @param base The base currency (e.g., "USD", "EUR")
     * @param symbols Optional comma-separated list of target currencies (e.g., "EUR,GBP,JPY")
     * @return CurrencyRateDTO with rates and metadata
     */
    public CurrencyRateDTO getLatestRates(String base, String symbols) {
        try {
            // Input validation
            if (base == null || base.trim().isEmpty()) {
                logger.warn("Base currency is null or empty, using USD as default");
                base = "USD";
            }
            
            // Normalize base currency
            base = base.trim().toUpperCase();
            
            // Validate base currency format (3 letters)
            if (!base.matches("^[A-Z]{3}$")) {
                logger.error("Invalid base currency format: {}", base);
                return createErrorResponse(base, "Invalid base currency format. Must be 3 letters (e.g., USD, EUR)");
            }
            
            // Validate symbols if provided (more lenient - allow empty symbols)
            if (symbols != null && !symbols.trim().isEmpty()) {
                String[] symbolArray = symbols.split(",");
                for (String symbol : symbolArray) {
                    String cleanSymbol = symbol.trim().toUpperCase();
                    // Skip empty symbols after splitting
                    if (cleanSymbol.isEmpty()) {
                        continue;
                    }
                    if (!cleanSymbol.matches("^[A-Z]{3}$")) {
                        logger.error("Invalid symbol format: {}", cleanSymbol);
                        return createErrorResponse(base, "Invalid symbol format: " + cleanSymbol + ". Must be 3 letters");
                    }
                }
            }
            
            String cacheKey = base + "_" + (symbols != null ? symbols : "ALL");
            CachedRates cached = ratesCache.get(cacheKey);
            
            // Check if we have valid cached data
            if (cached != null && !cached.isExpired()) {
                logger.debug("Using cached rates for base: {}, symbols: {}", base, symbols);
                return cached.getRates();
            }
            
            // Build API URL
            String apiUrl = "https://api.exchangerate.host/latest?base=" + base;
            if (symbols != null && !symbols.isEmpty()) {
                apiUrl += "&symbols=" + symbols;
            }
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse and return real data
                JsonNode rootNode;
                try {
                    rootNode = objectMapper.readTree(response.getBody());
                } catch (Exception e) {
                    logger.error("Failed to parse JSON response: {}", e.getMessage());
                    return createErrorResponse(base, "Invalid response format from API");
                }
                
                // Check if the response contains an error
                if (rootNode.has("error") && rootNode.get("error").asBoolean()) {
                    String errorMessage = rootNode.has("message") ? rootNode.get("message").asText() : "Unknown API error";
                    logger.error("API Error: {}", errorMessage);
                    return createErrorResponse(base, errorMessage);
                }
                
                // Validate required fields
                if (!rootNode.has("base") || !rootNode.has("date") || !rootNode.has("rates")) {
                    logger.error("Missing required fields in API response");
                    return createErrorResponse(base, "Invalid response format from API");
                }
                
                CurrencyRateDTO parsedDto = new CurrencyRateDTO();
                parsedDto.setBase(rootNode.get("base").asText());
                
                try {
                    parsedDto.setDate(LocalDate.parse(rootNode.get("date").asText()));
                } catch (Exception e) {
                    logger.error("Failed to parse date from API response: {}", e.getMessage());
                    parsedDto.setDate(LocalDate.now());
                }
                
                Map<String, BigDecimal> rates = new HashMap<>();
                JsonNode ratesNode = rootNode.get("rates");
                
                if (ratesNode != null && ratesNode.isObject()) {
                    ratesNode.fields().forEachRemaining(entry -> {
                        try {
                            String currency = entry.getKey();
                            BigDecimal rate = new BigDecimal(entry.getValue().asText());
                            rates.put(currency, rate);
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid rate value for currency {}: {}", entry.getKey(), entry.getValue().asText());
                        }
                    });
                }
                
                parsedDto.setRates(rates);
                
                // Cache the result
                ratesCache.put(cacheKey, new CachedRates(parsedDto));
                
                logger.info("Successfully fetched {} exchange rates for base: {}", rates.size(), base);
                return parsedDto;
            } else {
                logger.error("Currency API returned non-2xx: {}", response.getStatusCode());
                return createFallbackRates(base, symbols, "API returned non-2xx");
            }
        } catch (ResourceAccessException e) {
            logger.error("Network error while fetching exchange rates: {}", e.getMessage());
            return createFallbackRates(base, symbols, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("HTTP error while fetching exchange rates: {} - {}", e.getStatusCode(), e.getMessage());
            return createFallbackRates(base, symbols, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            logger.error("Server error while fetching exchange rates: {} - {}", e.getStatusCode(), e.getMessage());
            return createFallbackRates(base, symbols, "Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error while fetching exchange rates: {}", e.getMessage(), e);
            return createFallbackRates(base, symbols, "Service temporarily unavailable. Please try again later.");
        }
    }
    
    /**
     * Convert amount from one currency to another
     * @param fromCurrency Source currency
     * @param toCurrency Target currency
     * @param amount Amount to convert
     * @return Converted amount
     */
    public BigDecimal convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) {
        try {
            // Input validation with defaults
            if (fromCurrency == null || fromCurrency.trim().isEmpty()) {
                fromCurrency = "USD"; // Default to USD
            }
            if (toCurrency == null || toCurrency.trim().isEmpty()) {
                toCurrency = "EUR"; // Default to EUR
            }
            if (amount == null) {
                amount = BigDecimal.ONE; // Default to 1
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                amount = BigDecimal.ONE; // Default to 1 if invalid
            }
            
            // Normalize currencies
            fromCurrency = fromCurrency.trim().toUpperCase();
            toCurrency = toCurrency.trim().toUpperCase();
            
            // Validate currency formats (more lenient)
            if (!fromCurrency.matches("^[A-Z]{3}$")) {
                logger.warn("Invalid from currency format: {}, using USD", fromCurrency);
                fromCurrency = "USD";
            }
            if (!toCurrency.matches("^[A-Z]{3}$")) {
                logger.warn("Invalid to currency format: {}, using EUR", toCurrency);
                toCurrency = "EUR";
            }
            
            // Same currency conversion
            if (fromCurrency.equals(toCurrency)) {
                return amount;
            }
            
            CurrencyRateDTO rates = getLatestRates(fromCurrency, toCurrency);
            
            if (rates.getError() != null) {
                logger.error("Failed to get rates for conversion: {}", rates.getError());
                throw new RuntimeException("Failed to get exchange rates: " + rates.getError());
            }
            
            Map<String, BigDecimal> ratesMap = rates.getRates();
            if (ratesMap == null || ratesMap.isEmpty()) {
                throw new RuntimeException("No exchange rates available");
            }
            
            BigDecimal rate = ratesMap.get(toCurrency);
            if (rate == null) {
                throw new IllegalArgumentException("Rate not found for currency: " + toCurrency);
            }
            
            return amount.multiply(rate);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid conversion request: {}", e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Currency conversion failed: {}", e.getMessage());
            throw new RuntimeException("Currency conversion failed: " + e.getMessage());
        }
    }
    
    private CurrencyRateDTO createErrorResponse(String base, String errorMessage) {
        CurrencyRateDTO errorResponse = new CurrencyRateDTO();
        errorResponse.setBase(base != null ? base : "USD");
        errorResponse.setRates(new HashMap<>());
        errorResponse.setError(errorMessage);
        return errorResponse;
    }

    private CurrencyRateDTO createFallbackRates(String base, String symbols, String errorMessage) {
        logger.warn("Using fallback rates for base: {}, symbols: {}, due to: {}", base, symbols, errorMessage);
        CurrencyRateDTO fallback = new CurrencyRateDTO();
        fallback.setBase(base);
        fallback.setDate(LocalDate.now());
        fallback.setError(errorMessage);
        fallback.setRates(new HashMap<>()); // Return empty rates as a fallback
        return fallback;
    }
    
    /**
     * Clear the rates cache (useful for testing or manual refresh)
     */
    public void clearCache() {
        ratesCache.clear();
        logger.info("Currency rates cache cleared");
    }
    
    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", ratesCache.size());
        stats.put("cacheKeys", ratesCache.keySet());
        return stats;
    }
    
    /**
     * Data Transfer Object for currency rates
     */
    public static class CurrencyRateDTO {
        private String base;
        private Map<String, BigDecimal> rates;
        private LocalDate date;
        private String error;
        
        public String getBase() {
            return base;
        }
        
        public void setBase(String base) {
            this.base = base;
        }
        
        public Map<String, BigDecimal> getRates() {
            return rates;
        }
        
        public void setRates(Map<String, BigDecimal> rates) {
            this.rates = rates;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    private static class CachedRates {
        private final CurrencyRateDTO rates;
        private final LocalDateTime cachedAt;
        
        public CachedRates(CurrencyRateDTO rates) {
            this.rates = rates;
            this.cachedAt = LocalDateTime.now();
        }
        
        public CurrencyRateDTO getRates() {
            return rates;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(cachedAt.plusMinutes(CACHE_DURATION_MINUTES));
        }
    }
} 