package com.platform.common.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

    private final String orderId;
    private final String customerId;
    private final String orderNumber;
    private final BigDecimal totalAmount;
    private final String customerIp;

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