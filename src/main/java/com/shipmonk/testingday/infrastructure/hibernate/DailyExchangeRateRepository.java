package com.shipmonk.testingday.infrastructure.hibernate;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyExchangeRateRepository
    extends JpaRepository<DailyExchangeRate, DailyExchangeRateId> {

    List<DailyExchangeRate> findByIdDateAndIdCurrencyCodeIn(LocalDate date,
                                                            List<String> currencyCodes);

}
