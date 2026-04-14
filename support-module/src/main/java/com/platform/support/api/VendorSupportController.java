package com.platform.support.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import com.platform.support.application.dto.DisputeResponse;
import com.platform.support.application.dto.TicketResponse;
import com.platform.support.application.service.DisputeService;
import com.platform.support.application.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendor/support")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorSupportController {

    private final TicketService ticketService;
    private final DisputeService disputeService;

    @GetMapping("/tickets")
    @Operation(summary = "Get tickets related to vendor's products")
    public ResponseEntity<ApiResponse<PagedResponse<TicketResponse>>> getVendorTickets(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getVendorTickets(pageable)));
    }

    @PostMapping("/tickets/{ticketId}/messages")
    @Operation(summary = "Vendor reply to ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> replyToTicket(
            @PathVariable UUID ticketId,
            @RequestBody String message) {
        // Implementation
        throw new UnsupportedOperationException("Implement vendor reply");
    }

    @GetMapping("/disputes")
    @Operation(summary = "Get disputes against vendor")
    public ResponseEntity<ApiResponse<PagedResponse<DisputeResponse>>> getVendorDisputes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(disputeService.getVendorDisputes(pageable)));
    }
}