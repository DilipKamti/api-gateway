package com.microservice.api.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based route configuration for Spring Cloud Gateway with Circuit Breakers.
 */
@Configuration
public class Routes {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Product Service with Circuit Breaker
                .route("product-service", r -> r
                        .path("/api/v1/products/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("productCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/product")
                                )
                        )
                        .uri("lb://PRODUCT-SERVICE"))

                // Order Service with Circuit Breaker
                .route("order-service", r -> r
                        .path("/api/v1/order/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("orderCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/order")
                                )
                        )
                        .uri("lb://ORDER-SERVICE"))

                // Inventory Service with Circuit Breaker
                .route("inventory-service", r -> r
                        .path("/api/v1/inventory/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("inventoryCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/inventory")
                                )
                        )
                        .uri("lb://INVENTORY-SERVICE"))

                .build();
    }
}
