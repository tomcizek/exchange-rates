package com.shipmonk.testingday.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.shipmonk.testingday.ApprovalTest;
import com.shipmonk.testingday.infrastructure.fixer.FixerExchangeRatesProvider;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.time.LocalDate;
import java.util.List;
import javax.money.Monetary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FixerExchangeRatesProviderTest extends ApprovalTest {

    @Autowired
    private FixerExchangeRatesProvider fixerExchangeRatesProvider;

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
    public void itShouldReturnExchangeRates() throws ExchangeRatesProviderException {
        var exchangeRates = fixerExchangeRatesProvider.getRates(
            LocalDate.of(2023, 6, 6),
            List.of(
                Monetary.getCurrency("EUR"),
                Monetary.getCurrency("CZK")
            )
        );

        this.thenExpectedResult(exchangeRates);
    }
}
