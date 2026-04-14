package com.platform.support.application.dto;

import com.platform.support.domain.model.TicketCategory;
import com.platform.support.domain.model.TicketPriority;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private UUID id;
    private String ticketNumber;
    private UUID customerId;
    private String customerName;
    private UUID vendorId;
    private String vendorName;
    private UUID orderId;
    private String categoryName;
    private String subject;
    private String description;
    private String status;
    private TicketPriority priority;
    private UUID assignedTo;
    private String assignedToName;
    private Instant assignedAt;
    private Instant resolvedAt;
    private Instant closedAt;
    private Instant firstResponseAt;
    private Integer customerRating;
    private TicketCategory category;
    private List<TicketMessageResponse> messages;
    private Instant createdAt;
    private Instant updatedAt;
}