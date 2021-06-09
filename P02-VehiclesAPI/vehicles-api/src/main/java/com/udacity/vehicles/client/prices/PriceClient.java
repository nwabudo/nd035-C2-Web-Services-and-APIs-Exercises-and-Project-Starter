package com.udacity.vehicles.client.prices;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@Component
public class PriceClient {

    private static final Logger log = LoggerFactory.getLogger(PriceClient.class);

    private final Cache<Long, Price>
            PRICE_CACHE = Caffeine.newBuilder()
                                  .expireAfterWrite(Duration.ofMinutes(5))
                                  .maximumSize(1_000)
                                  .build();

    private final WebClient client;

    public PriceClient(WebClient pricing) {
        this.client = pricing;
    }

    // In a real-world application we'll want to add some resilience
    // to this method with retries/CB/failover capabilities
    // We may also want to cache the results so we don't need to
    // do a request every time
    /**
     * Gets a vehicle price from the pricing client, given vehicle ID.
     * @param vehicleId ID number of the vehicle for which to get the price
     * @return Currency and price of the requested vehicle,
     *   error message that the vehicle ID is invalid, or note that the
     *   service is down.
     */
    public String getPrice(Long vehicleId) {
        Price price;
        try {
            Optional<Price> priceOptional = Optional.ofNullable(PRICE_CACHE.getIfPresent(vehicleId));
            if(!priceOptional.isPresent()){
                price = this.client
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/prices/search/byVehicleId")
                                .queryParam("vehicleId", vehicleId)
                                .build()
                        )
                        .retrieve().bodyToMono(Price.class)
                        .retryWhen(Retry.backoff(2, Duration.ofMillis(25))
                                        .filter(throwable -> throwable instanceof TimeoutException))
                        .doOnNext(p -> PRICE_CACHE.put(vehicleId, p))
                        .block();
            }else price = priceOptional.get();

            return String.format("%s %s", price.getCurrency(), price.getPrice());

        } catch (Exception e) {
            log.error("Unexpected error retrieving price for vehicle with id {}: {}", vehicleId, e);
        }
        return "(consult price)";
    }

    public void assignRandomPrice(Long vehicleId) {
        Price price;
        try {
            Optional<Price> priceOptional = Optional.ofNullable(PRICE_CACHE.getIfPresent(vehicleId));
            if(priceOptional.isPresent()) {
                price = priceOptional.get();
                price.setPrice(randomPrice());

                price = this.client
                        .post()
                        .uri("/prices")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(price), Price.class)
                        .retrieve()
                        .bodyToMono(Price.class)
                        .doOnNext(p -> PRICE_CACHE.put(vehicleId, p))
                        .block();
            }
        } catch (Exception e) {
            log.error("Unexpected error saving/retrieving price for vehicle with id {}: {}", vehicleId, e);
        }
    }

    private static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }
}
