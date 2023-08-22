package com.shipmonk.testingday.infrastructure.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.shipmonk.testingday.ApprovalTest;
import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.money.Monetary;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class HibernateExchangeRatesServiceTest extends ApprovalTest {

    @Autowired
    private HibernateExchangeRatesService hibernateExchangeRatesService;


    @Test
    public void itShouldFailWhenNoConversionSymbolsProvided() {
        Executable getRatesCall = () -> hibernateExchangeRatesService.getRates(
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
    public void itShouldReturnEmptyListOfExchangeRatesWhenNoneFoundInDb()
        throws ExchangeRatesProviderException, IOException, InterruptedException {

        var exchangeRates = hibernateExchangeRatesService.getRates(
            LocalDate.of(2023, 6, 6),
            List.of(
                Monetary.getCurrency("EUR"),
                Monetary.getCurrency("CZK")
            )
        );

        this.thenExpectedResult(exchangeRates);
    }

    @Test
    public void itShouldReturnExchangeRatesPresentInDatabase()
        throws ExchangeRatesProviderException, IOException, InterruptedException {

        var exchangeRatesToInsert = new ExchangeRates(
            true,
            "EUR",
            LocalDate.of(2023, 6, 6),
            Map.of("CZK", new BigDecimal("23.578329"))
        );
        hibernateExchangeRatesService.saveRates(exchangeRatesToInsert);

        var exchangeRates = hibernateExchangeRatesService.getRates(
            LocalDate.of(2023, 6, 6),
            List.of(
                Monetary.getCurrency("EUR"),
                Monetary.getCurrency("CZK")
            )
        );

        this.thenExpectedResult(exchangeRates);
    }

    @Test
    public void itShouldReturnMultipleExchangeRatesPresentInDatabase()
        throws ExchangeRatesProviderException, IOException, InterruptedException {

        var exchangeRatesToInsert = new ExchangeRates(
            true,
            "EUR",
            LocalDate.of(2023, 6, 6),
            Map.of(
                "CZK", new BigDecimal("23.578329"),
                "JPY", new BigDecimal("114.44321")
            )
        );
        hibernateExchangeRatesService.saveRates(exchangeRatesToInsert);

        var exchangeRates = hibernateExchangeRatesService.getRates(
            LocalDate.of(2023, 6, 6),
            List.of(
                Monetary.getCurrency("CZK"),
                Monetary.getCurrency("JPY")
            )
        );

        this.thenExpectedResult(exchangeRates);
    }

}
