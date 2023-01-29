package com.nosto.exchange.currencyconvert.controller;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.CurrencyConversionResponse;
import com.nosto.exchange.currencyconvert.model.ExchangeRateListResponse;
import com.nosto.exchange.currencyconvert.model.SupportedCurrenciesResponse;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExchangeRateControllerTest {

    @MockBean
    private ExchangeRateProvider mockExchangeRateProvider;

    @Autowired
    private ExchangeRateController exchangeRateController;

    private static Map<String, String> supportedCurrencies = new HashMap<>();

    private static final String CURRENCY_1 = "currency1";
    private static final String CURRENCY_2 = "currency2";

    @BeforeAll
    static void init() {
        supportedCurrencies.put(CURRENCY_1, "Description 1");
        supportedCurrencies.put(CURRENCY_2, "Description 2");
    }


    @Test
    void testListSupportedCurrencies() throws ExchangeServiceException {

        when(mockExchangeRateProvider.listSupportedCurrencies()).thenReturn(supportedCurrencies);

        SupportedCurrenciesResponse response = exchangeRateController.listSupportedCurrencies();

        assertThat(response.getCurrencies()).isEqualTo(supportedCurrencies);
    }

    @Test
    void testListExchangeRates() throws ExchangeServiceException {

        Map<String, Float> rates = new HashMap<>();
        rates.put(CURRENCY_1, 1.111F);

        when(mockExchangeRateProvider.listSupportedCurrencies()).thenReturn(supportedCurrencies);
        when(mockExchangeRateProvider.listExchangeRates(CURRENCY_1))
                .thenReturn(rates);

        ExchangeRateListResponse response = exchangeRateController.listExchangeRates(CURRENCY_1);

        assertThat(response.getRates()).isEqualTo(rates);

    }

    @Test
    void testConvertCurrency() throws ExchangeServiceException {

        Map<String, Float> rates = new HashMap<>();
        rates.put(CURRENCY_1, 1.111F);

        BigDecimal value = new BigDecimal("2.345");

        BigDecimal expectedValue = value.multiply(new BigDecimal(rates.get(CURRENCY_1)));

        when(mockExchangeRateProvider.listSupportedCurrencies()).thenReturn(supportedCurrencies);
        when(mockExchangeRateProvider.convertCurrency(CURRENCY_1, CURRENCY_2, value))
                .thenReturn(expectedValue);

        CurrencyConversionResponse response = exchangeRateController.convertCurrency(CURRENCY_1, CURRENCY_2, value);

        assertThat(response.getValue()).isEqualTo(expectedValue);
    }
}