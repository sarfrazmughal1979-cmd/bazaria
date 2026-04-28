package com.platform.loyalty.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loyalty_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoyaltyAccount extends AuditableEntity {

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "total_points_earned")
    private long totalPointsEarned;

    @Column(name = "available_points")
    private long availablePoints;

    @Column(name = "lifetime_spent", precision = 19, scale = 4)
    private BigDecimal lifetimeSpent;

    @Column(name = "tier", length = 20)
    private String tier;    // BRONZE, SILVER, GOLD, PLATINUM

    public void addPoints(long points) {
        this.availablePoints += points;
        this.totalPointsEarned += points;
    }

    public boolean redeemPoints(long points) {
        if (points > availablePoints) return false;
        this.availablePoints -= points;
        return true;
    }
}