package com.platform.cart.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;          // cached at add time

    @Column(name = "total_price", precision = 19, scale = 4)
    private BigDecimal totalPrice;

    public CartItem(Cart cart, UUID productId, UUID variantId, int quantity) {
        this.setCart(cart);
        this.setProductId(productId);
        this.setVariantId(variantId);
        this.setQuantity(quantity);
    }

    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        if (unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getSubtotal() {
        return totalPrice != null ? totalPrice : BigDecimal.ZERO;
    }
}