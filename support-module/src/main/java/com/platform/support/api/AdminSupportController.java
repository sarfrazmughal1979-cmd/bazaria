package com.platform.support.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.support.application.dto.*;
import com.platform.support.application.service.DisputeService;
import com.platform.support.application.service.SupportAutomationService;
import com.platform.support.application.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/support")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
@RequiredArgsConstructor
public class AdminSupportController {

    private final TicketService ticketService;
    private final DisputeService disputeService;
    private final SupportAutomationService automationService;

    @GetMapping("/tickets/pending")
    @Operation(summary = "Get all open tickets")
    public ResponseEntity<ApiResponse<PagedResponse<TicketResponse>>> getOpenTickets(
            @PageableDefault(size = 20) Pageable pageable) {
        // Implementation
        throw new UnsupportedOperationException("Implement admin ticket list");
    }

    @PutMapping("/tickets/{ticketId}/assign")
    @Operation(summary = "Assign ticket to agent")
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(
            @PathVariable UUID ticketId,
            @RequestParam UUID agentId) {
        TicketResponse response = ticketService.assignTicket(ticketId, agentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/tickets/{ticketId}/status")
    @Operation(summary = "Update ticket status")
    public ResponseEntity<ApiResponse<TicketResponse>> updateStatus(
            @PathVariable UUID ticketId,
            @RequestParam String status,
            @RequestParam(required = false) String note) {
        TicketResponse response = ticketService.updateTicketStatus(ticketId, status, note);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/disputes/pending")
    @Operation(summary = "Get pending disputes")
    public ResponseEntity<ApiResponse<PagedResponse<DisputeResponse>>> getPendingDisputes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(disputeService.getPendingDisputes(pageable)));
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    @Operation(summary = "Resolve dispute")
    public ResponseEntity<ApiResponse<DisputeResponse>> resolveDispute(
            @PathVariable UUID disputeId,
            @RequestParam String resolution,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String adminNotes) {
        DisputeResponse response = disputeService.resolveDispute(disputeId, resolution, amount, adminNotes);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get support statistics")
    public ResponseEntity<ApiResponse<SupportStatsResponse>> getStats() {
        // Implementation would aggregate from repositories
        throw new UnsupportedOperationException("Implement stats aggregation");
    }
}