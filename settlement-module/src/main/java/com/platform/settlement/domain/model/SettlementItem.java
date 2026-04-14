package com.platform.settlement.domain.model;
import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "settlement_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementItem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @Column(name = "sub_order_id", nullable = false)
    private UUID subOrderId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "order_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "order_currency"))
    })
    private Money orderAmount;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "commission_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "commission_currency"))
    })
    private Money commissionAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "vendor_earning")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "earning_currency"))
    })
    private Money vendorEarning;
}