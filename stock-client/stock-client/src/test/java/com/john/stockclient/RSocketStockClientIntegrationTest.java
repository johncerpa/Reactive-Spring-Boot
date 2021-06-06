package com.john.stockclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class RSocketStockClientIntegrationTest {
    @Autowired
    private RSocketRequester.Builder builder;

    private RSocketRequester createRSocketRequester() {
        return builder.connectTcp("localhost", 7000).block();
    }

    @Test
    void shouldRetrieveStockPricesFromTheService() {
        // given
        RSocketStockClient rSocketStockClient = new RSocketStockClient(createRSocketRequester());
        final String symbol = "SYMBOL";
        final int amount = 5;

        // when
        Flux<StockPrice> prices = rSocketStockClient.pricesFor(symbol);

        // then
        Assertions.assertNotNull(prices);
        Flux<StockPrice> fivePrices = prices.take(amount);
        Assertions.assertEquals(amount, fivePrices.count().block());
        Assertions.assertEquals(symbol, fivePrices.blockFirst().getSymbol());

        StepVerifier.create(prices.take(2))
                    .expectNextMatches(stockPrice -> stockPrice.getSymbol().equals("SYMBOL"))
                    .expectNextMatches(stockPrice -> stockPrice.getSymbol().equals("SYMBOL"))
                    .verifyComplete();
    }
}
