package com.nosto.exchange.currencyconvert.service;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private final ExchangeRateProvider exchangeRateProvider;

    @Autowired
    public CurrencyConversionService(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public Map<String, String> listSupportedCurrencies() throws ExchangeServiceException {
        return exchangeRateProvider.listSupportedCurrencies();
    }

    public Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException {
        return exchangeRateProvider.listExchangeRates(baseCurrency);
    }

    public BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value)
            throws ExchangeServiceException {
        // TODO: Validate input, check if the currency is a valid one, check if value is null and then use 1 if it is 0
        return exchangeRateProvider.convertCurrency(baseCurrency, targetCurrency, value);
    }
}
