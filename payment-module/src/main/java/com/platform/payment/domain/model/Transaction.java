package com.platform.payment.domain.model;

import com.platform.core.domain.BaseEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_transaction_payment", columnList = "payment_id"),
        @Index(name = "idx_transaction_type", columnList = "type"),
        @Index(name = "idx_transaction_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "payment_id", insertable = false, updatable = false, nullable = false)
    private Payment payment;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "type", nullable = false, length = 30)
    private String type;  // INITIATE, CAPTURE, AUTHORIZE, REFUND, VOID, CALLBACK

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency"))
    })
    private Money amount;

    @Column(name = "status", length = 30)
    private String status;  // PENDING, SUCCESS, FAILED

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}