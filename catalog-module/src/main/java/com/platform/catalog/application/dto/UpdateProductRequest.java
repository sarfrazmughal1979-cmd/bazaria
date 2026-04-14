package com.platform.catalog.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(min = 3, max = 500, message = "Product name must be between 3 and 500 characters")
    private String name;

    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    private String shortDescription;

    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Base price cannot exceed 9,999,999.99")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0", message = "Sale price must be greater than or equal to 0")
    @DecimalMax(value = "9999999.99", message = "Sale price cannot exceed 9,999,999.99")
    private BigDecimal salePrice;

    private UUID categoryId;

    private UUID brandId;

    @DecimalMin(value = "0.0", message = "Weight must be positive")
    private BigDecimal weight;

    private String weightUnit;

    private Boolean isFeatured;

    private Boolean isDigital;

    private String taxClass;

    private Integer minOrderQuantity;

    private Integer maxOrderQuantity;

    private List<CreateProductRequest.ProductVariantDto> variants;

    private List<CreateProductRequest.ProductImageDto> images;

    private List<CreateProductRequest.ProductAttributeDto> attributes;

    private CreateProductRequest.SEOMetadataDto seoMetadata;
}