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
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public StockDataDTO getStockData(String symbol) {
        try {
            String url = String.format("%s?symbol=%s&interval=1min&apikey=%s", 
                BASE_URL, symbol, apiKey);
            
            logger.info("Fetching stock data for symbol: {}", symbol);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                logger.error("Received null response from Twelve Data API for symbol: {}", symbol);
                return createErrorResponse(symbol, "No response from API");
            }
            
            JsonNode rootNode = objectMapper.readTree(response);
            
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
                if (valueNode.has("datetime") && valueNode.has("close")) {
                    StockDataDTO.PricePoint point = new StockDataDTO.PricePoint();
                    point.setDatetime(valueNode.get("datetime").asText());
                    point.setClose(Double.parseDouble(valueNode.get("close").asText()));
                    pricePoints.add(point);
                }
            }
            
            StockDataDTO result = new StockDataDTO();
            result.setSymbol(symbol);
            result.setData(pricePoints);
            
            logger.info("Successfully fetched {} price points for symbol: {}", pricePoints.size(), symbol);
            return result;
            
        } catch (ResourceAccessException e) {
            logger.error("Network error while fetching stock data for symbol {}: {}", symbol, e.getMessage());
            return createErrorResponse(symbol, "Network error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while fetching stock data for symbol {}: {}", symbol, e.getMessage(), e);
            return createErrorResponse(symbol, "Unexpected error: " + e.getMessage());
        }
    }
    
    private StockDataDTO createErrorResponse(String symbol, String errorMessage) {
        StockDataDTO errorResponse = new StockDataDTO();
        errorResponse.setSymbol(symbol);
        errorResponse.setData(new ArrayList<>());
        errorResponse.setError(errorMessage);
        return errorResponse;
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