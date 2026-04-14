package com.platform.notification.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.notification.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<Notification> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserIdAndReadAtIsNull(UUID userId);
}