package com.platform.notification.application.channel;

import com.platform.notification.domain.repository.InAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InAppChannel implements NotificationChannelAdapter {

    private final InAppNotificationRepository inAppRepository; // repository for storing in-app notifications

    @Override
    public boolean send(UUID userId, String subject, String body) {
        try {
            inAppRepository.save(userId, subject, body);
            log.info("In-app notification stored for user {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Failed to store in-app notification for user {}", userId, e);
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "IN_APP";
    }
}