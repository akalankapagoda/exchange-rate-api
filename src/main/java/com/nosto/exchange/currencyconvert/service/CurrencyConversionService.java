package com.nosto.exchange.currencyconvert.service;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.exception.InvalidInputException;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service layer for currency conversion.
 */
@Service
public class CurrencyConversionService {

    private final ExchangeRateProvider exchangeRateProvider;

    @Autowired
    public CurrencyConversionService(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    /**
     * Lists supported currency symbols.
     *
     * @return List of supported currencies Map<ISO 4217 Currency Code, Currency Name>
     * @throws ExchangeServiceException
     */
    public Map<String, String> listSupportedCurrencies() throws ExchangeServiceException {
        return exchangeRateProvider.listSupportedCurrencies();
    }

    /**
     * List exchange rates against a base currency.
     *
     * @param baseCurrency ISO 4217 currency code of the base currency
     * @return List of exchange rates against the base currency
     * @throws ExchangeServiceException
     */
    public Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException {

        if (!exchangeRateProvider.listSupportedCurrencies().containsKey(baseCurrency)) {
            throw new InvalidInputException("The provided base currency is not supported." +
                    " Base currency : " + baseCurrency +
                    ". Please refer to the /symbols endpoint for supported currencies!");
        }

        return exchangeRateProvider.listExchangeRates(baseCurrency);
    }

    /**
     * Convert an amount from one currency to another.
     *
     * @param baseCurrency ISO 4217 base currency code
     * @param targetCurrency ISO 4217 target currency code
     * @param value Value to convert
     * @return The value in target currency
     * @throws ExchangeServiceException
     */
    public BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value)
            throws ExchangeServiceException {

        Map<String, String> supportedCurrencies = exchangeRateProvider.listSupportedCurrencies();

        if (!supportedCurrencies.containsKey(baseCurrency) || !supportedCurrencies.containsKey(targetCurrency)) {
            throw new InvalidInputException("The conversion of the provided currencies are not supported." +
                    " Source : " + baseCurrency + " Target : " + targetCurrency +
                    ". Please refer to the /symbols endpoint for supported currencies!");
        }

        return exchangeRateProvider.convertCurrency(baseCurrency, targetCurrency, value);
    }
}
