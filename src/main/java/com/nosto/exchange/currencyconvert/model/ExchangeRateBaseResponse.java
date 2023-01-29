package com.nosto.exchange.currencyconvert.model;

import java.util.Date;

/**
 * Base API response for all requests.
 */
public class ExchangeRateBaseResponse {

    private boolean success;

    private String message;

    private Date date;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
