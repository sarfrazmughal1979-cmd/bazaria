package com.platform.common.domain.event;
import lombok.NoArgsConstructor;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class ProductAddedToCartEvent extends DomainEvent {
    private  String productId;
    private  String variantId;
    private  String vendorId;
    private  int quantity;

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