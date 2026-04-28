package com.platform.core.event.outbox;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntry {
    @Id
    private UUID id;
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "payload", columnDefinition = "JSONB", nullable = false)
    private String payload;   // serialised JSON of the DomainEvent
    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;
    @Column(name = "processed")
    private boolean processed;
}