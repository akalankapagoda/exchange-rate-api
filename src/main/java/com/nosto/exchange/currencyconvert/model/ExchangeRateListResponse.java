package com.nosto.exchange.currencyconvert.model;

import java.util.Map;

/**
 * API response for list request.
 */
public class ExchangeRateListResponse extends ExchangeRateBaseResponse {

    private String base;

    private Map<String, Float> rates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Float> getRates() {
        return rates;
    }

    public void setRates(Map<String, Float> rates) {
        this.rates = rates;
    }
}
