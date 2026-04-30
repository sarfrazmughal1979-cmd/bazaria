package com.platform.search.application.listener;

import com.platform.common.domain.event.*;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.search.application.service.SearchService;
import com.platform.search.domain.model.ProductDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogSyncListener {

    private final SearchService searchService;
    private final RestClientFactory restClientFactory;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    private ResilientRestClient catalogRestClient;

    @PostConstruct
    public void init() {
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);
    }

    @Async
    @EventListener
    public void onProductCreated(ProductCreatedEvent event) {
        reindexProduct(UUID.fromString(event.getProductId()));
    }

    @Async
    @EventListener
    public void onProductApproved(ProductApprovedEvent event) {
        reindexProduct(UUID.fromString(event.getProductId()));
    }

    @Async
    @EventListener
    public void onProductUpdated(ProductUpdatedEvent event) {
        reindexProduct(UUID.fromString(event.getProductId()));
    }

    @Async
    @EventListener
    public void onProductDeleted(ProductDeletedEvent event) {
        searchService.deleteProduct(event.getProductId());
    }

    private void reindexProduct(UUID productId) {
        try {
            CatalogProductInfo info = catalogRestClient.get(
                    "/api/v1/products/{productId}/info", CatalogProductInfo.class, productId);
            if (info == null) return;

            ProductDocument doc = ProductDocument.builder()
                    .id(info.productId().toString())
                    .name(info.name())
                    .slug(info.slug())
                    .sku(info.sku())
                    .description(info.description())
                    .shortDescription(info.shortDescription())
                    .categoryId(info.categoryId() != null ? info.categoryId().toString() : null)
                    .categoryName(info.categoryName())
                    .brandId(info.brandId() != null ? info.brandId().toString() : null)
                    .brandName(info.brandName())
                    .vendorId(info.vendorId() != null ? info.vendorId().toString() : null)
                    .vendorName(info.vendorName())
                    .basePrice(info.basePrice())
                    .effectivePrice(info.effectivePrice())
                    .salePrice(info.salePrice())
                    .currency(info.currency())
                    .averageRating(info.averageRating())
                    .reviewCount(info.reviewCount())
                    .soldCount(info.soldCount())
                    .inStock(info.inStock())
                    .featured(info.featured())
                    .suggest(ProductDocument.createSuggest(info.name()))
                    .primaryImage(info.primaryImage())
                    .build();

            searchService.indexProduct(doc);
            log.info("Indexed product {}", productId);
        } catch (Exception e) {
            log.error("Failed to reindex product {}", productId, e);
        }
    }

    // DTO for catalog response
    private record CatalogProductInfo(
            UUID productId, String name, String slug, String sku,
            String description, String shortDescription,
            UUID categoryId, String categoryName,
            UUID brandId, String brandName,
            UUID vendorId, String vendorName,
            BigDecimal basePrice, BigDecimal effectivePrice, BigDecimal salePrice,
            String currency, BigDecimal averageRating, int reviewCount,
            long soldCount, boolean inStock, boolean featured,
            String primaryImage
    ) {}
}