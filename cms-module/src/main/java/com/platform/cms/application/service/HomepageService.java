package com.platform.cms.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.exception.BusinessException;
import com.platform.cms.application.dto.HomepageSectionRequest;
import com.platform.cms.application.dto.HomepageSectionResponse;
import com.platform.cms.application.mapper.CmsMapper;
import com.platform.cms.domain.model.HomepageSection;
import com.platform.cms.domain.model.HomepageSectionItem;
import com.platform.cms.domain.repository.HomepageSectionRepository;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomepageService {

    private final HomepageSectionRepository sectionRepository;
    private final CmsMapper mapper;
    private final RestClientFactory restClientFactory;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    @Value("${module.promotion.url:http://localhost:8080}")
    private String promotionBaseUrl;

    private ResilientRestClient catalogRestClient;
    private ResilientRestClient promotionRestClient;

    @PostConstruct
    public void init() {
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);
        promotionRestClient = restClientFactory.create(promotionBaseUrl, 10);
    }

    // ========== DTOs for REST responses ==========
    private record CatalogProductInfo(UUID productId, String name, String slug, String imageUrl, BigDecimal effectivePrice) {}
    private record CatalogCategoryInfo(UUID categoryId, String name, String slug, String imageUrl) {}
    private record PromotionFlashSaleInfo(String id, String name) {}

    @Transactional
    @CacheEvict(value = "homepage", allEntries = true)
    public HomepageSectionResponse createSection(HomepageSectionRequest request) {
        HomepageSection section = HomepageSection.builder()
                .title(request.getTitle())
                .sectionType(request.getSectionType())
                .sectionKey(request.getSectionKey())
                .subtitle(request.getSubtitle())
                .backgroundColor(request.getBackgroundColor())
                .textColor(request.getTextColor())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .maxItems(request.getMaxItems())
                .layout(request.getLayout())
                .configuration(request.getConfiguration())
                .deviceVisibility(request.getDeviceVisibility())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        if (request.getItems() != null) {
            for (var itemReq : request.getItems()) {
                HomepageSectionItem item = HomepageSectionItem.builder()
                        .itemType(itemReq.getItemType())
                        .itemId(itemReq.getItemId() != null ? UUID.fromString(itemReq.getItemId()) : null)
                        .customTitle(itemReq.getCustomTitle())
                        .customImageUrl(itemReq.getCustomImageUrl())
                        .customLinkUrl(itemReq.getCustomLinkUrl())
                        .itemOrder(itemReq.getItemOrder() != null ? itemReq.getItemOrder() : 0)
                        .build();
                section.addItem(item);
            }
        }

        section = sectionRepository.save(section);
        return enrichSectionResponse(mapper.toResponse(section));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "homepage", key = "'sections'")
    public List<HomepageSectionResponse> getActiveSections() {
        List<HomepageSection> sections = sectionRepository
                .findByVisibleTrueAndDeletedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        Instant.now(), Instant.now());
        return sections.stream()
                .map(mapper::toResponse)
                .map(this::enrichSectionResponse)
                .collect(Collectors.toList());
    }

    private HomepageSectionResponse enrichSectionResponse(HomepageSectionResponse response) {
        // Enrich items with actual data from other modules via REST
        for (var item : response.getItems()) {
            switch (item.getItemType()) {
                case "PRODUCT":
                    try {
                        CatalogProductInfo product = catalogRestClient.get(
                                "/api/v1/products/{productId}/info",
                                CatalogProductInfo.class,
                                UUID.fromString(item.getItemId())
                        );
                        if (product != null) {
                            item.setEnrichedTitle(product.name());
                            item.setEnrichedImageUrl(product.imageUrl());
                            item.setEnrichedLinkUrl("/product/" + product.slug());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch product info for item {}: {}", item.getItemId(), e.getMessage());
                    }
                    break;
                case "CATEGORY":
                    try {
                        CatalogCategoryInfo category = catalogRestClient.get(
                                "/api/v1/categories/{categoryId}/info",
                                CatalogCategoryInfo.class,
                                UUID.fromString(item.getItemId())
                        );
                        if (category != null) {
                            item.setEnrichedTitle(category.name());
                            item.setEnrichedImageUrl(category.imageUrl());
                            item.setEnrichedLinkUrl("/category/" + category.slug());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch category info for item {}: {}", item.getItemId(), e.getMessage());
                    }
                    break;
                case "FLASH_SALE":
                    try {
                        PromotionFlashSaleInfo flashSale = promotionRestClient.get(
                                "/api/v1/flash-sales/{flashSaleId}/info",
                                PromotionFlashSaleInfo.class,
                                item.getItemId()
                        );
                        if (flashSale != null) {
                            item.setEnrichedTitle(flashSale.name());
                            item.setEnrichedLinkUrl("/flash-sale/" + flashSale.id());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch flash sale info for item {}: {}", item.getItemId(), e.getMessage());
                    }
                    break;
                case "CUSTOM":
                    item.setEnrichedTitle(item.getCustomTitle());
                    item.setEnrichedImageUrl(item.getCustomImageUrl());
                    item.setEnrichedLinkUrl(item.getCustomLinkUrl());
                    break;
            }
        }
        return response;
    }
}