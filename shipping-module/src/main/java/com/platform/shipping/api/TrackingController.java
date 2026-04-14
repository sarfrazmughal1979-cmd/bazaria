package com.platform.shipping.api;

import com.platform.core.dto.ApiResponse;
import com.platform.shipping.application.dto.TrackingResponse;
import com.platform.shipping.application.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Track a shipment (public)")
    public ResponseEntity<ApiResponse<TrackingResponse>> track(@PathVariable String trackingNumber) {
        TrackingResponse response = trackingService.getTrackingInfo(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}