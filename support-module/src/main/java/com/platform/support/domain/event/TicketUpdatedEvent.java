package com.platform.support.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class TicketUpdatedEvent extends DomainEvent {
    private final String ticketId;
    private final String oldStatus;
    private final String newStatus;
    public TicketUpdatedEvent(String ticketId, String oldStatus, String newStatus) {
        super();
        this.ticketId = ticketId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
    @Override public String getAggregateId() { return ticketId; }
    @Override public String getAggregateType() { return "Ticket"; }
}