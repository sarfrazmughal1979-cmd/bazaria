package com.platform.cart.api;

import com.platform.cart.application.dto.AddToCartRequest;
import com.platform.cart.application.dto.CartResponse;
import com.platform.cart.application.dto.UpdateCartItemRequest;
import com.platform.cart.application.service.CartService;
import com.platform.core.dto.ApiResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart")
public class CartInternalController {

    private final CartService cartService;

    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = request.getSession(true).getId();
        }
        return sessionId;
    }

    @GetMapping
    @Operation(summary = "Get current cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @CurrentUser UserContext user,
            HttpServletRequest request) {
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.getCart(customerId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @CurrentUser UserContext user,
            HttpServletRequest request,
            @Valid @RequestBody AddToCartRequest addRequest) {
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.addToCart(customerId, sessionId, addRequest);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @CurrentUser UserContext user,
            HttpServletRequest request,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest updateRequest) {
        updateRequest.setItemId(itemId);
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.updateCartItem(customerId, sessionId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @CurrentUser UserContext user,
            HttpServletRequest request,
            @PathVariable UUID itemId) {
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.removeCartItem(customerId, sessionId, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply coupon code")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(
            @CurrentUser UserContext user,
            HttpServletRequest request,
            @RequestParam String couponCode) {
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.applyCoupon(customerId, sessionId, couponCode);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove coupon")
    public ResponseEntity<ApiResponse<CartResponse>> removeCoupon(
            @CurrentUser UserContext user,
            HttpServletRequest request) {
        UUID customerId = user != null ? user.getUserId() : null;
        String sessionId = getSessionId(request);
        CartResponse cart = cartService.removeCoupon(customerId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam UUID customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}