package com.platform.loyalty.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.security.CurrentUser;
import com.platform.core.security.UserContext;
import com.platform.loyalty.application.dto.LoyaltyAccountResponse;
import com.platform.loyalty.application.dto.RedemptionEstimate;
import com.platform.loyalty.application.service.LoyaltyService;
import com.platform.loyalty.domain.model.LoyaltyTransaction;
import com.platform.loyalty.domain.repository.LoyaltyTransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final LoyaltyTransactionRepository transactionRepository;

    @GetMapping("/account")
    @Operation(summary = "Get loyalty account balance")
    public ResponseEntity<ApiResponse<LoyaltyAccountResponse>> getAccount(@CurrentUser UserContext user) {
        return ResponseEntity.ok(ApiResponse.success(loyaltyService.getAccount(user.getUserId())));
    }

    @GetMapping("/estimate")
    @Operation(summary = "Estimate redemption value")
    public ResponseEntity<ApiResponse<RedemptionEstimate>> estimate(
            @CurrentUser UserContext user,
            @RequestParam long points,
            @RequestParam BigDecimal subtotal) {
        return ResponseEntity.ok(ApiResponse.success(loyaltyService.estimateRedemption(user.getUserId(), points, subtotal)));
    }

    @PostMapping("/redeem")
    @Operation(summary = "Redeem points and get discount amount")
    public ResponseEntity<ApiResponse<BigDecimal>> redeem(
            @CurrentUser UserContext user,
            @RequestParam long points,
            @RequestParam BigDecimal subtotal) {
        BigDecimal discount = loyaltyService.redeemPoints(user.getUserId(), points, subtotal);
        return ResponseEntity.ok(ApiResponse.success(discount));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transaction history")
    public ResponseEntity<ApiResponse<PagedResponse<LoyaltyTransaction>>> transactions(
            @CurrentUser UserContext user,
            @PageableDefault(size = 20) Pageable pageable) {
        var page = transactionRepository.findByCustomerIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(page)));
    }
}