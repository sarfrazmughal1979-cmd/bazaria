package com.platform.common.domain.event;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class OrderDeliveredEvent extends DomainEvent {
    private final String orderId;
    private final String orderNumber;
    private final String customerId;

    public OrderDeliveredEvent(String orderId, String orderNumber, String customerId) {
        super();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }
}