package com.john.stockclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

class WebClientStockClientIntegrationTest {
    private WebClient webClient = WebClient.builder().build();

    @Test
    void shouldRetrieveStockPricesFromTheService() {
        // given
        WebClientStockClient client = new WebClientStockClient(webClient);
        final String symbol = "SYMBOL";
        final int amount = 5;

        // when
        Flux<StockPrice> prices = client.pricesFor(symbol);

        // then
        Assertions.assertNotNull(prices);
        Flux<StockPrice> fivePrices = prices.take(amount);
        Assertions.assertEquals(amount, fivePrices.count().block());
        Assertions.assertEquals(symbol, fivePrices.blockFirst().getSymbol());
    }
}