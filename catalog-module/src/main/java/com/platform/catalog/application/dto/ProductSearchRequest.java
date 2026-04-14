package com.platform.catalog.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    private String keyword;

    private UUID categoryId;

    private UUID brandId;

    private UUID vendorId;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private BigDecimal minRating;   // e.g., 4.0

    private String sortBy;          // price, rating, newest, popularity

    private String sortDirection;   // asc, desc

    private Boolean inStock;        // filter by available inventory

    private Boolean isFeatured;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;
}