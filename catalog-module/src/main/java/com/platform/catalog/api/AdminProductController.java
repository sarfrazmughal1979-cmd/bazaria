package com.platform.catalog.api;

import com.platform.common.application.dto.ProductResponse;
import com.platform.catalog.application.service.ProductService;
import com.platform.catalog.domain.model.ProductStatus;
import com.platform.core.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    // 1. List products needing approval (PENDING_APPROVAL)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getPendingProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> page = productService.getProductsByStatus(ProductStatus.PENDING_APPROVAL, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    // 2. Approve a product
    @PutMapping("/{productId}/approve")
    public ResponseEntity<ApiResponse<String>> approveProduct(@PathVariable UUID productId) {
        productService.approveProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product approved"));
    }

    // 3. Reject a product (simple rejection, could add reason later)
    @PutMapping("/{productId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectProduct(@PathVariable UUID productId) {
        productService.rejectProduct(productId, "Rejected by admin");
        return ResponseEntity.ok(ApiResponse.success("Product rejected"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }
}