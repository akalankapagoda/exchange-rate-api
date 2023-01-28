package com.nosto.exchange.currencyconvert.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ExchangeServiceException extends Exception {

    public ExchangeServiceException(String message) {
        super(message);
    }


    public ExchangeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
