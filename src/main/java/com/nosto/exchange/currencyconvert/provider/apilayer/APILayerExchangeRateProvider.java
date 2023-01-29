package com.nosto.exchange.currencyconvert.provider.apilayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerConvertResponse;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerListResponse;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerSymbolsResponse;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import com.nosto.exchange.currencyconvert.provider.apilayer.cache.APILayerRatesCache;
import com.nosto.exchange.currencyconvert.provider.apilayer.cache.APILayerSymbolsCache;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Utilizes the data from <a href="https://apilayer.com/">APILayer</a> to calculate the exchange rate.
 */
@Component
public class APILayerExchangeRateProvider implements ExchangeRateProvider {

    private static final String API_KEY_HEADER_NAME = "apikey";

    @Value("${http.client.timeout.seconds}")
    private long httpClientTimeoutSeconds;

    @Value(("${api.layer.base.url}"))
    private String baseURL;

    @Value(("${api.layer.latest.resource.path}"))
    private String listResourcePath;

    @Value(("${api.layer.convert.resource.path}"))
    private String convertResourcePath;

    @Value(("${api.layer.symbols.resource.path}"))
    private String symbolsResourcePath;

    @Value(("${api.layer.access.key}"))
    private String apiAccessKey;

    @Value(("${api.layer.symbols.cache.expiry.millis}"))
    private long symbolsCacheExpiryMillis;

    @Value(("${api.layer.rates.cache.expiry.millis}"))
    private long ratesCacheExpiryMillis;

    private HttpClient apiLayerClient;

    private ObjectMapper objectMapper;

    private APILayerSymbolsCache symbolsCache;
    private APILayerRatesCache ratesCache;

    private URL baseURLContext;

    private static final String PARAM_SEPARATOR = "&";
    private static final String PARAM_VALUE_SEPARATOR = "=";
    private static final String QUERY_PARAM_START = "?";

    @PostConstruct
    public void init() throws MalformedURLException, URISyntaxException {

        apiLayerClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        symbolsCache = new APILayerSymbolsCache(symbolsCacheExpiryMillis);
        ratesCache = new APILayerRatesCache(ratesCacheExpiryMillis);

        baseURLContext = new URL(this.baseURL);

    }

    /**
     * Builds an HttpRequest initialized with auth headers and timeouts.
     *
     * @param url The request URL
     * @return An HttpRequest
     * @throws URISyntaxException
     */
    private HttpRequest buildRequest(URL url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(url.toURI())
                .timeout(Duration.ofSeconds(httpClientTimeoutSeconds))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .headers(API_KEY_HEADER_NAME, apiAccessKey)
                .GET()
                .build();
    }

    /**
     * List supported currencies by APILayer.
     * This utilizes a separate cache. It is recommended to keep this cache for a longer time eg:- One day.
     *
     * @return List of supported currencies Map<ISO 4217 Currency Code, Currency Name>
     * @throws ExchangeServiceException
     */
    @Override
    public Map<String, String> listSupportedCurrencies() throws ExchangeServiceException {

        Map<String, String> cachedSymbols = symbolsCache.getSupportedSymbols();

        if (cachedSymbols != null) {
            return cachedSymbols;
        }

        try {

            URL listURL = new URL(baseURLContext, symbolsResourcePath);

            HttpResponse<String> response = apiLayerClient.send(buildRequest(listURL), HttpResponse.BodyHandlers.ofString());

            APILayerSymbolsResponse symbolsResponse = objectMapper.readValue(response.body(),
                    APILayerSymbolsResponse.class);

            if (!symbolsResponse.success()) {
                throw new ExchangeServiceException("Failed to list supported currencies. " +
                        "APILayer returned an error response : " + response.body());
            }

            Map<String, String> symbols = symbolsResponse.symbols();

            CompletableFuture.runAsync(() ->
                symbolsCache.putSymbolsToCache(symbols)

            );

            return symbols;

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ExchangeServiceException("Failed to list exchange rates.", e);
        }
    }

    /**
     * Calling the API Layer endpoints and listing the currency rates.
     *
     * At this endpoint, we are deliberately skipping caching as listing involves many currencies and the
     * probability of the one of those changing in the next second is very high which would make the resources we
     * expend to cache this an expense that is not used.
     *
     * @param baseCurrency ISO 4217 currency code
     * @return List of exchange rates against the base currency
     * @throws ExchangeServiceException
     */
    @Override
    public Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException {

        try {

            URL listURL = new URL(baseURLContext, (new StringBuilder(listResourcePath))
                    .append(QUERY_PARAM_START)
                    .append("base").append(PARAM_VALUE_SEPARATOR).append(baseCurrency)
                    .toString());

            HttpResponse<String> response = apiLayerClient.send(buildRequest(listURL), HttpResponse.BodyHandlers.ofString());

            APILayerListResponse listResponse = objectMapper.readValue(response.body(), APILayerListResponse.class);

            if (!listResponse.success()) {
                throw new ExchangeServiceException("Failed to retrieve exchange rates." +
                        " APILayer returned an error response : " + response.body());
            }

            return listResponse.rates();

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ExchangeServiceException("Failed to list exchange rates.", e);
        }
    }

    /**
     * Convert an amount from one currency to another by calling the APILayer.
     * If the same two has been converted recently, uses the rate received in the earlier request to do the conversion.
     *
     * @param baseCurrency ISO 4217 base currency code
     * @param targetCurrency ISO 4217 target currency code
     * @param value Value to convert
     * @return The value in target currency
     * @throws ExchangeServiceException
     */
    @Override
    public BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value)
            throws ExchangeServiceException {

        BigDecimal cachedRate = ratesCache.getCachedRate(baseCurrency, targetCurrency);

        if (cachedRate != null) {
            return cachedRate.multiply(value);
        }

        try {

            URL listURL = new URL(baseURLContext, (new StringBuilder(convertResourcePath))
                    .append(QUERY_PARAM_START)
                    .append("from").append(PARAM_VALUE_SEPARATOR).append(baseCurrency)
                    .append(PARAM_SEPARATOR).append("to").append(PARAM_VALUE_SEPARATOR).append(targetCurrency)
                    .append(PARAM_SEPARATOR).append("amount").append(PARAM_VALUE_SEPARATOR).append(value)
                    .toString());

            HttpResponse<String> response = apiLayerClient.send(buildRequest(listURL),
                    HttpResponse.BodyHandlers.ofString());

            APILayerConvertResponse convertResponse = objectMapper.readValue(response.body(),
                    APILayerConvertResponse.class);

            if (!convertResponse.success()) {
                throw new ExchangeServiceException("Failed to convert rates! APILayer returned a failure response : " +
                        response.body());
            }

            BigDecimal rate = convertResponse.result();

            CompletableFuture.runAsync(() ->
                ratesCache.putRateToCache(baseCurrency, targetCurrency, convertResponse.info().rate())
            );


            return rate;

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ExchangeServiceException("Failed to list exchange rates.", e);
        }
    }
}
