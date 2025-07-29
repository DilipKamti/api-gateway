package com.microservice.api.gateway.routes;

import com.microservice.api.gateway.configs.AuthenticationFilterProvider;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based route configuration for Spring Cloud Gateway with Circuit Breakers and Authentication.
 */
@Slf4j
@Configuration
public class Routes {

    private AuthenticationFilterProvider authenticationFilterFactory;

    public Routes(AuthenticationFilterProvider authenticationFilterFactory) {
        this.authenticationFilterFactory = authenticationFilterFactory;
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        log.info("Setting up custom routes");

        return builder.routes()

            .route("product-service", r -> r
                .path("/api/v1/products/**")
                .filters(f -> f
                    .filter(authenticationFilterFactory.apply())
                    .circuitBreaker(c -> c
                        .setName("productCircuitBreaker")
                        .setFallbackUri("forward:/fallback/product")
                    )
                )
                .uri("lb://PRODUCT-SERVICE")
            )

            .route("order-service", r -> r
                .path("/api/v1/order/**")
                .filters(f -> f
                    .filter(authenticationFilterFactory.apply())
                    .circuitBreaker(c -> c
                        .setName("orderCircuitBreaker")
                        .setFallbackUri("forward:/fallback/order")
                    )
                )
                .uri("lb://ORDER-SERVICE")
            )

            .route("inventory-service", r -> r
                .path("/api/v1/inventory/**")
                .filters(f -> f
                    .filter(authenticationFilterFactory.apply())
                    .circuitBreaker(c -> c
                        .setName("inventoryCircuitBreaker")
                        .setFallbackUri("forward:/fallback/inventory")
                    )
                )
                .uri("lb://INVENTORY-SERVICE")
            )

            .route("user-auth-open", r -> r
                .path("/api/v1/auth/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("userAuthOpenCircuitBreaker")
                        .setFallbackUri("forward:/fallback/user")
                    )
                )
                .uri("lb://USER-MANAGEMENT")
            )

            .route("user-management-secured", r -> r
                .path("/api/v1/user/**", "/api/v1/admin/**", "/api/v1/test/**")
                .filters(f -> f
                    .filter(authenticationFilterFactory.apply())
                    .circuitBreaker(c -> c
                        .setName("userSecuredCircuitBreaker")
                        .setFallbackUri("forward:/fallback/user")
                    )
                )
                .uri("lb://USER-MANAGEMENT")
            )

            .build();
    }
}
