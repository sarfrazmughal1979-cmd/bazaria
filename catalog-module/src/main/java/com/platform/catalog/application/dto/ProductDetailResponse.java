package com.platform.catalog.application.dto;

import com.platform.catalog.domain.model.Category;
import com.platform.catalog.domain.model.Brand;
import com.platform.catalog.domain.model.SEOMetadata;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private UUID productId;
    private String name;
    private String slug;
    private String sku;
    private String shortDescription;
    private String description;

    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal effectivePrice;
    private BigDecimal discountPercentage;
    private String currency;

    private CategoryInfo category;
    private BrandInfo brand;

    private String status;
    private boolean featured;
    private boolean digital;
    private String taxClass;

    private BigDecimal weight;
    private String weightUnit;

    private int minOrderQuantity;
    private Integer maxOrderQuantity;

    private BigDecimal averageRating;
    private int reviewCount;
    private long viewCount;
    private long soldCount;

    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
    private List<ProductAttributeResponse> attributes;
    private SEOMetadata seoMetadata;

    private String vendorId;
    private String vendorName;

    private Instant createdAt;
    private Instant updatedAt;

    // Nested DTOs
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private UUID id;
        private String name;
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {
        private UUID id;
        private String name;
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantResponse {
        private UUID id;
        private String sku;
        private String name;
        private BigDecimal price;
        private String currency;
        private String imageUrl;
        private boolean active;
        private java.util.Map<String, String> attributes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageResponse {
        private UUID id;
        private String url;
        private String altText;
        private int sortOrder;
        private boolean primary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeResponse {
        private UUID id;
        private String name;
        private String value;
    }
}