package com.platform.notification.infrastructure.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSnsSmsSender {
    private final SnsClient snsClient;

    public void send(String phoneNumber, String message) {
        PublishRequest request = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .build();
        snsClient.publish(request);
        log.info("SMS sent to {}", phoneNumber);
    }
}
