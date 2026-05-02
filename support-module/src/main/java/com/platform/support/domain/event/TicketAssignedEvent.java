package com.platform.support.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {
    private final String ticketId;
    private final String ticketNumber;
    private final String assignedToUserId;
    public TicketAssignedEvent(String ticketId, String ticketNumber, String assignedToUserId) {
        super();
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.assignedToUserId = assignedToUserId;
    }
    @Override public String getAggregateId() { return ticketId; }
    @Override public String getAggregateType() { return "Ticket"; }
}