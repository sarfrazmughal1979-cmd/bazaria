package com.platform.analytics.api;

import com.platform.analytics.application.dto.DashboardResponse;
import com.platform.analytics.application.dto.SalesReportResponse;
import com.platform.analytics.application.dto.TimeRangeRequest;
import com.platform.analytics.application.service.AnalyticsService;
import com.platform.analytics.application.service.ReportGenerationService;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AnalyticsService analyticsService;
    private final ReportGenerationService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard metrics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate endDate) {
        DashboardResponse response = analyticsService.getAdminDashboard(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reports/sales/excel")
    @Operation(summary = "Export sales report as Excel")
    public ResponseEntity<byte[]> exportSalesExcel(@Valid @RequestBody TimeRangeRequest request) {
        byte[] excel = reportService.generateSalesReportExcel(request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excel);
    }
}