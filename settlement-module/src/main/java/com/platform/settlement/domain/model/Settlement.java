package com.platform.settlement.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "settlements", indexes = {
        @Index(name = "idx_settlement_vendor", columnList = "vendor_id"),
        @Index(name = "idx_settlement_status", columnList = "status"),
        @Index(name = "idx_settlement_period", columnList = "period_start, period_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement extends AuditableEntity {

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_sales")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "sales_currency"))
    })
    private Money totalSales;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_commission")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "commission_currency"))
    })
    private Money totalCommission;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_payout")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "payout_currency"))
    })
    private Money totalPayout;

    @Column(name = "period_start")
    private Instant periodStart;

    @Column(name = "period_end")
    private Instant periodEnd;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "payment_reference")
    private String paymentReference;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SettlementItem> items = new ArrayList<>();

    // Domain methods
    public void markAsProcessing() {
        this.status = SettlementStatus.PROCESSING;
    }

    public void markAsPaid(String reference) {
        this.status = SettlementStatus.PAID;
        this.paidAt = Instant.now();
        this.paymentReference = reference;
    }

    public void markAsFailed(String reason) {
        this.status = SettlementStatus.FAILED;
    }
}