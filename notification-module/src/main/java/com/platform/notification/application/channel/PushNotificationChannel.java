package com.platform.notification.application.channel;

import com.platform.core.cloud.push.FcmPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationChannel implements NotificationChannelAdapter {

    private final FcmPushService fcmPushService;

    @Override
    public boolean send(UUID userId, String subject, String body) {
        try {
            // In real implementation, fetch FCM token for the user
            fcmPushService.sendToUser(userId.toString(), subject, body);
            log.info("Push notification sent to user {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Failed to send push notification to user {}", userId, e);
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "PUSH";
    }
}