package com.platform.common.domain.event;
import lombok.NoArgsConstructor;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
 public class OrderDeliveredEvent extends DomainEvent {
    private  String orderId;
    private  String orderNumber;
    private  String customerId;

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