package com.platform.cart.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_cart_customer", columnList = "customer_id"),
        @Index(name = "idx_cart_session", columnList = "session_id"),
        @Index(name = "idx_cart_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends AuditableEntity {

    @Column(name = "customer_id")
    private UUID customerId;          // null for guest carts

    @Column(name = "session_id")
    private String sessionId;         // for guest carts

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }

    public boolean isGuestCart() {
        return customerId == null && sessionId != null;
    }

    public void merge(Cart otherCart) {
        // merge items: add quantities if same product+variant
        for (CartItem otherItem : otherCart.getItems()) {
            this.items.stream()
                    .filter(existing -> existing.getProductId().equals(otherItem.getProductId())
                            && Objects.equals(existing.getVariantId(), otherItem.getVariantId()))
                    .findFirst()
                    .ifPresentOrElse(
                            existing -> existing.setQuantity(existing.getQuantity() + otherItem.getQuantity()),
                            () -> this.addItem(new CartItem(null, otherItem.getProductId(),
                                    otherItem.getVariantId(), otherItem.getQuantity()))
                    );
        }
        if (otherCart.getCouponCode() != null && this.couponCode == null) {
            this.couponCode = otherCart.getCouponCode();
        }
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (discountAmount != null) {
            this.totalAmount = this.totalAmount.subtract(discountAmount);
        }
    }
}