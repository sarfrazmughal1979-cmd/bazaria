package com.platform.core.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentCompletedEvent extends DomainEvent {

    private final String paymentId;
    private final String orderId;
    private final BigDecimal amount;
    private final String currency;

    public PaymentCompletedEvent(String paymentId, String orderId, BigDecimal amount) {
        this(paymentId, orderId, amount, null);
    }

    public PaymentCompletedEvent(String paymentId, String orderId, BigDecimal amount, String currency) {
        super();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String getAggregateId() {
        return paymentId;
    }

    @Override
    public String getAggregateType() {
        return "Payment";
    }
}