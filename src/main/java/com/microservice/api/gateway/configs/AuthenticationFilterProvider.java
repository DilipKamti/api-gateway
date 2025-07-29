package com.microservice.api.gateway.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationFilterProvider {
    @Autowired
    private final JwtAuthenticationFilter jwtFilter;

    public AuthenticationFilterProvider(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    public GatewayFilter apply() {
        log.debug("Applying authentication filter");
        return jwtFilter;
    }
}



