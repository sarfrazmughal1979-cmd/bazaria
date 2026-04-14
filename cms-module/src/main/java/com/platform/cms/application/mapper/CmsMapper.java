package com.platform.cms.application.mapper;

import com.platform.cms.application.dto.*;
import com.platform.cms.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CmsMapper {

    // Banner mappings
    public BannerResponse toResponse(Banner banner) {
        if (banner == null) return null;

        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .position(banner.getPosition())
                .sortOrder(banner.getSortOrder())
                .active(banner.isActive())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .createdAt(banner.getCreatedAt())
                .build();
    }

    public Banner toEntity(BannerRequest request) {
        if (request == null) return null;

        return Banner.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .position(request.getPosition())
                .sortOrder(request.getSortOrder())
                .active(request.getActive() != null ? request.getActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public HomepageSectionResponse toResponse(HomepageSection section) {
        if (section == null) return null;
        return HomepageSectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .sectionType(section.getSectionType())
                .sectionKey(section.getSectionKey())
                .subtitle(section.getSubtitle())
                .backgroundColor(section.getBackgroundColor())
                .textColor(section.getTextColor())
                .sortOrder(section.getSortOrder())
                .visible(section.isVisible())
                .maxItems(section.getMaxItems())
                .layout(section.getLayout())
                .configuration(section.getConfiguration())
                .deviceVisibility(section.getDeviceVisibility())
                .startDate(section.getStartDate())
                .endDate(section.getEndDate())
                .items(toItemResponse(section.getItems()))
                .build();
    }

    private List<HomepageSectionResponse.HomepageSectionItemResponse> toItemResponse(List<HomepageSectionItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(item -> HomepageSectionResponse.HomepageSectionItemResponse.builder()
                        .id(item.getId())
                        .itemType(item.getItemType())
                        .itemId(item.getItemId() != null ? item.getItemId().toString() : null)
                        .customTitle(item.getCustomTitle())
                        .customImageUrl(item.getCustomImageUrl())
                        .customLinkUrl(item.getCustomLinkUrl())
                        .itemOrder(item.getItemOrder())
                        .build())
                .collect(Collectors.toList());
    }
    // Page mappings
    public PageResponse toResponse(Page page) {
        if (page == null) return null;

        return PageResponse.builder()
                .id(page.getId())
                .title(page.getTitle())
                .slug(page.getSlug())
                .content(page.getContent())
                .seoMetadata(page.getSeoMetadata())
                .isPublished(page.isPublished())
                .publishedAt(page.getPublishedAt())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }

    public Page toEntity(PageRequest request) {
        if (request == null) return null;

        return Page.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .content(request.getContent())
                .seoMetadata(request.getSeoMetadata())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .build();
    }

    // ========== FAQ Mappings ==========
    public FAQResponse toResponse(FAQ faq) {
        if (faq == null) return null;
        return FAQResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .sortOrder(faq.getSortOrder())
                .createdAt(faq.getCreatedAt())
                .build();
    }

    public FAQ toEntity(FAQRequest request) {
        if (request == null) return null;
        return FAQ.builder()
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .category(request.getCategory())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
    }

    // ========== Announcement Mappings ==========
    public AnnouncementResponse toResponse(Announcement announcement) {
        if (announcement == null) return null;
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .message(announcement.getMessage())
                .linkUrl(announcement.getLinkUrl())
                .active(announcement.isActive())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    public Announcement toEntity(AnnouncementRequest request) {
        if (request == null) return null;
        return Announcement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .linkUrl(request.getLinkUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }
}