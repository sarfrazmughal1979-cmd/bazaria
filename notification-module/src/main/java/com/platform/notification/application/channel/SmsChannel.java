package com.platform.notification.application.channel;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.cloud.messaging.SnsSmsSender;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsChannel implements NotificationChannelAdapter {

    private final SnsSmsSender smsSender;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    private ResilientRestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    @Override
    public boolean send(UUID userId, String subject, String body) {
        try {
            // Fetch user phone number via REST call to IAM module
            String phone = iamRestClient.get(
                    "/api/v1/users/{userId}/phone", String.class, userId);
            if (phone == null) {
                log.warn("No phone number found for user {}", userId);
                return false;
            }
            // Truncate body for SMS if needed (max 160 chars)
            String smsBody = body.length()  > 160 ? body.substring(0, 157) + "..." : body;
            smsSender.send(phone, smsBody);
            log.info("SMS sent to {}: {}", phone, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to user {}", userId, e);
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}