package com.platform.review.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "product_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends AuditableEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "title")
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_verified_purchase")
    private boolean verifiedPurchase;

    @Column(name = "is_approved")
    private boolean approved;

    @Column(name = "helpful_count")
    private int helpfulCount;

    @Column(name = "not_helpful_count")
    private int notHelpfulCount;

    public void approve() { this.approved = true; }
    public void reject() { this.approved = false; }
    public void markHelpful() { this.helpfulCount++; }
    public void markNotHelpful() { this.notHelpfulCount++; }
}
