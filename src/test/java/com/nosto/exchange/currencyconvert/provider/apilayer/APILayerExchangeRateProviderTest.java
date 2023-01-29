package com.nosto.exchange.currencyconvert.provider.apilayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerConvertResponse;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerInfo;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the APILayer provider functionality while mocking the APILayer.
 */
@WireMockTest(httpPort = 8082)
@SpringBootTest(properties = { "api.layer.base.url=http://localhost:8082/mockapilayer/" })
class APILayerExchangeRateProviderTest {

    private static final BigDecimal RATE = new BigDecimal("2.23");

    @Autowired
    private ExchangeRateProvider exchangeRateProvider;

    @Test
    void testConvertCurrency(WireMockRuntimeInfo wmRuntimeInfo) throws ExchangeServiceException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        BigDecimal value = new BigDecimal(3.36F);

        APILayerInfo info = new APILayerInfo(RATE);
        APILayerConvertResponse apiLayerConvertResponse = new APILayerConvertResponse(info, true,
                value.multiply(RATE));

        stubFor(get(urlPathEqualTo("/mockapilayer/convert")).willReturn(aResponse().withStatus(200)
                .withBody(objectMapper.writeValueAsString(apiLayerConvertResponse))));


        // First request goes to the 3rd party API
        BigDecimal result =
                exchangeRateProvider.convertCurrency("someCurrency1", "someCurrency2", value);

        assertThat(result).isEqualTo(value.multiply(RATE));

        // Next request should be picked up from the cache
        // The rate should be calculated instead of retrieving from the API
        // If this reaches the server, the value will not change even if the parameters change

        BigDecimal secondValue = value.multiply(new BigDecimal("2"));

        BigDecimal secondResult = exchangeRateProvider.convertCurrency(
                "someCurrency1", "someCurrency2", secondValue);

        assertThat(secondResult).isEqualTo(secondValue.multiply(RATE));

    }
}