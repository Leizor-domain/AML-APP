package com.leizo.service.impl;

import com.leizo.service.ExchangeRateService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final Map<String, BigDecimal> ratesToEUR = new HashMap<>();

    public ExchangeRateServiceImpl() {
        ratesToEUR.put("USD", BigDecimal.valueOf(0.91));
        ratesToEUR.put("EUR", BigDecimal.ONE);
        ratesToEUR.put("GBP", BigDecimal.valueOf(1.17));
    }

    @Override
    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        if (!toCurrency.equals("EUR")) {
            throw new UnsupportedOperationException("Only EUR is supported as target currency.");
        }
        BigDecimal rate = ratesToEUR.getOrDefault(fromCurrency, null);
        if (rate == null) throw new IllegalArgumentException("Unsupported currency: " + fromCurrency);
        return amount.multiply(rate);
    }
}
