package com.platform.notification.application.channel;

import java.util.UUID;

public interface NotificationChannelAdapter {
    boolean send(UUID userId, String subject, String body);
    String getChannelName();
}