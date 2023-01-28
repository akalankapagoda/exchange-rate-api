package com.nosto.exchange.currencyconvert.provider.apilayer.cache;

import com.nosto.exchange.currencyconvert.cache.SynchronizedExpiringMap;

import java.math.BigDecimal;

/**
 * Holds exchange rates cache.
 */
public class APILayerRatesCache extends SynchronizedExpiringMap<BigDecimal> {

    public APILayerRatesCache(long expiryDurationMilliseconds) {
        super(expiryDurationMilliseconds);
    }

    /**
     * Builds a unique key for the cache.
     *
     * @param base The base currency
     * @param target The target currency
     * @return A generated unique key
     */
    private String buildKey(String base, String target) {
        return base + target;
    }

    /**
     * Adds a conversion rate to the cache.
     *
     * @param base The base currency
     * @param target The target currency
     * @param rate The exchange rate
     */
    public void putRateToCache(String base, String target, BigDecimal rate) {
        super.put(buildKey(base, target), rate);
    }

    /**
     * Retrieves a cached rate if available.
     *
     * @return The cached rate if found or null
     */
    public BigDecimal getCachedRate(String base, String target) {
        return super.get(buildKey(base, target));
    }

}
