package com.platform.settlement.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "commission_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRule extends BaseEntity {
    @Column(name = "vendor_id")
    private UUID vendorId;
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(name = "rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;
    @Column(name = "is_default")
    private boolean isDefault;
}