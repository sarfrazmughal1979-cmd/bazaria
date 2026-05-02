package com.platform.common.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {
    private final String productId;
    private final String variantId;
    private final String vendorId;
    private final int quantity;

    public ProductAddedToCartEvent(String productId, String variantId, String vendorId, int quantity) {
        super();
        this.productId = productId;
        this.variantId = variantId;
        this.vendorId = vendorId;
        this.quantity = quantity;
    }

    @Override
    public String getAggregateId() {
        return productId;
    }

    @Override
    public String getAggregateType() {
        return "Product";
    }
}