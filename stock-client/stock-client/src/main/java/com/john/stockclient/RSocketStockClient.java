package com.john.stockclient;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

public class RSocketStockClient implements StockClient {
    private final RSocketRequester rSocketRequester;

    public RSocketStockClient(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @Override
    public Flux<StockPrice> pricesFor(String symbol) {
        return rSocketRequester.route("stockPrices")
                .data(symbol)
                .retrieveFlux(StockPrice.class);
    }
}
