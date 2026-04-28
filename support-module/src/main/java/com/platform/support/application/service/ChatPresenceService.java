package com.platform.support.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatPresenceService {
    private final RestClientFactory restClientFactory;
    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;
    private ResilientRestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    public boolean isUserOnline(UUID userId) {
        // Call IAM module endpoint to check user presence
        return iamRestClient.get("/api/v1/users/{userId}/presence", Boolean.class, userId);
    }

    public String getUserSessionId(UUID userId) {
        return iamRestClient.get("/api/v1/users/{userId}/session-id", String.class, userId);
    }
}