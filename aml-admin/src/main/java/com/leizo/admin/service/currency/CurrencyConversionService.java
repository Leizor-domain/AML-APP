package com.leizo.admin.service.currency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

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
            String cacheKey = base + "_" + (symbols != null ? symbols : "ALL");
            CachedRates cached = ratesCache.get(cacheKey);
            
            // Check if we have valid cached data
            if (cached != null && !cached.isExpired()) {
                logger.debug("Using cached rates for base: {}, symbols: {}", base, symbols);
                return cached.getRates();
            }
            
            // Build URL with parameters
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("?base=").append(base);
            if (symbols != null && !symbols.trim().isEmpty()) {
                urlBuilder.append("&symbols=").append(symbols);
            }
            
            String url = urlBuilder.toString();
            logger.info("Fetching exchange rates from: {}", url);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                logger.error("Received null response from exchange rate API");
                return createErrorResponse(base, "No response from API");
            }
            
            JsonNode rootNode = objectMapper.readTree(response);
            
            // Check if the response contains an error
            if (rootNode.has("error") && rootNode.get("error").asBoolean()) {
                String errorMessage = rootNode.get("message").asText();
                logger.error("API Error: {}", errorMessage);
                return createErrorResponse(base, errorMessage);
            }
            
            // Parse successful response
            CurrencyRateDTO result = new CurrencyRateDTO();
            result.setBase(rootNode.get("base").asText());
            result.setDate(LocalDate.parse(rootNode.get("date").asText()));
            
            Map<String, BigDecimal> rates = new HashMap<>();
            JsonNode ratesNode = rootNode.get("rates");
            
            if (ratesNode != null && ratesNode.isObject()) {
                ratesNode.fields().forEachRemaining(entry -> {
                    String currency = entry.getKey();
                    BigDecimal rate = new BigDecimal(entry.getValue().asText());
                    rates.put(currency, rate);
                });
            }
            
            result.setRates(rates);
            
            // Cache the result
            ratesCache.put(cacheKey, new CachedRates(result));
            
            logger.info("Successfully fetched {} exchange rates for base: {}", rates.size(), base);
            return result;
            
        } catch (ResourceAccessException e) {
            logger.error("Network error while fetching exchange rates: {}", e.getMessage());
            return createErrorResponse(base, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("HTTP error while fetching exchange rates: {} - {}", e.getStatusCode(), e.getMessage());
            return createErrorResponse(base, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            logger.error("Server error while fetching exchange rates: {} - {}", e.getStatusCode(), e.getMessage());
            return createErrorResponse(base, "Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error while fetching exchange rates: {}", e.getMessage(), e);
            return createErrorResponse(base, "Service temporarily unavailable. Please try again later.");
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
            CurrencyRateDTO rates = getLatestRates(fromCurrency, toCurrency);
            
            if (rates.getError() != null) {
                logger.error("Failed to get rates for conversion: {}", rates.getError());
                throw new RuntimeException("Failed to get exchange rates: " + rates.getError());
            }
            
            BigDecimal rate = rates.getRates().get(toCurrency);
            if (rate == null) {
                throw new IllegalArgumentException("Rate not found for currency: " + toCurrency);
            }
            
            return amount.multiply(rate);
            
        } catch (Exception e) {
            logger.error("Currency conversion failed: {}", e.getMessage());
            throw new RuntimeException("Currency conversion failed: " + e.getMessage());
        }
    }
    
    private CurrencyRateDTO createErrorResponse(String base, String errorMessage) {
        CurrencyRateDTO errorResponse = new CurrencyRateDTO();
        errorResponse.setBase(base);
        errorResponse.setRates(new HashMap<>());
        errorResponse.setError(errorMessage);
        return errorResponse;
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
    
    /**
     * Internal class for caching rates with expiration
     */
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