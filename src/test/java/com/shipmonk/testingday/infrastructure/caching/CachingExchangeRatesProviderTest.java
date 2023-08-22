package com.shipmonk.testingday.infrastructure.caching;

import com.shipmonk.testingday.ApprovalTest;
import com.shipmonk.testingday.infrastructure.fixer.FixerExchangeRatesFetcher;
import com.shipmonk.testingday.infrastructure.hibernate.HibernateExchangeRatesService;
import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.money.Monetary;
import javax.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@Transactional
class CachingExchangeRatesProviderTest extends ApprovalTest {

    @MockBean
    private FixerExchangeRatesFetcher fixerExchangeRatesFetcher;

    @Autowired
    private HibernateExchangeRatesService hibernateExchangeRatesService;

    @Autowired
    private HibernateCachingExchangeRatesProvider hibernateCachingExchangeRatesProvider;

    @Test
    public void itShouldReturnExchangeRatesWhenOnePresentInDatabaseAndOneNot()
        throws ExchangeRatesProviderException, IOException, InterruptedException {

        var exchangeRatesToInsert = new ExchangeRates(
            true,
            "EUR",
            LocalDate.of(2023, 6, 6),
            Map.of("CZK", new BigDecimal("23.578329"))
        );
        hibernateExchangeRatesService.saveRates(exchangeRatesToInsert);

        Mockito.when(
            fixerExchangeRatesFetcher.fetchRates(
                "http://data.fixer.io/api/2023-06-06?access_key=e43235c7402859979b61400725efb108&symbols=EUR"
            )
        ).thenReturn(getFetcherExampleResult());


        var exchangeRates = hibernateCachingExchangeRatesProvider.getRates(
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
