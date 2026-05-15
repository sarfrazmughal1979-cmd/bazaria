package com.platform.cart.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartValidationService {

    private final RestClientFactory restClientFactory;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    @Value("${module.inventory.url:http://localhost:8080}")
    private String inventoryBaseUrl;

    private ResilientRestClient catalogRestClient;
    private ResilientRestClient inventoryRestClient;

    @PostConstruct
    public void init() {
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);
        inventoryRestClient = restClientFactory.create(inventoryBaseUrl, 10);
    }

    // DTOs for REST responses
    private record CatalogProductInfo(UUID productId, String name, BigDecimal effectivePrice,
                                      UUID vendorId,  String imageUrl, String sku) {}
    public record ProductValResult(UUID vendorId, BigDecimal price) {}

    public ProductValResult validateAndGetPrice(UUID productId, UUID variantId, int quantity) {
        // 1. Get product info from Catalog module
        CatalogProductInfo productInfo = catalogRestClient.get(
                "/api/v1/products/{productId}/info", CatalogProductInfo.class, productId);
        if (productInfo == null) {
            throw new BusinessException("PRODUCT_NOT_FOUND", "Product not found");
        }

        // 2. Get available stock from Inventory module
        int available = inventoryRestClient.get(
                "/api/internal/inventory/stock?productId={productId}&variantId={variantId}",
                Integer.class, productId, variantId != null ? variantId : "");
        if (available < quantity) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    String.format("Only %d items available", available));
        }

        // 3. Return effective price (variant price if variantId provided)
        if (variantId != null) {
            BigDecimal variantPrice = catalogRestClient.get(
                    "/api/v1/products/{productId}/variants/{variantId}/price",
                    BigDecimal.class, productId, variantId);
            return new ProductValResult(productInfo.vendorId(), variantPrice);
        }
        return new ProductValResult(productInfo.vendorId(),productInfo.effectivePrice());
    }
}