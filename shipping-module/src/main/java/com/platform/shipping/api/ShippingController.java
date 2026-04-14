package com.platform.shipping.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import com.platform.shipping.application.dto.*;
import com.platform.shipping.application.service.ShippingRateCalculator;
import com.platform.shipping.application.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;
    private final ShippingRateCalculator rateCalculator;

    @PostMapping("/rates")
    @Operation(summary = "Calculate shipping rates")
    public ResponseEntity<ApiResponse<List<ShippingRateResponse>>> calculateRates(
            @Valid @RequestBody ShippingRateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(rateCalculator.calculateRates(request)));
    }

    @PostMapping("/shipments")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Create a shipment for a sub-order")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @CurrentUser UserContext user,
            @Valid @RequestBody CreateShipmentRequest request) {
        request.setVendorId(user.getVendorId()); // ensure vendor matches
        ShipmentResponse response = shippingService.createShipment(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/shipments/suborder/{subOrderId}")
    @Operation(summary = "Get shipment by sub-order ID")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentBySubOrder(
            @PathVariable UUID subOrderId) {
        ShipmentResponse response = shippingService.getShipmentBySubOrder(subOrderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/shipments/tracking/{trackingNumber}")
    @Operation(summary = "Get shipment by tracking number")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTracking(
            @PathVariable String trackingNumber) {
        ShipmentResponse response = shippingService.getShipmentByTracking(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vendor/shipments")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Get all shipments for the current vendor")
    public ResponseEntity<ApiResponse<PagedResponse<ShipmentResponse>>> getVendorShipments(
            @CurrentUser UserContext user,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<ShipmentResponse> response = shippingService.getVendorShipments(user.getVendorId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}