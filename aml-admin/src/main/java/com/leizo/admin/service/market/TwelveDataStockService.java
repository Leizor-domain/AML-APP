package com.leizo.admin.service.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class TwelveDataStockService {
    
    private static final Logger logger = LoggerFactory.getLogger(TwelveDataStockService.class);
    private static final String BASE_URL = "https://api.twelvedata.com/time_series";
    
    @Value("${twelve.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public TwelveDataStockService() {
        this.restTemplate = createRestTemplateWithTimeouts();
        this.objectMapper = new ObjectMapper();
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
    
    public StockDataDTO getStockData(String symbol) {
        try {
            String apiUrl = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=1min&apikey=" + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse and return real data
                JsonNode rootNode;
                try {
                    rootNode = objectMapper.readTree(response.getBody());
                } catch (Exception e) {
                    logger.error("Failed to parse JSON response for symbol {}: {}", symbol, e.getMessage());
                    return createErrorResponse(symbol, "Invalid response format from API");
                }
                
                // Check if the response contains an error
                if (rootNode.has("code") && rootNode.has("message")) {
                    String errorMessage = rootNode.get("message").asText();
                    logger.error("API Error for symbol {}: {}", symbol, errorMessage);
                    return createErrorResponse(symbol, errorMessage);
                }
                
                // Check if we have values array
                if (!rootNode.has("values") || !rootNode.get("values").isArray()) {
                    logger.error("Invalid response format for symbol {}: missing or invalid values array", symbol);
                    return createErrorResponse(symbol, "Invalid response format");
                }
                
                List<StockDataDTO.PricePoint> pricePoints = new ArrayList<>();
                JsonNode valuesNode = rootNode.get("values");
                
                for (JsonNode valueNode : valuesNode) {
                    try {
                        if (valueNode.has("datetime") && valueNode.has("close")) {
                            StockDataDTO.PricePoint point = new StockDataDTO.PricePoint();
                            point.setDatetime(valueNode.get("datetime").asText());
                            
                            String closeStr = valueNode.get("close").asText();
                            if (closeStr != null && !closeStr.trim().isEmpty()) {
                                try {
                                    point.setClose(Double.parseDouble(closeStr));
                                    pricePoints.add(point);
                                } catch (NumberFormatException e) {
                                    logger.warn("Invalid close price for symbol {}: {}", symbol, closeStr);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to parse price point for symbol {}: {}", symbol, e.getMessage());
                    }
                }
                
                StockDataDTO result = new StockDataDTO();
                result.setSymbol(symbol);
                result.setData(pricePoints);
                
                logger.info("Successfully fetched {} price points for symbol: {}", pricePoints.size(), symbol);
                return result;
            } else {
                logger.error("Stock API returned non-2xx: {}", response.getStatusCode());
                return createFallbackStockData(symbol, "API returned non-2xx");
            }
        } catch (ResourceAccessException e) {
            logger.error("Network error while fetching stock data for symbol {}: {}", symbol, e.getMessage());
            return createFallbackStockData(symbol, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("HTTP error while fetching stock data for symbol {}: {} - {}", symbol, e.getStatusCode(), e.getMessage());
            return createFallbackStockData(symbol, "Service temporarily unavailable. Please try again later.");
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            logger.error("Server error while fetching stock data for symbol {}: {} - {}", symbol, e.getStatusCode(), e.getMessage());
            return createFallbackStockData(symbol, "Service temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Stock API call failed: {}", e.getMessage());
            return createFallbackStockData(symbol, e.getMessage());
        }
    }
    
    private StockDataDTO createErrorResponse(String symbol, String errorMessage) {
        StockDataDTO errorResponse = new StockDataDTO();
        errorResponse.setSymbol(symbol != null ? symbol : "UNKNOWN");
        errorResponse.setData(new ArrayList<>());
        errorResponse.setError(errorMessage);
        return errorResponse;
    }

    private StockDataDTO createFallbackStockData(String symbol, String errorMessage) {
        logger.warn("Using fallback data for symbol: {}", symbol);
        StockDataDTO fallback = new StockDataDTO();
        fallback.setSymbol(symbol);
        fallback.setData(new ArrayList<>()); // Return empty data as a fallback
        fallback.setError(errorMessage);
        return fallback;
    }
    
    public static class StockDataDTO {
        private String symbol;
        private List<PricePoint> data;
        private String error;
        
        public String getSymbol() {
            return symbol;
        }
        
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        
        public List<PricePoint> getData() {
            return data;
        }
        
        public void setData(List<PricePoint> data) {
            this.data = data;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public static class PricePoint {
            private String datetime;
            private double close;
            
            public String getDatetime() {
                return datetime;
            }
            
            public void setDatetime(String datetime) {
                this.datetime = datetime;
            }
            
            public double getClose() {
                return close;
            }
            
            public void setClose(double close) {
                this.close = close;
            }
        }
    }
} 