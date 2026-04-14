package com.platform.iam.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.iam.application.dto.*;
import com.platform.iam.application.service.VendorService;
import com.platform.iam.domain.model.Vendor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Vendor Management")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping("/vendors/register")
    @Operation(summary = "Register as a vendor (requires authenticated user)")
    public ResponseEntity<ApiResponse<VendorResponse>> registerVendor(
            @Valid @RequestBody VendorRegistrationRequest request) {
        VendorResponse response = vendorService.registerVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Vendor registration submitted"));
    }

    @GetMapping("/vendor/profile")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Get current vendor profile")
    public ResponseEntity<ApiResponse<VendorResponse>> getCurrentVendor() {
        VendorResponse response = vendorService.getCurrentVendor();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vendors/{slug}/shop")
    @Operation(summary = "Get vendor public shop page")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorBySlug(
            @PathVariable String slug) {
        VendorResponse response = vendorService.getVendorBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/admin/vendors/{vendorId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or reject vendor application")
    public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(
            @PathVariable UUID vendorId,
            @Valid @RequestBody VendorApprovalRequest request) {
        VendorResponse response = vendorService.approveVendor(vendorId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/admin/vendors/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending vendor applications")
    public ResponseEntity<ApiResponse<PagedResponse<VendorResponse>>> getPendingVendors(
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<VendorResponse> response = vendorService.getVendorsByStatus(
                com.platform.iam.domain.model.VendorStatus.PENDING, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vendors/{vendorId}/info-mini")
    public ResponseEntity<VendorResponse> getVendorInfoMini(@PathVariable UUID vendorId) {
        VendorResponse vendor = vendorService.findById(vendorId);
        if (vendor == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vendor);
    }
    @GetMapping("/vendors/{vendorId}/active")
    public ResponseEntity<Boolean> isVendorActive(@PathVariable UUID vendorId) {
        VendorResponse vendor = vendorService.findById(vendorId);
        return ResponseEntity.ok(vendor != null && vendor.getStatus().equalsIgnoreCase("ACTIVE"));
    }

    @GetMapping("/vendors/{vendorId}/name")
    public ResponseEntity<String> getVendorName(@PathVariable UUID vendorId) {
        VendorResponse vendor = vendorService.findById(vendorId);
        return ResponseEntity.ok(vendor != null ? vendor.getShopName() : null);
    }
}