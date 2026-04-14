package com.platform.cms.application.service;

import com.platform.cms.application.dto.BannerRequest;
import com.platform.cms.application.dto.BannerResponse;
import com.platform.cms.application.mapper.CmsMapper;
import com.platform.cms.domain.event.BannerPublishedEvent;
import com.platform.cms.domain.model.Banner;
import com.platform.cms.domain.repository.BannerRepository;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
public class BannerService {

    private final BannerRepository bannerRepository;
    private final CmsMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public BannerResponse createBanner(BannerRequest request) {
        Banner banner = mapper.toEntity(request);
        banner = bannerRepository.save(banner);
        log.info("Banner created: {}", banner.getId());
        return mapper.toResponse(banner);
    }

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public BannerResponse updateBanner(UUID id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setMobileImageUrl(request.getMobileImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setLinkType(request.getLinkType());
        banner.setLinkValue(request.getLinkValue());
        banner.setPosition(request.getPosition());
        if (request.getSortOrder() != null) banner.setSortOrder(request.getSortOrder());
        if (request.getActive() != null) banner.setActive(request.getActive());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setTargetAudience(request.getTargetAudience());

        banner = bannerRepository.save(banner);

        if (banner.isActive() && banner.isCurrentlyActive()) {
            eventPublisher.publish(new BannerPublishedEvent(banner.getId().toString(), banner.getPosition()));
        }

        return mapper.toResponse(banner);
    }

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public void deleteBanner(UUID id) {
        bannerRepository.softDelete(id);
        log.info("Banner deleted: {}", id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "banners", key = "'active'")
    public List<BannerResponse> getActiveBanners() {
        return bannerRepository.findActiveBanners(Instant.now()).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "banners", key = "'position:' + #position")
    public List<BannerResponse> getBannersByPosition(String position) {
        return bannerRepository.findActiveBannersByPosition(position, Instant.now()).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResponse<BannerResponse> getAllBanners(Pageable pageable) {
        Page<Banner> page = bannerRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional
    public void trackClick(UUID bannerId) {
        bannerRepository.findById(bannerId).ifPresent(banner -> {
            banner.incrementClick();
            bannerRepository.save(banner);
        });
    }

    @Transactional
    public void trackImpression(UUID bannerId) {
        bannerRepository.findById(bannerId).ifPresent(banner -> {
            banner.incrementImpression();
            bannerRepository.save(banner);
        });
    }
}