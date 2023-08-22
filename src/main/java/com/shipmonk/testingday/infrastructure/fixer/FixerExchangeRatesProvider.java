package com.shipmonk.testingday.infrastructure.fixer;

import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.ExchangeRatesProvider;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.money.CurrencyUnit;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class FixerExchangeRatesProvider implements ExchangeRatesProvider {

    private final FixerExchangeRatesFetcher fixerExchangeRatesFetcher;

    @Value("${fixer.api.key}")
    private String apiKey;

    @Value("${fixer.api.base.url}")
    private String baseURL;

    @Value("${exchange.rates.currency.base.default}")
    private String defaultBaseCurrency;

    public FixerExchangeRatesProvider(FixerExchangeRatesFetcher fixerExchangeRatesFetcher) {
        this.fixerExchangeRatesFetcher = fixerExchangeRatesFetcher;
    }

    @Override
    public ExchangeRates getRates(
        LocalDate day,
        List<CurrencyUnit> symbols
    ) throws ExchangeRatesProviderException {
        if (symbols.isEmpty()) {
            throw ExchangeRatesProviderException.becauseNoConversionSymbolsProvided();
        }

        JSONObject jsonResponse;
        var url = buildUrl(day, symbols);
        try {
            jsonResponse = this.fixerExchangeRatesFetcher.fetchRates(url);
        } catch (Exception e) {
            throw ExchangeRatesProviderException.becauseFetchingFailed(e);
        }

        ensureJsonResponseIsSuccesful(jsonResponse);

        try {
            return parseJson(jsonResponse);
        } catch (Exception e) {
            throw ExchangeRatesProviderException.becauseParsingResultJSONFailed(e);
        }
    }

    private String buildUrl(LocalDate day, List<CurrencyUnit> symbols) {
        String date = day.toString();

        return this.baseURL + date +
            "?access_key=" + this.apiKey +
//            "&base=" + this.defaultBaseCurrency +
            "&symbols=" + String.join(",", symbols.stream()
            .map(CurrencyUnit::getCurrencyCode)
            .toList()
        );
    }

    private static void ensureJsonResponseIsSuccesful(JSONObject jsonResponse)
        throws ExchangeRatesProviderException {
        if (jsonResponse.getBoolean("success")) {
            return;
        }

        throw ExchangeRatesProviderException.becauseThereWasErrorInFetchedData(
            new RuntimeException(
                String.format("Full response: %n%s", jsonResponse.toString(2))
            )
        );
    }

    private ExchangeRates parseJson(JSONObject jsonObject) {
        boolean success = jsonObject.getBoolean("success");
        String base = jsonObject.getString("base");
        LocalDate date = LocalDate.parse(jsonObject.getString("date"));
        JSONObject ratesObject = jsonObject.getJSONObject("rates");

        Map<String, BigDecimal> rates = new HashMap<>();
        Iterator<String> keys = ratesObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            rates.put(key, BigDecimal.valueOf(ratesObject.getDouble(key)));
        }

        return new ExchangeRates(
            success,
            base,
            date,
            rates
        );
    }
}
