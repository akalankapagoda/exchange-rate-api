package com.nosto.exchange.currencyconvert.model;

import java.util.Map;

/**
 * API response for supported currencies request.
 */
public class SupportedCurrenciesResponse extends ExchangeRateBaseResponse {

    private Map<String, String> currencies;

    public Map<String, String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Map<String, String> currencies) {
        this.currencies = currencies;
    }
}
