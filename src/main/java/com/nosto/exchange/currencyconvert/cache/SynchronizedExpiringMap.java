package com.nosto.exchange.currencyconvert.cache;

import org.apache.commons.collections4.map.PassiveExpiringMap;

import java.util.Collections;
import java.util.Map;

/**
 * A thread safe cache built on Apache Commons PassiveExpiringMap.
 *
 * @param <T> The type of the cached objects
 */
public class SynchronizedExpiringMap<T> {

    private final Map<String, T> cache;

    /**
     * Initialise PassiveExpiringMap wrapping it with a Synchronized map.
     *
     * @param expiryDurationMilliseconds
     */
    public SynchronizedExpiringMap(long expiryDurationMilliseconds) {
        cache = Collections.synchronizedMap(
                new PassiveExpiringMap<>(expiryDurationMilliseconds));
    }

    /**
     * Retrieves a value in the cache or returns null if not found.
     *
     * @param key
     * @return The cached value or null
     */
    public T get(String key) {
        return cache.get(key);
    }

    /**
     * Adds or updated a value in the cache.
     *
     * @param key
     * @param value
     */
    public void put(String key, T value) {
        if (key != null && value != null) {
            cache.put(key, value);
        }
    }
}
