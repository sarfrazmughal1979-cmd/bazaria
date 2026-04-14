package com.platform.notification.application.channel;

import com.platform.core.client.RestClient;
import com.platform.core.client.RestClientFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannel implements NotificationChannelAdapter {

    private final JavaMailSender mailSender;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    private RestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    @Override
    public boolean send(UUID userId, String subject, String body) {
        try {
            // Fetch user email via REST call to IAM module
            String email = iamRestClient.get(
                    "/api/v1/users/{userId}/email", String.class, userId);
            if (email == null) {
                log.warn("No email found for user {}", userId);
                return false;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}: {}", email, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to user {}", userId, e);
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "EMAIL";
    }
}