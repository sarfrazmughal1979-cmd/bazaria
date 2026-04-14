package com.platform.shipping.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipping_rates", indexes = {
        @Index(name = "idx_rate_lookup", columnList = "from_postal, to_postal, weight_kg")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRate extends BaseEntity {

    @Column(name = "from_postal", length = 20)
    private String fromPostal;

    @Column(name = "to_postal", length = 20)
    private String toPostal;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "method", length = 30)
    private String method;

    @Column(name = "carrier", length = 50)
    private String carrier;

    @Column(name = "cost", precision = 19, scale = 4)
    private BigDecimal cost;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "expires_at")
    private Instant expiresAt;
}