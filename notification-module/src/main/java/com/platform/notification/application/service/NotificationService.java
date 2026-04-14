package com.platform.notification.application.service;

import com.platform.core.exception.BusinessException;
import com.platform.notification.application.channel.NotificationChannelFactory;
import com.platform.notification.domain.model.Notification;
import com.platform.notification.domain.model.NotificationChannel;
import com.platform.notification.domain.model.NotificationStatus;
import com.platform.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationChannelFactory channelFactory;
    private final TemplateService templateService;

    @Transactional
    public void send(UUID userId, String templateKey, Map<String, Object> templateData, NotificationChannel... channels) {
        String subject = templateService.getSubject(templateKey);
        String body = templateService.render(templateKey, templateData);

        for (NotificationChannel channel : channels) {
            try {
                var adapter = channelFactory.getAdapter(channel);
                boolean sent = adapter.send(userId, subject, body);

                Notification notification = Notification.builder()
                        .userId(userId)
                        .title(subject)
                        .message(body)
                        .type(templateKey)
                        .channel(channel.name())
                        .status(sent ? NotificationStatus.SENT.name() : NotificationStatus.FAILED.name())
                        .metadata(mapToJson(templateData))
                        .build();
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Failed to send notification via {} to user {}", channel, userId, e);
                // Save failed notification
                Notification failed = Notification.builder()
                        .userId(userId)
                        .title(subject)
                        .message(body)
                        .type(templateKey)
                        .channel(channel.name())
                        .status(NotificationStatus.FAILED.name())
                        .build();
                notificationRepository.save(failed);
            }
        }
    }

    private String mapToJson(Map<String, Object> data) {
        // Simple placeholder – use Jackson in real implementation
        return data != null ? data.toString() : null;
    }
}