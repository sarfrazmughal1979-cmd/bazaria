package com.platform.notification.application.channel;

import com.platform.notification.domain.model.Notification;
import com.platform.notification.domain.model.NotificationChannel;
import com.platform.notification.domain.repository.InAppNotificationRepository;
import com.platform.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InAppChannel implements NotificationChannelAdapter {

//    private final InAppNotificationRepository inAppRepository; // repository for storing in-app notifications
    private final NotificationRepository inAppRepository; // repository for storing in-app notifications

    @Override
    public boolean send(UUID userId, String subject, String body) {
        try {
            Notification notification = Notification.builder().userId(userId).title(subject).message(body).type("").channel(NotificationChannel.IN_APP.name())
                    .status("S").build();
//            inAppRepository.save(userId, subject, body);
            inAppRepository.save(notification);
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