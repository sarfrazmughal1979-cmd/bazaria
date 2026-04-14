package com.platform.notification.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.notification.domain.model.NotificationTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends BaseRepository<NotificationTemplate> {
    Optional<NotificationTemplate> findByTemplateKey(String templateKey);
}