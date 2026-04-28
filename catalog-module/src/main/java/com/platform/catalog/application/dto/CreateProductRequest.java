package com.platform.catalog.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 500, message = "Product name must be between 3 and 500 characters")
    private String name;

    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    private String shortDescription;

    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Za-z0-9\\-_.]+$", message = "SKU can only contain letters, numbers, hyphens, underscores, and dots")
    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    private String sku;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Base price cannot exceed 9,999,999.99")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0", message = "Sale price must be greater than or equal to 0")
    @DecimalMax(value = "9999999.99", message = "Sale price cannot exceed 9,999,999.99")
    private BigDecimal salePrice;

    @NotNull(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., PKR, USD)")
    private String currency = "PKR";

    private UUID categoryId;

    private UUID brandId;

    @DecimalMin(value = "0.0", message = "Weight must be positive")
    private BigDecimal weight;

    private String weightUnit = "kg";

    private Boolean isFeatured = false;

    private Boolean isDigital = false;

    private String taxClass;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minOrderQuantity = 1;

    @Min(value = 1, message = "Maximum order quantity must be at least 1")
    private Integer maxOrderQuantity;

    @Valid
    private List<ProductVariantDto> variants;

    private List<ProductImageDto> images;

    private List<ProductAttributeDto> attributes;

    private SEOMetadataDto seoMetadata;

    // Nested DTOs
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDto {
        @NotBlank(message = "Variant SKU is required")
        private String sku;

        private String name;

        @NotNull(message = "Variant price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Variant price must be greater than 0")
        private BigDecimal price;

        private String imageUrl;

        private Map<String, String> attributes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageDto {
        @NotBlank(message = "Image URL is required")
        private String url;

        private String altText;

        private Boolean isPrimary = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeDto {
        @NotBlank(message = "Attribute name is required")
        private String name;

        @NotBlank(message = "Attribute value is required")
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SEOMetadataDto {
        private String title;
        private String description;
        private String keywords;
        private String canonicalUrl;
        private String robots;
    }
}