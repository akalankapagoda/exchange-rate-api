package com.nosto.exchange.currencyconvert.model.apilayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Part of APILayer response for convert request.
 *
 * @param rate
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record APILayerInfo (BigDecimal rate) {
}
