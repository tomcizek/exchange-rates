package com.shipmonk.testingday.infrastructure.hibernate;

import java.math.BigDecimal;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class DailyExchangeRate {

    @EmbeddedId
    private DailyExchangeRateId id;

    private String baseCurrency;

    private BigDecimal rate;

    protected DailyExchangeRate() {
    }

    public DailyExchangeRate(DailyExchangeRateId id, String baseCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.rate = rate;
    }

    public DailyExchangeRateId getId() {
        return id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
