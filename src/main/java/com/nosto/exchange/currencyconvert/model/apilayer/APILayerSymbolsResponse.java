package com.nosto.exchange.currencyconvert.model.apilayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * APILayer response for supported symbols.
 *
 * @param success
 * @param symbols
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record APILayerSymbolsResponse(
        Boolean success,
        Map<String, String> symbols
) {
}