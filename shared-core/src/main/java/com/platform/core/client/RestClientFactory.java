package com.platform.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final ObjectMapper objectMapper;

    public RestClient create(String baseUrl, int timeoutSeconds) {
        return new RestClient(baseUrl, timeoutSeconds, objectMapper);
    }
}