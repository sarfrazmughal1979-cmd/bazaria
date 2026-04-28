package com.platform.wishlist.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import com.platform.wishlist.application.dto.AddToWishlistRequest;
import com.platform.wishlist.application.dto.WishlistItemResponse;
import com.platform.wishlist.application.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    @Operation(summary = "Add a product to wishlist")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> add(@CurrentUser UserContext user,
                                                                  @Valid @RequestBody AddToWishlistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(wishlistService.addItem(request)));
    }

    @GetMapping
    @Operation(summary = "Get my wishlist")
    public ResponseEntity<ApiResponse<PagedResponse<WishlistItemResponse>>> getWishlist(
            @CurrentUser UserContext user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(user.getUserId(), pageable)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove a product from wishlist")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable UUID productId) {
        wishlistService.removeItem(productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist"));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if a product is in wishlist")
    public ResponseEntity<ApiResponse<Boolean>> check(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.isInWishlist(productId)));
    }

    @GetMapping("/count")
    @Operation(summary = "Get wishlist item count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlistCount()));
    }
}