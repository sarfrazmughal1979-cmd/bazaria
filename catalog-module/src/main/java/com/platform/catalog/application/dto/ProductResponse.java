package com.platform.catalog.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID productId;
    private String name;
    private String slug;
    private String sku;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal effectivePrice;
    private BigDecimal discountPercentage;
    private String currency;

    private UUID categoryId;
    private String categoryName;
    private UUID brandId;
    private String brandName;
    private String primaryImage;

    private String status;
    private boolean featured;
    private BigDecimal averageRating;
    private int reviewCount;

    private Instant createdAt;
    private Instant updatedAt;
}