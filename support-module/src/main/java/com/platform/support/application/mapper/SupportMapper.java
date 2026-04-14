package com.platform.support.application.mapper;

import com.platform.support.application.dto.*;
import com.platform.support.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SupportMapper {

    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) return null;
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerId(ticket.getCustomerId())
                .orderId(ticket.getOrderId())
                .category(ticket.getCategory())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .priority(ticket.getPriority())
                .assignedTo(ticket.getAssignedTo())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .messages(mapMessages(ticket.getMessages()))
                .build();
    }

    public Ticket toEntity(CreateTicketRequest request, UUID customerId, String ticketNumber) {
        if (request == null) return null;

        return Ticket.builder()
                .ticketNumber(ticketNumber)
                .customerId(customerId)
                .orderId(request.getOrderId())
                .category(request.getCategory())
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(com.platform.support.domain.model.TicketStatus.OPEN)
                .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM)
                .build();
    }

    private List<TicketMessageResponse> mapMessages(List<TicketMessage> messages) {
        if (messages == null) return List.of();
        return messages.stream()
                .map(m -> TicketMessageResponse.builder()
                        .id(m.getId())
                        .senderId(m.getSenderId())
                        .message(m.getMessage())
                        .isInternal(m.isInternal())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public DisputeResponse toResponse(Dispute dispute) {
        if (dispute == null) return null;
        return DisputeResponse.builder()
                .id(dispute.getId())
                .disputeNumber(dispute.getDisputeNumber())
                .orderId(dispute.getOrderId())
                .customerId(dispute.getCustomerId())
                .vendorId(dispute.getVendorId())
                .reason(dispute.getReason())
                .description(dispute.getDescription())
                .status(dispute.getStatus() != null ? dispute.getStatus() : null)
                .resolution(dispute.getResolution())
                .resolvedAt(dispute.getResolvedAt())
                .resolvedBy(dispute.getResolvedBy())
                .createdAt(dispute.getCreatedAt())
                .build();
    }

    public Dispute toEntity(CreateDisputeRequest request, UUID customerId, String disputeNumber) {
        if (request == null) return null;
        return Dispute.builder()
                .disputeNumber(disputeNumber)
                .orderId(request.getOrderId())
                .customerId(customerId)
                .vendorId(request.getVendorId())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(DisputeStatus.PENDING)
                .build();
    }
    public Dispute toEntity(DisputeRequest request, UUID customerId, UUID vendorId, String disputeNumber) {
        if (request == null) return null;
        return Dispute.builder()
                .disputeNumber(disputeNumber)
                .orderId(request.getOrderId())
                .customerId(customerId)
                .vendorId(vendorId)
                .reason(request.getReason())
                .description(request.getDescription())
                .status(DisputeStatus.PENDING)
                .build();
    }
}