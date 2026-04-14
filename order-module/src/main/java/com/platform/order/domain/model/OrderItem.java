package com.platform.order.domain.model;

import com.platform.core.domain.BaseEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_order_id", nullable = false)
    private SubOrder subOrder;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "product_name", nullable = false, length = 500)
    private String productName;

    @Column(name = "product_image", length = 500)
    private String productImage;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "unit_price")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "unit_price_currency"))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_price")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_price_currency"))
    })
    private Money totalPrice;
}