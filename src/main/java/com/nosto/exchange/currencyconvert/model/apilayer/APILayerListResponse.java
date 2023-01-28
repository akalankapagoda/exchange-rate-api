package com.nosto.exchange.currencyconvert.model.apilayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record APILayerListResponse(
        Boolean success,
        String base,
        String date,
        Map<String, Float> rates
) {
}
