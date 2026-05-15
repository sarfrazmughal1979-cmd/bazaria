package com.platform.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final ObjectMapper objectMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    @Value("${internal.api.key:Mughal12}")
    private String internalApiKey;

    public ResilientRestClient create(String baseUrl, int timeoutSeconds) {
        return new ResilientRestClient(baseUrl, timeoutSeconds, internalApiKey, objectMapper,
                circuitBreakerRegistry, baseUrl.replaceAll("[^a-zA-Z0-9]", "_"));
    }
}