package com.platform.common.domain.event;
import lombok.NoArgsConstructor;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class ProductViewedEvent extends DomainEvent {
    private  String productId;
    private  String vendorId;

    public ProductViewedEvent(String productId, String vendorId) {
        super();
        this.productId = productId;
        this.vendorId = vendorId;
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