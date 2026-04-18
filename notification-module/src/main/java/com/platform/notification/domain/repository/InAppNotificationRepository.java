package com.platform.notification.domain.repository;

import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface InAppNotificationRepository {
    void save(UUID userId, String title, String message);
}