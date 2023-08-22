package com.shipmonk.testingday.infrastructure.hibernate;

import java.io.Serializable;
import java.time.LocalDate;

public class DailyExchangeRateId implements Serializable {

    private LocalDate date;
    private String currencyCode;

    protected DailyExchangeRateId() {
    }

    public DailyExchangeRateId(LocalDate date, String currencyCode) {
        this.date = date;
        this.currencyCode = currencyCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
