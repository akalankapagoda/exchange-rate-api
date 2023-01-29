package com.nosto.exchange.currencyconvert.model;

import java.math.BigDecimal;

/**
 * API response for currency conversion.
 */
public class CurrencyConversionResponse extends ExchangeRateBaseResponse {

    private String base;

    private String target;

    private BigDecimal value;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
