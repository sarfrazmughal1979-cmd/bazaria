package com.platform.support.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class TicketAssignedEvent extends DomainEvent {
    private  String ticketId;
    private  String ticketNumber;
    private  String assignedToUserId;
    public TicketAssignedEvent(String ticketId, String ticketNumber, String assignedToUserId) {
        super();
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.assignedToUserId = assignedToUserId;
    }
    @Override public String getAggregateId() { return ticketId; }
    @Override public String getAggregateType() { return "Ticket"; }
}