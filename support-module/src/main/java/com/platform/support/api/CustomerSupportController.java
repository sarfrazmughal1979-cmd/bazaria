package com.platform.support.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import com.platform.support.application.dto.*;
import com.platform.support.application.service.ChatService;
import com.platform.support.application.service.DisputeService;
import com.platform.support.application.service.TicketService;
import com.platform.support.domain.model.ChatSession;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/support")
@RequiredArgsConstructor
public class CustomerSupportController {

    private final TicketService ticketService;
    private final DisputeService disputeService;
    private final ChatService chatService;

    // Tickets
    @PostMapping("/tickets")
    @Operation(summary = "Create a new support ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @CurrentUser UserContext user,
            @Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get my tickets")
    public ResponseEntity<ApiResponse<PagedResponse<TicketResponse>>> getMyTickets(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getMyTickets(pageable)));
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get ticket details")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable UUID ticketId) {
        // Implementation would fetch single ticket with messages
        throw new UnsupportedOperationException("Implement fetch by ID");
    }

    @PostMapping("/tickets/{ticketId}/messages")
    @Operation(summary = "Add message to ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> addMessage(
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketMessageRequest request) {
        TicketResponse response = ticketService.addMessage(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/tickets/{ticketId}/rate")
    @Operation(summary = "Rate ticket resolution")
    public ResponseEntity<ApiResponse<Void>> rateTicket(
            @PathVariable UUID ticketId,
            @RequestParam int rating,
            @RequestParam(required = false) String feedback) {
        ticketService.rateTicket(ticketId, rating, feedback);
        return ResponseEntity.ok(ApiResponse.success("Thank you for your feedback"));
    }

    // Disputes
    @PostMapping("/disputes")
    @Operation(summary = "Open a dispute for an order")
    public ResponseEntity<ApiResponse<DisputeResponse>> openDispute(
            @CurrentUser UserContext user,
            @Valid @RequestBody DisputeRequest request) {
        DisputeResponse response = disputeService.openDispute(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/disputes")
    @Operation(summary = "Get my disputes")
    public ResponseEntity<ApiResponse<PagedResponse<DisputeResponse>>> getMyDisputes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(disputeService.getMyDisputes(pageable)));
    }

    // Chat
    @PostMapping("/chat/start")
    @Operation(summary = "Start a chat session")
    public ResponseEntity<ApiResponse<ChatSession>> startChat(@CurrentUser UserContext user) {
        ChatSession session = chatService.startCustomerChat(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(session));
    }
}