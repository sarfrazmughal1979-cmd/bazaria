package com.platform.fraud.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fraud_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudRule extends AuditableEntity {

    @Column(name = "rule_name", length = 100, nullable = false, unique = true)
    private String ruleName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "points", nullable = false)
    private int points;         // how many risk points this rule adds

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "rule_type", length = 30)
    private String ruleType;    // AMOUNT, VELOCITY, GEO, DEVICE, etc.
}