package com.nosto.exchange.currencyconvert.controller;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.CurrencyConversionResponse;
import com.nosto.exchange.currencyconvert.model.ExchangeRate;
import com.nosto.exchange.currencyconvert.model.ExchangeRateListResponse;
import com.nosto.exchange.currencyconvert.model.SupportedCurrenciesResponse;
import com.nosto.exchange.currencyconvert.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("exchangeRate")
public class ExchangeRateController
{

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public ExchangeRateController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    /**
     * Health check endpoint.
     *
     * @return A simple hello message
     */
    @GetMapping("/hello")
    @Tag(name = "Health Check", description = "Endpoints for checking server health")
    public String getHello() {
        return "Hello";
    }

    @GetMapping("/symbols")
    @Tag(name = "List Supported Currencies", description = "Lists supported currencies by this API")
    public SupportedCurrenciesResponse listSupportedCurrencies()  throws ExchangeServiceException {

        Map<String, String> supportedCurrencies = currencyConversionService.listSupportedCurrencies();

        SupportedCurrenciesResponse response = new SupportedCurrenciesResponse();

        response.setSuccess(true);
        response.setDate(new Date());
        response.setCurrencies(supportedCurrencies);

        return response;
    }

    @GetMapping("/list")
    @Tag(name = "List Exchange Rates", description = "Lists exchange rates")
    public ExchangeRateListResponse listExchangeRates(@RequestParam(name = "base" ,
            defaultValue = "USD") String baseCurrency) throws ExchangeServiceException {

        Map<String, Float> rates = currencyConversionService.listExchangeRates(baseCurrency);

        ExchangeRateListResponse response = new ExchangeRateListResponse();

        response.setBase(baseCurrency);
        response.setRates(rates);
        response.setDate(new Date());
        response.setSuccess(true);
        response.setMessage("Listing exchange rates against base currency : " + baseCurrency);

        return response;
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param baseCurrency The source currency
     * @param targetCurrency The target currency
     * @param value The currency value
     *
     * @return The stock
     */
    @GetMapping
    @Tag(name = "Convert Currency", description = "Converts a currency")
    public CurrencyConversionResponse convertCurrency(
            @RequestParam(name = "base", defaultValue = "USD") String baseCurrency,
        @RequestParam(name = "target ") String targetCurrency,
        @RequestParam(name = "value", defaultValue = "1") BigDecimal value) throws ExchangeServiceException {

        BigDecimal convertedValue = currencyConversionService.convertCurrency(baseCurrency, targetCurrency, value);
        return new CurrencyConversionResponse();
    }

}
