package com.platform.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RestClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public RestClient(String baseUrl, int timeoutSeconds, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public <T> T get(String path, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.get()
                .uri(path, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType)
        );
    }

    public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.get()
                .uri(path, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType)
        );
    }

    public <T, R> T post(String path, R requestBody, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.post()
                .uri(path, uriVariables)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType)
        );
    }

    public <T, R> T put(String path, R requestBody, Class<T> responseType, Object... uriVariables) {
        return execute(() -> webClient.put()
                .uri(path, uriVariables)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(responseType)
        );
    }

    public void delete(String path, Object... uriVariables) {
        execute(() -> webClient.delete()
                .uri(path, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(Void.class)
        );
    }

    private <T> T execute(Supplier<Mono<T>> supplier) {
        try {
            return supplier.get()
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .maxBackoff(Duration.ofSeconds(5))
                            .filter(throwable -> throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable))
                    .block();
        } catch (Exception e) {
            log.error("REST call failed", e);
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