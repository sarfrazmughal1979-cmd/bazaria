package com.platform.pricing.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tax_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxRule extends AuditableEntity {

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;          // ISO 3166-1 alpha-3

    @Column(name = "state_code", length = 10)
    private String stateCode;

    @Column(name = "category_id")
    private java.util.UUID categoryId;   // optional, null = applies to all

    @Column(name = "tax_type", nullable = false, length = 20)
    private String taxType;              // VAT, GST, SALES_TAX, etc.

    @Column(name = "rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal rate;             // e.g., 15.00 for 15%

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "priority")
    private int priority;                // 1 = highest
}