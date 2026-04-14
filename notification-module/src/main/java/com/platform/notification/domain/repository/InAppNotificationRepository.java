package com.platform.notification.domain.repository;

import java.util.UUID;

public interface InAppNotificationRepository {
    void save(UUID userId, String title, String message);
}