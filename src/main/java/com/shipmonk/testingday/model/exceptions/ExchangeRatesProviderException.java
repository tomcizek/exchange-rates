package com.shipmonk.testingday.model.exceptions;

public class ExchangeRatesProviderException extends Exception {


    protected ExchangeRatesProviderException(
        String message
    ) {
        super(message);
    }

    protected ExchangeRatesProviderException(
        String message,
        Throwable cause
    ) {
        super(message, cause);
    }

    public static ExchangeRatesProviderException becauseNoConversionSymbolsProvided() {
        return new ExchangeRatesProviderException(
            "No symbols for currency conversion provided."
        );
    }

    public static ExchangeRatesProviderException becauseFetchingFailed(Exception e) {
        return new ExchangeRatesProviderException(
            "Fetching exchange rates failed.",
            e
        );
    }

    public static ExchangeRatesProviderException becauseThereWasErrorInFetchedData(Exception e) {
        return new ExchangeRatesProviderException(
            "There was error in fetched data.",
            e
        );
    }

    public static ExchangeRatesProviderException becauseParsingResultJSONFailed(Exception e) {
        return new ExchangeRatesProviderException(
            "Parsing JSON result of exchange rates failed.",
            e
        );
    }
}
