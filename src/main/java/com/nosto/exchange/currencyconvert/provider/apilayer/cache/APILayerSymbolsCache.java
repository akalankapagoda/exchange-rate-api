package com.nosto.exchange.currencyconvert.provider.apilayer.cache;

import com.nosto.exchange.currencyconvert.cache.SynchronizedExpiringMap;

import java.util.Map;

/**
 * A cache built to hold API Layer responses which expires with time.
 */
public class APILayerSymbolsCache extends SynchronizedExpiringMap<Map<String, String>> {

    /**
     * Since we have only one key at the moment, we define a static value for it.
     */
    private static final String KEY = "KEY";

    public APILayerSymbolsCache(long expiryDurationMilliseconds) {
        super(expiryDurationMilliseconds);
    }

    /**
     * Add an item to cache.
     *
     * @param symbols Supported symbols Map<ShortCode, Description>
     */
    public void putSymbolsToCache(Map<String, String> symbols) {
        super.put(KEY, symbols);
    }

    /**
     * Retrieves an object from Cache.
     *
     * @return The cached symbols if found or null
     */
    public Map<String, String> getSupportedSymbols() {
        return super.get(KEY);
    }

}
