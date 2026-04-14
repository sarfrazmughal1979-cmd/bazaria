package com.platform.catalog.api;

import com.platform.catalog.application.service.ProductService;
import com.platform.catalog.domain.model.Category;
import com.platform.catalog.domain.model.Product;
import com.platform.catalog.domain.model.ProductImage;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService catalogService;
    @GetMapping("/{productId}/info")
    public ResponseEntity<CatalogProductInfo> getProductInfo(@PathVariable UUID productId) {
        var product = catalogService.findById(productId); // returns your internal DTO
        return ResponseEntity.ok(new CatalogProductInfo(
                product.getId(),
                product.getName(),
                product.getEffectivePrice().getAmount(),
                product.getVendorId(),
                product.getCategory().getImageUrl(),
                product.getSku()
        ));
    }
 
	@GetMapping("/products/{productId}/info-mini")
public ResponseEntity<ProductInfoMini> getProductInfoMini(@PathVariable UUID productId) {
    Product product = catalogService.findById(productId);
    if (product == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(new ProductInfoMini(
        product.getId(), product.getName(), product.getSlug(),
        product.getImages().stream().filter(ProductImage::isPrimary).findFirst().map(ProductImage::getUrl).orElse(null)
    ));
}

public record ProductInfoMini(UUID id, String name, String slug, String imageUrl) {}

	@GetMapping("/{productId}/variants/{variantId}/price")
    public ResponseEntity<BigDecimal> getVariantPrice(@PathVariable UUID productId,
                                                      @PathVariable UUID variantId) {
        BigDecimal price = catalogService.getVariantPrice(productId, variantId);
        return ResponseEntity.ok(price);
    }
    @GetMapping("/categories/{categoryId}/info")
    public ResponseEntity<CatalogCategoryInfo> getCategoryInfo(@PathVariable UUID categoryId) {
        Category category = catalogService.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return ResponseEntity.ok(new CatalogCategoryInfo(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getImageUrl()
        ));
    }

    public record CatalogCategoryInfo(UUID categoryId, String name, String slug, String imageUrl) {}
    public record CatalogProductInfo(
            UUID productId,
            String name,
            BigDecimal effectivePrice,
            UUID vendorId,
            String imageUrl,
            String sku
    ) {}
}