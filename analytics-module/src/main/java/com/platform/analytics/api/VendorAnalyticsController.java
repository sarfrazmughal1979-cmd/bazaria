package com.platform.analytics.api;

import com.platform.analytics.application.dto.VendorPerformanceResponse;
import com.platform.analytics.application.service.VendorAnalyticsService;
import com.platform.core.dto.ApiResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/vendor/analytics")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorAnalyticsController {

    private final VendorAnalyticsService vendorAnalyticsService;

    @GetMapping("/performance")
    @Operation(summary = "Get performance metrics for the current vendor")
    public ResponseEntity<ApiResponse<VendorPerformanceResponse>> getPerformance(
            @CurrentUser UserContext user,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate endDate) {
        VendorPerformanceResponse response = vendorAnalyticsService.getVendorPerformance(
            user.getVendorId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}