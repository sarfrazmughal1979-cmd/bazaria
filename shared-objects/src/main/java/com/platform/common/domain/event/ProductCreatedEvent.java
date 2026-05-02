package com.platform.common.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class ProductCreatedEvent extends DomainEvent {

    private  String productId;
    private  String vendorId;
    private  String productName;

    public ProductCreatedEvent(String productId, String vendorId, String productName) {
        super();
        this.productId = productId;
        this.vendorId = vendorId;
        this.productName = productName;
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