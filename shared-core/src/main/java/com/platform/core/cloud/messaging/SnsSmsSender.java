package com.platform.core.cloud.messaging;

import io.awspring.cloud.sns.sms.SnsSmsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsSmsSender {

    private final SnsSmsTemplate snsSmsTemplate;

    public void send(String phoneNumber, String message) {
        try {
            snsSmsTemplate.send(phoneNumber, message);
            log.info("SMS sent successfully to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", phoneNumber, e);
            throw new RuntimeException("SMS sending failed", e);
        }
    }
}