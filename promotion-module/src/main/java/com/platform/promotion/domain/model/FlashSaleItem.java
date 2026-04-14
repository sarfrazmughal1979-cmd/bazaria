package com.platform.promotion.domain.model;

import com.platform.core.domain.BaseEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "flash_sale_items", indexes = {
        @Index(name = "idx_flash_sale_item_product", columnList = "product_id"),
        @Index(name = "idx_flash_sale_item_sale", columnList = "flash_sale_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;  // null for simple products

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "flash_sale_price")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "price_currency"))
    })
    private Money flashSalePrice;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "sold_quantity", nullable = false)
    @Builder.Default
    private int soldQuantity = 0;

    @Column(name = "limit_per_customer")
    private int limitPerCustomer;  // maximum each customer can buy

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    // Domain methods
    public int getRemainingQuantity() {
        return totalQuantity - soldQuantity;
    }

    public boolean hasAvailableStock(int requestedQuantity) {
        return getRemainingQuantity() >= requestedQuantity;
    }

    public void increaseSoldQuantity(int quantity) {
        if (soldQuantity + quantity > totalQuantity) {
            throw new IllegalStateException("Cannot sell more than total quantity");
        }
        this.soldQuantity += quantity;
    }

    public boolean isExhausted() {
        return getRemainingQuantity() <= 0;
    }
}