package com.platform.shipping.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryZone extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "region")
    private String region;          // state/province

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code_pattern")
    private String postalCodePattern; // regex for matching

    @Column(name = "base_rate", precision = 19, scale = 4)
    private BigDecimal baseRate;

    @Column(name = "rate_per_kg", precision = 19, scale = 4)
    private BigDecimal ratePerKg;

    @Column(name = "is_active")
    private boolean active;
}