package com.shipmonk.testingday.infrastructure.fixer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.shipmonk.testingday.ApprovalTest;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.money.Monetary;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class FixerExchangeRatesProviderTest extends ApprovalTest {

    @Autowired
    private FixerExchangeRatesProvider fixerExchangeRatesProvider;

    @MockBean
    private FixerExchangeRatesFetcher fixerExchangeRatesFetcher;

    @Test
    public void itShouldFailWhenNoConversionSymbolsProvided() {
        Executable getRatesCall = () -> fixerExchangeRatesProvider.getRates(
            LocalDate.of(2023, 6, 6),
            List.of()
        );

        var exception = assertThrows(
            ExchangeRatesProviderException.class,
            getRatesCall
        );

        assertEquals("No symbols for currency conversion provided.", exception.getMessage());
    }

    @Test
    public void itShouldReturnExchangeRates()
        throws ExchangeRatesProviderException, IOException, InterruptedException {

        Mockito.when(
            fixerExchangeRatesFetcher.fetchRates(
                "http://data.fixer.io/api/2023-06-06?access_key=e43235c7402859979b61400725efb108&symbols=EUR,CZK"
            )
        ).thenReturn(getFetcherExampleResult());

        var exchangeRates = fixerExchangeRatesProvider.getRates(
            LocalDate.of(2023, 6, 6),
            List.of(
                Monetary.getCurrency("EUR"),
                Monetary.getCurrency("CZK")
            )
        );

        this.thenExpectedResult(exchangeRates);
    }

    public static JSONObject getFetcherExampleResult() {
        Map<String, Object> jsonData = new HashMap<>();

        jsonData.put("success", true);
        jsonData.put("timestamp", 1686095999);
        jsonData.put("historical", true);
        jsonData.put("base", "EUR");
        jsonData.put("date", "2023-06-06");

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0);
        rates.put("CZK", 23.578329);

        jsonData.put("rates", rates);

        return new JSONObject(jsonData);
    }
}
