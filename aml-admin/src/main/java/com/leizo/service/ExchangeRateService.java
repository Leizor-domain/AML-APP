package com.leizo.service;

import java.math.BigDecimal;

public interface ExchangeRateService {

    BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount);

}
