package com.platform.cart.application.service;

import com.platform.cart.application.dto.*;
import com.platform.cart.application.mapper.CartMapper;
import com.platform.cart.domain.event.CartUpdatedEvent;
import com.platform.cart.domain.model.Cart;
import com.platform.cart.domain.model.CartItem;
import com.platform.cart.domain.model.CartStatus;
import com.platform.cart.domain.repository.CartRepository;
import com.platform.common.domain.event.ProductAddedToCartEvent;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMergeService cartMergeService;
    private final CartMapper cartMapper;
    private final CartValidationService validationService;
    private final DomainEventPublisher eventPublisher;
    private final RedissonClient redissonClient;
    private final RestClientFactory restClientFactory;

    @Value("${module.promotion.url:http://localhost:8080}")
    private String promotionBaseUrl;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    private ResilientRestClient promotionRestClient;
    private ResilientRestClient catalogRestClient;

    @PostConstruct
    public void init() {
        promotionRestClient = restClientFactory.create(promotionBaseUrl, 10);
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);

    }

    private static final int GUEST_CART_TTL_DAYS = 7;
    private static final int USER_CART_TTL_DAYS = 30;

    // ---------- Retrieve cart ----------
    private String cacheKey(String customerId, String sessionId) {
        if (customerId != null) return customerId;
        if (sessionId != null && !sessionId.isBlank()) return sessionId;
        return "anonymous";
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "cart", key = "#customerId != null ? #customerId.toString() : (#sessionId != null ? #sessionId : 'anonymous')")
    public CartResponse getCart(UUID customerId, String sessionId) {
        Cart cart = findActiveCart(customerId, sessionId)
                .orElseGet(() -> createEmptyCart(customerId, sessionId));
        enrichWithProductDetails(cart);
        cart.recalculateTotal();   // ← add this line
        return cartMapper.toResponse(cart);
    }

    public void mergeGuestCart(UUID customerId, String sessionId) {
        cartMergeService.mergeGuestCart(customerId, sessionId);
    }

    public record CartItemInfo(UUID productId, UUID variantId, int quantity, BigDecimal unitPrice) {}

    public List<CartItemInfo> getCartItems(UUID customerId) {
        Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getItems().stream()
                .map(item -> new CartItemInfo(
                        item.getProductId(),
                        item.getVariantId(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());
    }

    // ---------- Add to cart ----------

    @Transactional
    @CacheEvict(value = "cart", key = "#customerId != null ? #customerId.toString() : (#sessionId != null ? #sessionId : 'anonymous')")
    public CartResponse addToCart(UUID customerId, String sessionId, AddToCartRequest request) {
        String lockKey = "cart:lock:" + (customerId != null ? customerId : sessionId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new BusinessException("CART_BUSY", "Cart is being updated, please retry");
            }
            Cart cart = findActiveCart(customerId, sessionId)
                    .orElseGet(() -> createEmptyCart(customerId, sessionId));

            // Validate product and price
            CartValidationService.ProductValResult valResult = validationService.validateAndGetPrice(
                    request.getProductId(), request.getVariantId(), request.getQuantity());

            // Find existing item or create new
            CartItem existing = cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(request.getProductId())
                            && (request.getVariantId() == null
                            ? i.getVariantId() == null
                            : request.getVariantId().equals(i.getVariantId())))
                    .findFirst().orElse(null);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + request.getQuantity());
                existing.setUnitPrice(valResult.price()); // update price in case it changed
            } else {
                CartItem newItem = CartItem.builder()
                        .productId(request.getProductId())
                        .variantId(request.getVariantId())
                        .quantity(request.getQuantity())
                        .unitPrice(valResult.price())
                        .build();
                cart.addItem(newItem);
            }

            // Recalculate totals
            cart.recalculateTotal();

            // Apply coupon if present
            if (cart.getCouponCode() != null) {
                applyCouponToCart(cart);
            }

            cartRepository.save(cart);
            eventPublisher.publishAsync(new CartUpdatedEvent(cart.getId().toString(), "ADD_ITEM"));
            eventPublisher.publishAsync(new ProductAddedToCartEvent(
                    request.getProductId().toString(),
                    request.getVariantId() != null ? request.getVariantId().toString() : null,
                    valResult.vendorId().toString(),
                    request.getQuantity()
            ));
            return cartMapper.toResponse(cart);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("CART_ERROR", "Operation interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ---------- Update quantity ----------

    @Transactional
    @CacheEvict(value = "cart", key = "#customerId != null ? #customerId : #sessionId")
    public CartResponse updateCartItem(UUID customerId, String sessionId, UpdateCartItemRequest request) {
        Cart cart = findActiveCart(customerId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user/session", "not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(request.getItemId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", request.getItemId()));

        // Re-validate stock
        validationService.validateAndGetPrice(item.getProductId(), item.getVariantId(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        cart.recalculateTotal();
        if (cart.getCouponCode() != null) {
            applyCouponToCart(cart);
        }
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    // ---------- Remove item ----------

    @Transactional
    @CacheEvict(value = "cart", key = "#customerId != null ? #customerId : #sessionId")
    public CartResponse removeCartItem(UUID customerId, String sessionId, UUID itemId) {
        Cart cart = findActiveCart(customerId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user/session", "not found"));
        cart.removeItem(itemId);
        cart.recalculateTotal();
        if (cart.getCouponCode() != null) {
            applyCouponToCart(cart);
        }
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    // ---------- Apply coupon ----------

    @Transactional
    @CacheEvict(value = "cart", key = "#customerId != null ? #customerId : #sessionId")
    public CartResponse applyCoupon(UUID customerId, String sessionId, String couponCode) {
        Cart cart = findActiveCart(customerId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user/session", "not found"));

        // Validate coupon via REST call to Promotion module
        DiscountRequest discountRequest = new DiscountRequest(couponCode, cart.getTotalAmount(), customerId);
        BigDecimal discount = promotionRestClient.post(
                "/api/internal/promotions/calculate-discount",
                discountRequest,
                BigDecimal.class);

        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_COUPON", "Coupon code is invalid or expired");
        }

        cart.setCouponCode(couponCode);
        cart.setDiscountAmount(discount);
        cart.recalculateTotal();
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    // ---------- Remove coupon ----------

    @Transactional
    @CacheEvict(value = "cart", key = "#customerId != null ? #customerId : #sessionId")
    public CartResponse removeCoupon(UUID customerId, String sessionId) {
        Cart cart = findActiveCart(customerId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user/session", "not found"));
        cart.setCouponCode(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.recalculateTotal();
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    // ---------- Clear cart (after order) ----------

    @Transactional
    public void clearCart(UUID customerId) {
        Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElse(null);
        if (cart != null) {
            cart.setStatus(CartStatus.CONVERTED);
            cartRepository.save(cart);
        }
    }

    // ---------- Private helpers ----------

    private Optional<Cart> findActiveCart(UUID customerId, String sessionId) {
        if (customerId != null) {
            return cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
        } else if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE);
        }
        return Optional.of(createEmptyCart(customerId, sessionId));
    }

    private Cart createEmptyCart(UUID customerId, String sessionId) {
        Instant expiresAt = Instant.now().plus(Duration.ofDays(
                customerId != null ? USER_CART_TTL_DAYS : GUEST_CART_TTL_DAYS));
        Cart cart = Cart.builder()
                .customerId(customerId)
                .sessionId(sessionId)
                .status(CartStatus.ACTIVE)
                .expiresAt(expiresAt)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    private void applyCouponToCart(Cart cart) {
        DiscountRequest discountRequest = new DiscountRequest(cart.getCouponCode(), cart.getTotalAmount(), cart.getCustomerId());
        BigDecimal discount = promotionRestClient.post(
                "/api/internal/promotions/calculate-discount",
                discountRequest,
                BigDecimal.class);
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            cart.setDiscountAmount(discount);
            cart.recalculateTotal();
        }
    }

    private void enrichWithProductDetails(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) return;
        try {
            for (var item : cart.getItems()) {
                var productInfo = catalogRestClient.get(
                    "/api/v1/products/{productId}/info-mini", CatalogProductInfo.class, item.getProductId());
                if (productInfo != null) {
                    item.setProductName(productInfo.name());
                    item.setProductImage(productInfo.imageUrl());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to enrich cart items", e);
        }
    }
    private record CatalogProductInfo(UUID productId, String name, String slug, String imageUrl) {}
    @Scheduled(cron = "0 0 3 * * *") // daily at 3 AM
    @Transactional
    public void expireOldCarts() {
        int updated = cartRepository.expireOldCarts(Instant.now(), CartStatus.EXPIRED);
        log.info("Expired {} old carts", updated);
    }

    // Request DTO for discount calculation
    private record DiscountRequest(String couponCode, BigDecimal subtotal, UUID customerId) {}
}