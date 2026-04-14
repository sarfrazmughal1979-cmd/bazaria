package com.platform.cms.application.service;

import com.platform.cms.application.dto.AnnouncementRequest;
import com.platform.cms.application.dto.AnnouncementResponse;
import com.platform.cms.application.mapper.CmsMapper;
import com.platform.cms.domain.event.AnnouncementCreatedEvent;
import com.platform.cms.domain.model.Announcement;
import com.platform.cms.domain.repository.AnnouncementRepository;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CmsMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "announcements", allEntries = true)
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Announcement announcement = mapper.toEntity(request);
        announcement = announcementRepository.save(announcement);

        if (announcement.isActive() && announcement.isCurrentlyActive()) {
            eventPublisher.publish(new AnnouncementCreatedEvent(
                announcement.getId().toString(), announcement.getTitle()));
        }

        return mapper.toResponse(announcement);
    }

    @Cacheable(value = "announcements", key = "'active'")
    public List<AnnouncementResponse> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements(Instant.now()).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "announcements", key = "'page:' + #page")
    public List<AnnouncementResponse> getActiveAnnouncementsForPage(String page) {
        return announcementRepository.findActiveAnnouncementsForPage(Instant.now(), page).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    public PagedResponse<AnnouncementResponse> getAllAnnouncements(Pageable pageable) {
        List<Announcement> page = announcementRepository.findActiveAnnouncements(Instant.now());
        List<AnnouncementResponse> announcements = page.stream().map(mapper::toResponse).collect(Collectors.toList());
        Page<AnnouncementResponse> responses = new PageImpl<>(announcements, pageable, announcements.size());
        return PagedResponse.from(responses);
    }

    @Transactional
    @CacheEvict(value = "announcements", allEntries = true)
    public void deleteAnnouncement(UUID id) {
        announcementRepository.softDelete(id);
    }
}