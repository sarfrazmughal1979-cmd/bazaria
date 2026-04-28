package com.platform.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final ObjectMapper objectMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilientRestClient create(String baseUrl, int timeoutSeconds) {
        return new ResilientRestClient(baseUrl, timeoutSeconds, objectMapper,
                circuitBreakerRegistry, baseUrl.replaceAll("[^a-zA-Z0-9]", "_"));
    }
}