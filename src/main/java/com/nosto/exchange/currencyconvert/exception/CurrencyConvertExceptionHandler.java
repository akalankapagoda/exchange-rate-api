package com.nosto.exchange.currencyconvert.exception;

import com.nosto.exchange.currencyconvert.model.ExchangeRateBaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Handles errors received at the controller layer and returns a meaningful error message to the client.
 */
@ControllerAdvice
public class CurrencyConvertExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertExceptionHandler.class);

    /**
     * Handles internal server errors.
     *
     * @param e
     * @return A response with a 500 status code
     */
    @ExceptionHandler(ExchangeServiceException.class)
    public ResponseEntity<ExchangeRateBaseResponse> handleExchangeServiceException(ExchangeServiceException e) {

        logger.info("An error occurred in service request.", e);

        ExchangeRateBaseResponse errorResponse = new ExchangeRateBaseResponse();
        errorResponse.setDate(new Date());
        errorResponse.setSuccess(false);
        errorResponse.setMessage("An error occurred in service request. Please contact the administrator!");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Handles client side errors
     *
     * @param e
     * @return A response with a 400 status code
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ExchangeRateBaseResponse> handleInvalidInputException(InvalidInputException e) {

        // We'll log just the error as it's a client error, and we don't want to fill the log file with client errors
        logger.info("A user error occurred in service request : " + e.getMessage());

        ExchangeRateBaseResponse errorResponse = new ExchangeRateBaseResponse();
        errorResponse.setDate(new Date());
        errorResponse.setSuccess(false);
        errorResponse.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
