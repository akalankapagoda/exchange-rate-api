package com.nosto.exchange.currencyconvert;

import com.nosto.exchange.currencyconvert.controller.ExchangeRateController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CurrencyConverterApplicationTests {

	@Autowired
	ExchangeRateController exchangeRateController;

	@Test
	void contextLoads() {
		assertThat(exchangeRateController).isNotNull();
	}

}
