package com.platform.payment.domain.model;
import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity
@Table(name = "refunds")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Refund extends AuditableEntity {
    private UUID paymentId;
    private UUID orderId;
    @Embedded
    private Money amount;
    private String reason;
    @Enumerated(EnumType.STRING)
    private RefundStatus status;
    private String gatewayRefundId;
}
