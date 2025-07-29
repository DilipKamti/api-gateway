package com.microservice.api.gateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/product")
    public ResponseEntity<String> productFallback() {
        log.warn("Fallback triggered for Product Service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Product service is unavailable. Please try again later.");
    }

    @GetMapping("/order")
    public ResponseEntity<String> orderFallback() {
        log.warn("Fallback triggered for Order Service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order service is unavailable. Please try again later.");
    }

    @GetMapping("/inventory")
    public ResponseEntity<String> inventoryFallback() {
        log.warn("Fallback triggered for Inventory Service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Inventory service is unavailable. Please try again later.");
    }

    @GetMapping("/user")
    public ResponseEntity<String> userFallback() {
        log.warn("Fallback triggered for User Management Service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is unavailable. Please try again later.");
    }
}
