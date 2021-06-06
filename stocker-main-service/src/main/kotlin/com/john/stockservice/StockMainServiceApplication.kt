package com.john.stockservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.concurrent.ThreadLocalRandom

@SpringBootApplication
class StockMainServiceApplication

fun main(args: Array<String>) {
	runApplication<StockMainServiceApplication>(*args)
}

@RestController
class RestController(val priceService: PriceService) {
	@GetMapping(value = ["/stocks/{symbol}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun prices(@PathVariable symbol: String) = priceService.generatePrices(symbol)
}

@Controller
class RSocketController(val priceService: PriceService) {
	fun prices(symbol: String) = priceService.generatePrices(symbol)
}

@Service
class PriceService {
	@MessageMapping("stockPrices")
	fun generatePrices(symbol: String): Flux<StockPrice> {
		return Flux.interval(Duration.ofSeconds(1))
				.map { StockPrice(symbol, randomStockPrice(), now()) }
	}

	private fun randomStockPrice(): Double = ThreadLocalRandom.current().nextDouble(100.0)
}

data class StockPrice (val symbol: String, val price: Double, val time: LocalDateTime)