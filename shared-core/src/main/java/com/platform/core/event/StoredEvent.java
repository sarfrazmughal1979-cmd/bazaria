package com.platform.core.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "domain_events", indexes = {
        @Index(name = "idx_event_aggregate", columnList = "aggregate_type, aggregate_id"),
        @Index(name = "idx_event_type", columnList = "event_type"),
        @Index(name = "idx_event_occurred_on", columnList = "occurred_on")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredEvent {

    @Id
    private UUID eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;

    @Column(name = "processed")
    private boolean processed = false;
}