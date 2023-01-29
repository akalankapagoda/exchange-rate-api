package com.nosto.exchange.currencyconvert.model.apilayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * APILayer response for convert request.
 *
 * @param info
 * @param success
 * @param result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record APILayerConvertResponse (APILayerInfo info, boolean success, BigDecimal result) {
}

