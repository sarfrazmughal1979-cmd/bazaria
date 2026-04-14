package com.platform.support.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class TicketCreatedEvent extends DomainEvent {
    private final String ticketId;
    private final String ticketNumber;
    private final String customerId;
    private final String category;
    private final String subject;
    public TicketCreatedEvent(String ticketId, String ticketNumber, String customerId, String category, String subject) {
        super();
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.customerId = customerId;
        this.category = category;
        this.subject = subject;
    }
    @Override public String getAggregateId() { return ticketId; }
    @Override public String getAggregateType() { return "Ticket"; }
}