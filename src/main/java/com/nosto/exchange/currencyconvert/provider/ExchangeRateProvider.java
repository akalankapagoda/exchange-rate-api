package com.nosto.exchange.currencyconvert.provider;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.ExchangeRate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExchangeRateProvider {

    Map<String, String> listSupportedCurrencies() throws ExchangeServiceException;

    Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException;

    BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value) throws ExchangeServiceException;
}
