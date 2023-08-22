package com.shipmonk.testingday.model;

import com.shipmonk.testingday.model.exceptions.ExchangeRatesProviderException;
import java.time.LocalDate;
import java.util.List;
import javax.money.CurrencyUnit;

public interface ExchangeRatesProvider {

    ExchangeRates getRates(LocalDate day, List<CurrencyUnit> symbols) throws ExchangeRatesProviderException;

}
