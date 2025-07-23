package com.microservice.api.gateway.configs;

import java.io.IOException;
import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Configuration
public class CircuitBreakerConfigs {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> {
            // Common configuration reused for all breakers
            CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                    .slidingWindowSize(10)
                    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                    .failureRateThreshold(50)
                    .waitDurationInOpenState(Duration.ofSeconds(5))
                    .permittedNumberOfCallsInHalfOpenState(1)
                    .minimumNumberOfCalls(5)
                    .automaticTransitionFromOpenToHalfOpenEnabled(true)
                    .recordExceptions(
        IOException.class,
                        TimeoutException.class,
                        RuntimeException.class,
                        Exception.class,
                        WebClientRequestException.class,
                        WebClientResponseException.class
                    )
                    .ignoreExceptions(IllegalArgumentException.class)
                    .build();

            factory.configure(builder -> builder.circuitBreakerConfig(config), "productCircuitBreaker");
            factory.configure(builder -> builder.circuitBreakerConfig(config), "orderCircuitBreaker");
            factory.configure(builder -> builder.circuitBreakerConfig(config), "inventoryCircuitBreaker");
        };
    }
}
