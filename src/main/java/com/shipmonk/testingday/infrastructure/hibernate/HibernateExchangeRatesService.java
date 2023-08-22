package com.shipmonk.testingday.infrastructure.hibernate;

import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.ExchangeRatesProvider;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HibernateExchangeRatesService implements ExchangeRatesProvider {

    private final DailyExchangeRateRepository exchangeRateRepository;

    @Value("${exchange.rates.currency.base.default}")
    private String defaultBaseCurrency;

    @Autowired
    public HibernateExchangeRatesService(DailyExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ExchangeRates getRates(LocalDate day, List<CurrencyUnit> symbols)
        throws ExchangeRatesProviderException {

        if (symbols.isEmpty()) {
            throw ExchangeRatesProviderException.becauseNoConversionSymbolsProvided();
        }

        try {
            List<String> currencyCodes = symbols.stream()
                .map(CurrencyUnit::getCurrencyCode)
                .toList();
            List<DailyExchangeRate> ratesFromDb =
                exchangeRateRepository.findByIdDateAndIdCurrencyCodeIn(
                    day,
                    currencyCodes,
                    Sort.by("id.currencyCode").ascending()
                );

            Map<String, BigDecimal> rates = ratesFromDb.stream()
                .collect(Collectors.toMap(
                    rate -> rate.getId().getCurrencyCode(),
                    DailyExchangeRate::getRate
                ));

            return new ExchangeRates(true, this.defaultBaseCurrency, day, rates);

        } catch (Exception e) {
            // TODO log exception
            throw ExchangeRatesProviderException.becauseFetchingFailed(e);
        }
    }

    public ExchangeRates saveRates(ExchangeRates exchangeRates) {
        var savedRates = new ArrayList<DailyExchangeRate>();

        exchangeRates.getRates().forEach((key, value) -> {
            var id = new DailyExchangeRateId(
                exchangeRates.getDate(),
                key
            );
            var rate = new DailyExchangeRate(
                id,
                exchangeRates.getBase(),
                value
            );

            savedRates.add(exchangeRateRepository.save(rate));
        });

        Map<String, BigDecimal> savedRateMap = savedRates.stream()
            .collect(Collectors.toMap(
                rate -> rate.getId().getCurrencyCode(),
                DailyExchangeRate::getRate
            ));

        return new ExchangeRates(
            true,
            this.defaultBaseCurrency,
            exchangeRates.getDate(),
            savedRateMap
        );
    }

}
