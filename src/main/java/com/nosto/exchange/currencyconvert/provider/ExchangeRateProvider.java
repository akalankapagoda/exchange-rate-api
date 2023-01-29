package com.nosto.exchange.currencyconvert.provider;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * A provider interface to provide currency conversion rates.
 */
public interface ExchangeRateProvider {

    /**
     * Lists supported currency symbols.
     *
     * @return List of supported currencies Map<ISO 4217 Currency Code, Currency Name>
     * @throws ExchangeServiceException
     */
    Map<String, String> listSupportedCurrencies() throws ExchangeServiceException;

    /**
     * List exchange rates against a base currency.
     *
     * @param baseCurrency ISO 4217 currency code of the base currency
     * @return List of exchange rates against the base currency
     * @throws ExchangeServiceException
     */
    Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException;

    /**
     * Convert an amount from one currency to another.
     *
     * @param baseCurrency ISO 4217 base currency code
     * @param targetCurrency ISO 4217 target currency code
     * @param value Value to convert
     * @return The value in target currency
     * @throws ExchangeServiceException
     */
    BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value)
            throws ExchangeServiceException;
}
