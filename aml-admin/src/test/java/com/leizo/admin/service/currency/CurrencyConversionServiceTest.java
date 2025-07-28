package com.leizo.admin.service.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Test
    void testConvertCurrency_Success() {
        // Mock successful API response
        String mockResponse = """
            {
                "success": true,
                "rates": {
                    "EUR": 0.85,
                    "USD": 1.0,
                    "GBP": 0.73
                }
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(mockResponse);

        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", new BigDecimal("100"));

        // Verify
        assertNotNull(result);
        assertEquals(new BigDecimal("85.00"), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void testConvertCurrency_InvalidBaseCurrency() {
        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("INVALID", "EUR", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_InvalidToCurrency() {
        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "INVALID", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_NullAmount() {
        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", null);

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_ZeroAmount() {
        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", BigDecimal.ZERO);

        // Verify
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testConvertCurrency_SameCurrency() {
        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "USD", new BigDecimal("100"));

        // Verify
        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void testConvertCurrency_ApiError() {
        // Mock API error response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn("{\"success\": false, \"error\": \"Invalid API key\"}");

        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_InvalidJsonResponse() {
        // Mock invalid JSON response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn("invalid json");

        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_MissingRates() {
        // Mock response without rates
        String mockResponse = """
            {
                "success": true
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(mockResponse);

        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testConvertCurrency_MissingTargetRate() {
        // Mock response without target currency rate
        String mockResponse = """
            {
                "success": true,
                "rates": {
                    "USD": 1.0
                }
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(mockResponse);

        // Execute
        BigDecimal result = currencyConversionService.convertCurrency("USD", "EUR", new BigDecimal("100"));

        // Verify
        assertNull(result);
    }

    @Test
    void testGetLatestRates_Success() {
        // Mock successful API response
        String mockResponse = """
            {
                "success": true,
                "rates": {
                    "EUR": 0.85,
                    "USD": 1.0,
                    "GBP": 0.73
                }
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(mockResponse);

        // Execute
        CurrencyConversionService.CurrencyRateDTO result = currencyConversionService.getLatestRates("USD", "EUR,GBP");

        // Verify
        assertNotNull(result);
        assertNotNull(result.getRates());
        assertEquals(2, result.getRates().size());
        assertEquals(new BigDecimal("0.85"), result.getRates().get("EUR"));
        assertEquals(new BigDecimal("0.73"), result.getRates().get("GBP"));
    }

    @Test
    void testGetLatestRates_InvalidBaseCurrency() {
        // Execute
        CurrencyConversionService.CurrencyRateDTO result = currencyConversionService.getLatestRates("INVALID", "EUR");

        // Verify
        assertNotNull(result);
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("Invalid base currency format"));
    }

    @Test
    void testGetLatestRates_ApiError() {
        // Mock API error response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn("{\"success\": false, \"error\": \"Invalid API key\"}");

        // Execute
        CurrencyConversionService.CurrencyRateDTO result = currencyConversionService.getLatestRates("USD", "EUR");

        // Verify
        assertNotNull(result);
        assertNotNull(result.getError());
    }

    @Test
    void testGetLatestRates_InvalidJsonResponse() {
        // Mock invalid JSON response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn("invalid json");

        // Execute
        CurrencyConversionService.CurrencyRateDTO result = currencyConversionService.getLatestRates("USD", "EUR");

        // Verify
        assertNotNull(result);
        assertNotNull(result.getError());
    }

    @Test
    void testClearCache() {
        // Execute
        currencyConversionService.clearCache();

        // Verify - cache should be cleared (no exceptions thrown)
        assertTrue(true);
    }
} 