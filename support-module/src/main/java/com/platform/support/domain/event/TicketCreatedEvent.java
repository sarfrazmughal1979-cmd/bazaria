package com.platform.support.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class TicketCreatedEvent extends DomainEvent {
    private  String ticketId;
    private  String ticketNumber;
    private  String customerId;
    private  String category;
    private  String subject;
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