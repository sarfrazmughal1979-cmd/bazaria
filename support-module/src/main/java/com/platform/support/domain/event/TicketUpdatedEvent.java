package com.platform.support.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
 public class TicketUpdatedEvent extends DomainEvent {
    private  String ticketId;
    private  String oldStatus;
    private  String newStatus;
    public TicketUpdatedEvent(String ticketId, String oldStatus, String newStatus) {
        super();
        this.ticketId = ticketId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
    @Override public String getAggregateId() { return ticketId; }
    @Override public String getAggregateType() { return "Ticket"; }
}