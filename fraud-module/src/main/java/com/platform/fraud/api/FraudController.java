package com.platform.fraud.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.fraud.application.dto.FraudEvaluationResponse;
import com.platform.fraud.application.service.FraudService;
import com.platform.fraud.domain.model.FraudCheck;
import com.platform.fraud.domain.repository.FraudCheckRepository;
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
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudService fraudService;
    private final FraudCheckRepository fraudCheckRepository;

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate an order for fraud risk")
    public ResponseEntity<ApiResponse<FraudEvaluationResponse>> evaluate(
            @RequestParam UUID orderId,
            @RequestParam UUID customerId,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "0.0.0.0") String ipAddress) {
        var result = fraudService.evaluateOrder(orderId, customerId, amount.doubleValue(), ipAddress);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/checks/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get checks needing manual review")
    public ResponseEntity<ApiResponse<PagedResponse<FraudCheck>>> getReviewQueue(
            @PageableDefault(size = 20) Pageable pageable) {
        var page = fraudCheckRepository.findByStatus("REVIEW", pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(page)));
    }

    @PutMapping("/checks/{checkId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or reject a flagged check")
    public ResponseEntity<ApiResponse<Void>> reviewCheck(
            @PathVariable UUID checkId,
            @RequestParam boolean approved,
            @RequestParam UUID reviewerId,
            @RequestParam(required = false) String notes) {
        fraudService.reviewCheck(checkId, approved, reviewerId, notes);
        return ResponseEntity.ok(ApiResponse.success("Check reviewed"));
    }
}