package com.nosto.exchange.currencyconvert.model.apilayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * APILayer response for list request.
 *
 * @param success
 * @param base
 * @param date
 * @param rates
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record APILayerListResponse(
        Boolean success,
        String base,
        String date,
        Map<String, Float> rates
) {
}
