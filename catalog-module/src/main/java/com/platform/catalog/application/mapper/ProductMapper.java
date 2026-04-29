package com.platform.catalog.application.mapper;

import com.platform.catalog.application.dto.CreateProductRequest;
import com.platform.catalog.application.dto.ProductDetailResponse;
import com.platform.common.application.dto.ProductResponse;
import com.platform.catalog.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    // ============================================================
    // Entity to Response DTOs (manual mapping â€“ no MapStruct errors)
    // ============================================================

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .shortDescription(product.getShortDescription())
                .basePrice(product.getBasePrice() != null ? product.getBasePrice().getAmount() : null)
                .salePrice(product.getSalePrice() != null ? product.getSalePrice().getAmount() : null)
                .effectivePrice(product.getEffectivePrice().getAmount())
                .discountPercentage(product.getDiscountPercentage())
                .currency(product.getBasePrice() != null ? product.getBasePrice().getCurrencyCode() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .primaryImage(getPrimaryImageUrl(product.getImages()))
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .featured(product.isFeatured())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductDetailResponse toDetailResponse(Product product) {
        if (product == null) return null;

        return ProductDetailResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .basePrice(product.getBasePrice() != null ? product.getBasePrice().getAmount() : null)
                .salePrice(product.getSalePrice() != null ? product.getSalePrice().getAmount() : null)
                .effectivePrice(product.getEffectivePrice().getAmount())
                .discountPercentage(product.getDiscountPercentage())
                .currency(product.getBasePrice() != null ? product.getBasePrice().getCurrencyCode() : null)
                .category(mapCategory(product.getCategory()))
                .brand(mapBrand(product.getBrand()))
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .featured(product.isFeatured())
                .digital(product.isDigital())
                .taxClass(product.getTaxClass())
                .weight(product.getWeight())
                .weightUnit(product.getWeightUnit())
                .minOrderQuantity(product.getMinOrderQuantity())
                .maxOrderQuantity(product.getMaxOrderQuantity())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .viewCount(product.getViewCount())
                .soldCount(product.getSoldCount())
                .variants(mapVariants(product.getVariants()))
                .images(mapImages(product.getImages()))
                .attributes(mapAttributes(product.getAttributes()))
                .seoMetadata(product.getSeoMetadata())
                .vendorId(product.getVendorId() != null ? product.getVendorId().toString() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    // ============================================================
    // Helper methods for nested objects
    // ============================================================

    private String getPrimaryImageUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(images.get(0).getUrl());
    }

    public ProductDetailResponse.CategoryInfo mapCategory(Category category) {
        if (category == null) return null;
        return ProductDetailResponse.CategoryInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .build();
    }

    public ProductDetailResponse.BrandInfo mapBrand(Brand brand) {
        if (brand == null) return null;
        return ProductDetailResponse.BrandInfo.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .build();
    }

    public List<ProductDetailResponse.ProductVariantResponse> mapVariants(List<ProductVariant> variants) {
        if (variants == null) return List.of();
        return variants.stream()
                .map(v -> ProductDetailResponse.ProductVariantResponse.builder()
                        .id(v.getId())
                        .sku(v.getSku())
                        .name(v.getName())
                        .price(v.getPrice() != null ? v.getPrice().getAmount() : null)
                        .currency(v.getPrice() != null ? v.getPrice().getCurrencyCode() : null)
                        .imageUrl(v.getImageUrl())
                        .active(v.isActive())
                        .attributes(v.getAttributes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductDetailResponse.ProductImageResponse> mapImages(List<ProductImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(i -> ProductDetailResponse.ProductImageResponse.builder()
                        .id(i.getId())
                        .url(i.getUrl())
                        .altText(i.getAltText())
                        .sortOrder(i.getSortOrder())
                        .primary(i.isPrimary())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductDetailResponse.ProductAttributeResponse> mapAttributes(List<ProductAttribute> attributes) {
        if (attributes == null) return List.of();
        return attributes.stream()
                .map(a -> ProductDetailResponse.ProductAttributeResponse.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .value(a.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    // ============================================================
    // Request DTO to Entity (manual mapping)
    // ============================================================

    public Product toEntity(CreateProductRequest request) {
        if (request == null) return null;

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .minOrderQuantity(request.getMinOrderQuantity() != null ? request.getMinOrderQuantity() : 1)
                .maxOrderQuantity(request.getMaxOrderQuantity())
                .featured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .digital(request.getIsDigital() != null ? request.getIsDigital() : false)
                .taxClass(request.getTaxClass())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit() != null ? request.getWeightUnit() : "kg")
                .build();

        // Set Money objects
        if (request.getBasePrice() != null) {
            product.setBasePrice(com.platform.core.domain.Money.of(
                    request.getBasePrice(),
                    request.getCurrency() != null ? request.getCurrency() : "PKR"));
        }
        if (request.getSalePrice() != null) {
            product.setSalePrice(com.platform.core.domain.Money.of(
                    request.getSalePrice(),
                    request.getCurrency() != null ? request.getCurrency() : "PKR"));
        }

        return product;
    }
}