package com.shipmonk.testingday;

import com.shipmonk.testingday.model.ExchangeRates;
import com.shipmonk.testingday.model.ExchangeRatesProvider;
import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/v1/rates"
)
public class ExchangeRatesController {

    private ExchangeRatesProvider exchangeRatesProvider;

    @Value("${exchange.rates.currency.target.defaults}")
    private String defaultTargetCurrencies;

    public ExchangeRatesController(ExchangeRatesProvider exchangeRatesProvider) {
        this.exchangeRatesProvider = exchangeRatesProvider;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{day}")
    public ResponseEntity<Object> getRates(@PathVariable("day") String day) {

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(day);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(
                String.format("Invalid date format: '%s'", day),
                HttpStatus.BAD_REQUEST
            );
        }

        List<CurrencyUnit> defaultTargetCurrencies;
        try {
            defaultTargetCurrencies = Arrays.stream(this.defaultTargetCurrencies.split(","))
                .map(Monetary::getCurrency)
                .toList();
        } catch (Exception e) {
            // TODO log exception, bad configuration
            return new ResponseEntity<>(
                "Invalid currency symbols", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ExchangeRates rates = exchangeRatesProvider.getRates(
                localDate, defaultTargetCurrencies);
            return new ResponseEntity<>(rates, HttpStatus.OK);
        } catch (ExchangeRatesProviderException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
