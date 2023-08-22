package com.shipmonk.testingday.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ExchangeRates {
    private boolean success;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public ExchangeRates(
        boolean success,
        String base,
        LocalDate date,
        Map<String, BigDecimal> rates
    ) {
        this.success = success;
        this.base = base;
        this.date = date;
        this.rates = Map.copyOf(rates);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getBase() {
        return base;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
