package com.microservice.api.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based route configuration for Spring Cloud Gateway.
 * These routes use lb:// to load balance via Eureka.
 */
@Configuration
public class Routes {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Product Service - routed via Eureka using load balancer
                .route("product-service", r -> r
                        .path("/api/v1/product/**")
                        .uri("lb://PRODUCT-SERVICE"))

                // Order Service
                .route("order-service", r -> r
                        .path("/api/v1/order/**")
                        .uri("lb://ORDER-SERVICE"))

                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/v1/inventory/**")
                        .uri("lb://INVENTORY-SERVICE"))

                .build();
    }
}
