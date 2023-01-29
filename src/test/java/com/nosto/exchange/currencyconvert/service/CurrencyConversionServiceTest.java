package com.nosto.exchange.currencyconvert.service;

import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.exception.InvalidInputException;
import com.nosto.exchange.currencyconvert.model.ExchangeRateListResponse;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CurrencyConversionServiceTest {

    @MockBean
    private ExchangeRateProvider mockExchangeRateProvider;

    @Autowired
    private CurrencyConversionService currencyConversionService;

    private static Map<String, String> supportedCurrencies = new HashMap<>();

    private static final String CURRENCY_1 = "currency1";
    private static final String CURRENCY_2 = "currency2";

    @BeforeAll
    static void init() {
        supportedCurrencies.put(CURRENCY_1, "Description 1");
        supportedCurrencies.put(CURRENCY_2, "Description 2");
    }

    /**
     * Test if the input validations are properly done.
     *
     * @throws ExchangeServiceException
     */
    @Test
    void testInvalidInput() throws ExchangeServiceException {

        when(mockExchangeRateProvider.listSupportedCurrencies()).thenReturn(supportedCurrencies);

        assertThrows(InvalidInputException.class, () -> {
            currencyConversionService.listExchangeRates("INVALID_CURRENCY");
        });
    }
}