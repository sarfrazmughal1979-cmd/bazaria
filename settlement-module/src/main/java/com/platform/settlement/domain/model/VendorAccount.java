package com.platform.settlement.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "vendor_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorAccount extends BaseEntity {
    @Column(name = "vendor_id", unique = true, nullable = false)
    private UUID vendorId;
    @Column(name = "available_balance", precision = 19, scale = 4)
    private BigDecimal availableBalance;
    @Column(name = "pending_balance", precision = 19, scale = 4)
    private BigDecimal pendingBalance;
    @Column(name = "total_earned", precision = 19, scale = 4)
    private BigDecimal totalEarned;
    @Column(name = "total_withdrawn", precision = 19, scale = 4)
    private BigDecimal totalWithdrawn;
    @Column(name = "currency", length = 3)
    private String currency;
}