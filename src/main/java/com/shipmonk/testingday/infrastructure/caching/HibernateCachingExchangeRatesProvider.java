package com.shipmonk.testingday.infrastructure.caching;

import com.shipmonk.testingday.infrastructure.fixer.FixerExchangeRatesProvider;
import com.shipmonk.testingday.infrastructure.hibernate.HibernateExchangeRatesService;
import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.ExchangeRatesProvider;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class HibernateCachingExchangeRatesProvider implements ExchangeRatesProvider {

    @Autowired
    private FixerExchangeRatesProvider fixerExchangeRatesProvider;

    @Autowired
    private HibernateExchangeRatesService hibernateExchangeRatesService;

    @Value("${exchange.rates.currency.base.default}")
    private String defaultBaseCurrency;

    @Override
    public ExchangeRates getRates(
        LocalDate day,
        List<CurrencyUnit> symbols
    ) throws ExchangeRatesProviderException {
        if (symbols.isEmpty()) {
            throw ExchangeRatesProviderException.becauseNoConversionSymbolsProvided();
        }

        ExchangeRates cachedRates;

        try {
            cachedRates = this.hibernateExchangeRatesService.getRates(day, symbols);
        } catch (ExchangeRatesProviderException e) {
            cachedRates = new ExchangeRates(
                true,
                this.defaultBaseCurrency,
                day,
                new HashMap<>()
            );
        }

        var missingSymbols = symbols.stream()
            .map(CurrencyUnit::getCurrencyCode)
            .collect(Collectors.toSet());

        Set<String> presentSymbols = cachedRates.getRates().keySet();

        missingSymbols.removeAll(presentSymbols);

        var missingcurrencyUnits = missingSymbols.stream()
            .map(Monetary::getCurrency)
            .toList();

        ExchangeRates freshRates;
        try {
            freshRates = this.fixerExchangeRatesProvider.getRates(day, missingcurrencyUnits);
            hibernateExchangeRatesService.saveRates(freshRates);
        } catch (ExchangeRatesProviderException e) {
            // TODO log exception
            freshRates = new ExchangeRates(
                true,
                this.defaultBaseCurrency,
                day,
                new HashMap<>()
            );
        }

        Map<String, BigDecimal> combinedRates = new HashMap<>();
        combinedRates.putAll(cachedRates.getRates());
        combinedRates.putAll(freshRates.getRates());

        return new ExchangeRates(
            true,
            this.defaultBaseCurrency,
            day,
            combinedRates
        );

    }
}
