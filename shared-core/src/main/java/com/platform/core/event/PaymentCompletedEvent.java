package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
 public class PaymentCompletedEvent extends DomainEvent {

    private  String paymentId;
    private  String orderId;
    private  BigDecimal amount;
    private  String currency;

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