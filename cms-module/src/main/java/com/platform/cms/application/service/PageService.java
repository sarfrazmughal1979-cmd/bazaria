package com.platform.cms.application.service;

import com.platform.cms.application.dto.PageRequest;
import com.platform.cms.application.dto.PageResponse;
import com.platform.cms.application.mapper.CmsMapper;
import com.platform.cms.domain.event.PageUpdatedEvent;
import com.platform.cms.domain.model.Page;
import com.platform.cms.domain.repository.PageRepository;
import com.platform.core.domain.SlugGenerator;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final CmsMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "pages", allEntries = true)
    public PageResponse createPage(PageRequest request) {
        if (pageRepository.existsBySlugAndDeletedFalse(request.getSlug())) {
            throw new BusinessException("DUPLICATE_SLUG", "Page slug already exists");
        }

        Page page = mapper.toEntity(request);
        page.setSlug(SlugGenerator.generate(request.getSlug()));

        if ("PUBLISHED".equals(request.getStatus())) {
            page.publish();
        }

        page = pageRepository.save(page);
        log.info("Page created: {} [{}]", page.getSlug(), page.getId());

        if ("PUBLISHED".equals(page.getStatus())) {
            eventPublisher.publish(new PageUpdatedEvent(page.getId().toString(), page.getSlug()));
        }

        return mapper.toResponse(page);
    }

    @Transactional
    @CacheEvict(value = "pages", allEntries = true)
    public PageResponse updatePage(UUID id, PageRequest request) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Page", "id", id));

        page.setTitle(request.getTitle());
        if (!page.getSlug().equals(request.getSlug())) {
            if (pageRepository.existsBySlugAndDeletedFalse(request.getSlug())) {
                throw new BusinessException("DUPLICATE_SLUG", "Page slug already exists");
            }
            page.setSlug(SlugGenerator.generate(request.getSlug()));
        }
        page.setContent(request.getContent());
        page.setExcerpt(request.getExcerpt());
        page.setFeaturedImage(request.getFeaturedImage());
        page.setSeoMetadata(request.getSeoMetadata());
        page.setShowInFooter(request.getShowInFooter() != null && request.getShowInFooter());
        page.setShowInHeader(request.getShowInHeader() != null && request.getShowInHeader());
        page.setFooterColumn(request.getFooterColumn());
        page.setFooterOrder(request.getFooterOrder());

        if ("PUBLISHED".equals(request.getStatus()) && !"PUBLISHED".equals(page.getStatus())) {
            page.publish();
        }

        page = pageRepository.save(page);

        eventPublisher.publish(new PageUpdatedEvent(page.getId().toString(), page.getSlug()));

        return mapper.toResponse(page);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pages", key = "#slug")
    public PageResponse getPageBySlug(String slug) {
        Page page = pageRepository.findBySlugAndStatus(slug, "PUBLISHED")
            .orElseThrow(() -> new ResourceNotFoundException("Page", "slug", slug));
        page.incrementViewCount();
        pageRepository.save(page);
        return mapper.toResponse(page);
    }

    @Transactional(readOnly = true)
    public PagedResponse<PageResponse> getPublishedPages(Pageable pageable) {
        var page = pageRepository.findByStatusAndDeletedFalse("PUBLISHED", pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional
    @CacheEvict(value = "pages", allEntries = true)
    public void deletePage(UUID id) {
        pageRepository.softDelete(id);
        log.info("Page deleted: {}", id);
    }
}