package com.platform.payment.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_order", columnList = "order_id"),
        @Index(name = "idx_payment_customer", columnList = "customer_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_gateway_txn", columnList = "gateway_transaction_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AuditableEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency"))
    })
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 30)
    private PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;

    @Column(name = "gateway_redirect_url", length = 1000)
    private String gatewayRedirectUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    // Domain methods
    public void markInitiated() {
        this.status = PaymentStatus.INITIATED;
    }

    public void markPending() {
        this.status = PaymentStatus.PENDING;
    }

    public void markCompleted() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
}