package com.nosto.exchange.currencyconvert.model;

/**
 * An exchange rate of a currency against US Dollar.
 *
 * @param currency
 * @param rate
 */
public record ExchangeRate (String currency, Float rate) {
}
