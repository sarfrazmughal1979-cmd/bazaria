package com.platform.cart.application.service;

import com.platform.cart.domain.model.Cart;
import com.platform.cart.domain.model.CartStatus;
import com.platform.cart.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartMergeService {

    private final CartRepository cartRepository;
    private final CartService cartService;

    @Transactional
    public void mergeGuestCart(UUID customerId, String sessionId) {
        Cart guestCart = cartRepository.findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE)
                .orElse(null);
        if (guestCart == null || guestCart.getItems().isEmpty()) {
            return;
        }

        Cart userCart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElse(null);

        if (userCart == null) {
            // Transfer guest cart to user
            guestCart.setCustomerId(customerId);
            guestCart.setSessionId(null);
            // Extend expiry
            guestCart.setExpiresAt(java.time.Instant.now().plus(java.time.Duration.ofDays(30)));
            cartRepository.save(guestCart);
        } else {
            // Merge items
            userCart.merge(guestCart);
            cartRepository.save(userCart);
            // Delete guest cart
            guestCart.setStatus(CartStatus.EXPIRED);
            cartRepository.save(guestCart);
        }
        log.info("Merged guest cart for user {}", customerId);
    }
}