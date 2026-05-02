package com.platform.common.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class OrderPlacedEvent extends DomainEvent {

    private  String orderId;
    private  String customerId;
    private  String orderNumber;
    private  BigDecimal totalAmount;
    private  String customerIp;

    public OrderPlacedEvent(String orderId, String customerId, String orderNumber, BigDecimal totalAmount, String customerIp) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.customerIp = customerIp;
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