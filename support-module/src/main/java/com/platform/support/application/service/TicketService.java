package com.platform.support.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import com.platform.support.application.dto.*;
import com.platform.support.application.mapper.SupportMapper;
import com.platform.support.domain.event.TicketAssignedEvent;
import com.platform.support.domain.event.TicketCreatedEvent;
import com.platform.support.domain.event.TicketUpdatedEvent;
import com.platform.support.domain.model.*;
import com.platform.support.domain.repository.TicketCategoryRepository;
import com.platform.support.domain.repository.TicketMessageRepository;
import com.platform.support.domain.repository.TicketRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final TicketCategoryRepository categoryRepository;
    private final SupportMapper mapper;
    private final DomainEventPublisher eventPublisher;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient iamRestClient;
    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    // DTOs for REST responses
    private record OrderInfo(UUID orderId, String orderNumber, UUID customerId,
                             java.math.BigDecimal totalAmount, String currency, String status) {}
    private record UserInfo(UUID userId, String fullName, String email) {}
    private record VendorInfo(UUID vendorId, String shopName) {}

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        UUID customerId = SecurityUtils.getCurrentUserId();

        // Verify order belongs to customer via REST
        if (request.getOrderId() != null) {
            OrderInfo order = orderRestClient.get(
                "/api/v1/orders/{orderId}/info-mini", OrderInfo.class, request.getOrderId());
            if (order == null || !order.customerId().equals(customerId)) {
                throw new BusinessException("INVALID_ORDER", "Order not found or access denied");
            }
        }

        TicketCategory category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        String ticketNumber = Ticket.generateTicketNumber();
        Ticket ticket = mapper.toEntity(request, customerId, ticketNumber);
        ticket.setCustomerId(customerId);
        ticket.setCategory(category);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(request.getPriority() != null ? 
            request.getPriority() : TicketPriority.MEDIUM);

        ticket = ticketRepository.save(ticket);

        // Add initial message
        TicketMessage initialMessage = TicketMessage.builder()
            .ticket(ticket)
            .senderId(customerId)
            .senderType("CUSTOMER")
            .message(request.getDescription())
            .internal(false)
            .build();
        ticket.addMessage(initialMessage);
        ticket.updateFirstResponse();

        ticketRepository.save(ticket);

        eventPublisher.publish(new TicketCreatedEvent(
            ticket.getId().toString(), 
            ticket.getTicketNumber(),
            customerId.toString(),
            ticket.getCategory().getId().toString(),
            ticket.getSubject()));

        return enrichResponse(mapper.toResponse(ticket));
    }

    @Transactional
    public TicketResponse addMessage(UUID ticketId, TicketMessageRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        TicketStatus oldStatus = ticket.getStatus();
        // Verify user has access to this ticket
        boolean isCustomer = ticket.getCustomerId().equals(userId);
        boolean isVendor = ticket.getVendorId() != null && ticket.getVendorId().equals(userId);
        boolean isAgent = SecurityUtils.hasRole("SUPPORT") || SecurityUtils.hasRole("ADMIN");

        if (!isCustomer && !isVendor && !isAgent) {
            throw new BusinessException("ACCESS_DENIED", "You don't have access to this ticket");
        }

        String senderType = isCustomer ? "CUSTOMER" : (isVendor ? "VENDOR" : "AGENT");

        TicketMessage message = TicketMessage.builder()
            .ticket(ticket)
            .senderId(userId)
            .senderType(senderType)
            .message(request.getMessage())
            .internal(request.isInternal() && isAgent)
            .build();

        ticket.addMessage(message);
        ticket.setLastResponseAt(Instant.now());

        if (isCustomer && ticket.getStatus() == TicketStatus.WAITING_CUSTOMER) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else if ((isVendor || isAgent) && ticket.getStatus() == TicketStatus.WAITING_VENDOR) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticket.updateFirstResponse();
        ticketRepository.save(ticket);

        eventPublisher.publish(new TicketUpdatedEvent(ticket.getId().toString(), oldStatus.toString(), ticket.getStatus().toString()));

        return enrichResponse(mapper.toResponse(ticket));
    }

    @Transactional(readOnly = true)
    public PagedResponse<TicketResponse> getMyTickets(Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<Ticket> page = ticketRepository.findByCustomerId(userId, pageable);
        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    @Transactional(readOnly = true)
    public PagedResponse<TicketResponse> getVendorTickets(Pageable pageable) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
            .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Not a vendor"));
        Page<Ticket> page = ticketRepository.findByVendorId(vendorId, pageable);

        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    @Transactional(readOnly = true)
    public PagedResponse<TicketResponse> getAssignedTickets(Pageable pageable) {
        UUID agentId = SecurityUtils.getCurrentUserId();
        Page<Ticket> page = ticketRepository.findByAssignedTo(agentId, pageable);
        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    @Transactional
    public TicketResponse assignTicket(UUID ticketId, UUID agentId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        
        ticket.assignTo(agentId);
        ticketRepository.save(ticket);

        eventPublisher.publish(new TicketAssignedEvent(
            ticket.getId().toString(), 
            ticket.getTicketNumber(),
            agentId.toString()));

        return enrichResponse(mapper.toResponse(ticket));
    }

    @Transactional
    public TicketResponse updateTicketStatus(UUID ticketId, String status, String resolutionNote) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        TicketStatus newStatus = TicketStatus.valueOf(status);
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.resolve();
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.close();
        }

        if (resolutionNote != null && !resolutionNote.isBlank()) {
            TicketMessage note = TicketMessage.builder()
                .ticket(ticket)
                .senderId(SecurityUtils.getCurrentUserId())
                .senderType("AGENT")
                .message(resolutionNote)
                .internal(true)
                .build();
            ticket.addMessage(note);
        }

        ticketRepository.save(ticket);
        return enrichResponse(mapper.toResponse(ticket));
    }

    @Transactional
    public void rateTicket(UUID ticketId, int rating, String feedback) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (!ticket.getCustomerId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("ACCESS_DENIED", "You can only rate your own tickets");
        }

        ticket.setCustomerRating(rating);
        ticket.setCustomerFeedback(feedback);
        ticketRepository.save(ticket);
    }

    // Helper method to convert Ticket to TicketResponse and enrich
    private TicketResponse enrichResponseWithMapper(Ticket ticket) {
        return enrichResponse(mapper.toResponse(ticket));
    }
    private TicketResponse enrichResponse(TicketResponse response) {
        // Add customer name via REST
        if (response.getCustomerId() != null) {
            try {
                UserInfo user = iamRestClient.get(
                    "/api/v1/users/{userId}/info-mini", UserInfo.class, response.getCustomerId());
                if (user != null) response.setCustomerName(user.fullName());
            } catch (Exception e) {
                log.warn("Failed to fetch customer name for {}", response.getCustomerId());
        }
        }
        // Add vendor name via REST
        if (response.getVendorId() != null) {
            try {
                VendorInfo vendor = iamRestClient.get(
                    "/api/v1/vendors/{vendorId}/info-mini", VendorInfo.class, response.getVendorId());
                if (vendor != null) response.setVendorName(vendor.shopName());
            } catch (Exception e) {
                log.warn("Failed to fetch vendor name for {}", response.getVendorId());
        }
        }
        // Add assigned agent name via REST
        if (response.getAssignedTo() != null) {
            try {
                UserInfo user = iamRestClient.get(
                    "/api/v1/users/{userId}/info-mini", UserInfo.class, response.getAssignedTo());
                if (user != null) response.setAssignedToName(user.fullName());
            } catch (Exception e) {
                log.warn("Failed to fetch agent name for {}", response.getAssignedTo());
            }
        }
        return response;
    }
    public Page<Ticket> findByStatus(Pageable pageable){
      return ticketRepository.findByStatus(TicketStatus.OPEN, pageable);
    }
    public long countOpenTickets(){
        return ticketRepository.countOpenTickets(List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS));
    }
    @Transactional(readOnly = true)
    public TicketResponse getTicketDetails(UUID ticketId, UUID userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        // Verify access
        boolean isCustomer = ticket.getCustomerId().equals(userId);
        boolean isVendor = ticket.getVendorId() != null && ticket.getVendorId().equals(userId);
        boolean isAgent = SecurityUtils.hasRole("SUPPORT") || SecurityUtils.hasRole("ADMIN");
        if (!isCustomer && !isVendor && !isAgent) {
            throw new BusinessException("ACCESS_DENIED", "You don't have access to this ticket");
        }
        return enrichResponse(mapper.toResponse(ticket));
    }
}