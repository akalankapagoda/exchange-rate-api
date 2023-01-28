package com.nosto.exchange.currencyconvert.provider.apilayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosto.exchange.currencyconvert.exception.ExchangeServiceException;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerListResponse;
import com.nosto.exchange.currencyconvert.model.apilayer.APILayerSymbolsResponse;
import com.nosto.exchange.currencyconvert.provider.ExchangeRateProvider;
import com.nosto.exchange.currencyconvert.provider.apilayer.cache.APILayerRatesCache;
import com.nosto.exchange.currencyconvert.provider.apilayer.cache.APILayerSymbolsCache;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
import java.util.concurrent.ExecutionException;

/**
 * Utilizes the data from https://exchangeratesapi.io/, an APILayer company, to calculate the exchange rate.
 */
@Component
public class APILayerExchangeRateProvider implements ExchangeRateProvider {

    private static final String ACCESS_KEY_PARAM = "access_key";

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

    private HttpRequest symbolsRequest;

    private HttpRequest listRequest;

    private HttpRequest convertRequest;

    private ObjectMapper objectMapper;

    private APILayerSymbolsCache symbolsCache;
    private APILayerRatesCache ratesCache;

    @PostConstruct
    public void init() throws MalformedURLException, URISyntaxException {

        apiLayerClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        symbolsCache = new APILayerSymbolsCache(symbolsCacheExpiryMillis);
        ratesCache = new APILayerRatesCache(ratesCacheExpiryMillis);

        URL baseURL = new URL(this.baseURL);
        String accessKeyParameter = "?" + ACCESS_KEY_PARAM + "=" + apiAccessKey;

        URL listURL = new URL(baseURL, listResourcePath + accessKeyParameter);
        URL symbolsURL = new URL(baseURL, symbolsResourcePath + accessKeyParameter);
        URL convertURL = new URL(baseURL, convertResourcePath + accessKeyParameter);

        symbolsRequest = buildRequest(symbolsURL);
        listRequest = buildRequest(listURL);
        convertRequest = buildRequest(convertURL);

    }

    private HttpRequest buildRequest(URL url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(url.toURI())
                .timeout(Duration.ofSeconds(httpClientTimeoutSeconds))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .GET()
                .build();
    }

    @Override
    public Map<String, String> listSupportedCurrencies() throws ExchangeServiceException {

        Map<String, String> cachedSymbols = symbolsCache.getSupportedSymbols();

        if (cachedSymbols != null) {
            return cachedSymbols;
        }

        CompletableFuture<String> response = apiLayerClient.sendAsync(symbolsRequest,
                        HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);

        try {

            // TODO: Responses from two endpoints are different, need to investigate
            APILayerSymbolsResponse symbolsResponse = objectMapper.readValue(response.get(), APILayerSymbolsResponse.class);

            if (!symbolsResponse.success()) {
                throw new ExchangeServiceException("Failed to retrieve exchange rates from APILayer");
            }

            Map<String, String> symbols = symbolsResponse.symbols();

            symbolsCache.putSymbolsToCache(symbols);

            return symbols;

        } catch (InterruptedException | ExecutionException e) {
            throw new ExchangeServiceException("Failed to list exchange rates.", e);
        } catch (JsonProcessingException e) {
            throw new ExchangeServiceException("Failed to parse APILayer response for list request", e);
        }
    }

    /**
     * Calling the API Layer endpoints and listing the currency rates.
     *
     * At this endpoint, we are deliberately skipping caching as listing involves many currencies and the
     * probability of the one of those changing in the next second is very high which would make the resources we
     * expend to cache this an expense that is not used.
     *
     * @param baseCurrency
     * @return
     * @throws ExchangeServiceException
     */
    @Override
    public Map<String, Float> listExchangeRates(String baseCurrency) throws ExchangeServiceException {

        CompletableFuture<String> response = apiLayerClient.sendAsync(listRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        try {

            APILayerListResponse listResponse = objectMapper.readValue(response.get(), APILayerListResponse.class);

            if (!listResponse.success()) {
                throw new ExchangeServiceException("Failed to retrieve exchange rates from APILayer");
            }

            return listResponse.rates();

        } catch (InterruptedException | ExecutionException e) {
            throw new ExchangeServiceException("Failed to list exchange rates.", e);
        } catch (JsonProcessingException e) {
            throw new ExchangeServiceException("Failed to parse APILayer response for list request", e);
        }
    }

    @Override
    public BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal value) {
        return null;
    }
}
