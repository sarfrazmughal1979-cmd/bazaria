package com.platform.core.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentFailedEvent extends DomainEvent {

    private final String paymentId;
    private final String orderId;
    private final BigDecimal amount;
    private final String currency;
    private final String failureReason;

    public PaymentFailedEvent(String paymentId, String orderId, String failureReason) {
        this(paymentId, orderId, null, null, failureReason);
    }

    public PaymentFailedEvent(String paymentId, String orderId, BigDecimal amount, String currency, String failureReason) {
        super();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.failureReason = failureReason;
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