package com.platform.order.domain.model;
import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity
@Table(name = "return_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReturnRequest extends AuditableEntity {
    private UUID orderId;
    private UUID subOrderId;
    private UUID customerId;
    private String reason;
    private String description;
    @Enumerated(EnumType.STRING)
    private ReturnStatus status;
    @Embedded
    private Money refundAmount;
    private String adminNotes;
}
