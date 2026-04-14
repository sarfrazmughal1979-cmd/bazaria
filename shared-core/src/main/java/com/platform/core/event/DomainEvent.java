package com.platform.core.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }

    public abstract String getAggregateId();

    public abstract String getAggregateType();
}