package com.platform.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.decorators.Decorators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class ResilientRestClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ResilientRestClient(String baseUrl, int timeoutSeconds, String idempotencyKey, String apiKey,
                               ObjectMapper objectMapper,
                               CircuitBreakerRegistry circuitBreakerRegistry,
                               String serviceName) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Internal-API-Key", apiKey)
                .defaultHeader("Idempotency-Key", idempotencyKey)
                .build();
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
        this.retry = Retry.of(serviceName + "-retry", RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnException(e -> e instanceof org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable)
                .build());
    }

    public <T> T get(String path, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.get()
                .uri(path, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType));
    }

    public <T, R> T post(String path, R requestBody, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.post()
                .uri(path, uriVariables)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType));
    }

    public <T, R> T put(String path, R requestBody, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.put()
                .uri(path, uriVariables)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType));
    }

    public void delete(String path, Object... uriVariables) {
        execute(() -> webClient.delete()
                .uri(path, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(Void.class));
    }

    private <T> T execute(Supplier<Mono<T>> supplier) {
        Supplier<T> decoratedSupplier = Decorators.ofSupplier(() -> supplier.get().block())
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .decorate();
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("REST call failed after retries and circuit breaker", e);
            throw new BusinessException("REST_CALL_FAILED", e.getMessage());
        }
    }

    private Mono<? extends Throwable> handleError(org.springframework.web.reactive.function.client.ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    log.error("REST error: status={}, body={}", response.statusCode(), body);
                    return Mono.error(new BusinessException(
                            "REST_ERROR_" + response.statusCode().value(),
                            "Remote service returned " + response.statusCode()
                    ));
                });
    }
}
