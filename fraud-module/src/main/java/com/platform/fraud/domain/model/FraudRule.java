package com.platform.fraud.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // New columns for generic engine
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions_json", columnDefinition = "JSONB")
    private ConditionNode conditions; // null means use old hardcoded logic

    @Column(name = "action", length = 20)
    @Builder.Default
    private String action = "SCORE";  // SCORE, BLOCK, FLAG

    @Column(name = "priority")
    @Builder.Default
    private int priority = 0;
}