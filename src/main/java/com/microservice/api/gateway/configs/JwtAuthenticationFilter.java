package com.microservice.api.gateway.configs;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpResponse;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Intercepted request: {} {}", request.getMethod(), request.getURI());

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing Authorization header");
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header format");
            return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = validateToken(token);
            log.info("JWT validated. Subject: {}, Role: {}", claims.getSubject(), claims.get("role", String.class));

            request = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-Role", claims.get("role", String.class))
                    .build();
            exchange = exchange.mutate().request(request).build();
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        log.warn("Authentication failed: {}", error);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(error.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
