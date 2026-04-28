package com.platform.loyalty.application.service;

import com.platform.loyalty.application.dto.LoyaltyAccountResponse;
import com.platform.loyalty.application.dto.RedemptionEstimate;
import com.platform.loyalty.domain.model.LoyaltyAccount;
import com.platform.loyalty.domain.model.LoyaltyTransaction;
import com.platform.loyalty.domain.repository.LoyaltyAccountRepository;
import com.platform.loyalty.domain.repository.LoyaltyTransactionRepository;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    @Value("${platform.loyalty.points-per-currency:1}")
    private int pointsPerCurrency;

    @Value("${platform.loyalty.redemption-rate:100}")
    private int redemptionRate;

    @Value("${platform.loyalty.max-redemption-percent:50}")
    private int maxRedemptionPercent;

    @Transactional
    public void earnPoints(UUID customerId, BigDecimal orderAmount, UUID orderId) {
        long points = orderAmount.multiply(BigDecimal.valueOf(pointsPerCurrency)).longValue();
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseGet(() -> createAccount(customerId));
        account.addPoints(points);
        account.setLifetimeSpent(account.getLifetimeSpent().add(orderAmount));
        updateTier(account);
        accountRepository.save(account);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customerId(customerId).points(points).type("EARN").orderId(orderId)
                .description("Points earned on order " + orderId).build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public BigDecimal redeemPoints(UUID customerId, long pointsRequested, BigDecimal orderSubtotal) {
        RedemptionEstimate estimate = estimateRedemption(customerId, pointsRequested, orderSubtotal);
        if (estimate.getApplicableDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new com.platform.core.exception.BusinessException("INVALID_REDEMPTION", "Cannot redeem points");
        }
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId).orElseThrow();
        long pointsToUse = Math.min(estimate.getRequestedPoints(), account.getAvailablePoints());
        account.redeemPoints(pointsToUse);
        accountRepository.save(account);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customerId(customerId).points(-pointsToUse).type("REDEEM")
                .description("Redeemed " + pointsToUse + " points").build();
        transactionRepository.save(transaction);

        return estimate.getApplicableDiscount();
    }

    public LoyaltyAccountResponse getAccount(UUID customerId) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyAccount", "customerId", customerId));
        return LoyaltyAccountResponse.builder()
                .availablePoints(account.getAvailablePoints())
                .totalPointsEarned(account.getTotalPointsEarned())
                .tier(account.getTier()).build();
    }

    public RedemptionEstimate estimateRedemption(UUID customerId, long pointsRequested, BigDecimal subtotal) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId).orElse(null);
        long available = account != null ? account.getAvailablePoints() : 0;
        long usablePoints = Math.min(pointsRequested, available);
        BigDecimal maxDiscount = subtotal.multiply(BigDecimal.valueOf(maxRedemptionPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        BigDecimal pointDiscount = BigDecimal.valueOf(usablePoints).divide(BigDecimal.valueOf(redemptionRate), 2, RoundingMode.HALF_UP);
        BigDecimal actualDiscount = pointDiscount.compareTo(maxDiscount) < 0 ? pointDiscount : maxDiscount;
        return RedemptionEstimate.builder()
                .requestedPoints(pointsRequested)
                .maxDiscount(maxDiscount)
                .orderSubtotal(subtotal)
                .applicableDiscount(actualDiscount).build();
    }

    private LoyaltyAccount createAccount(UUID customerId) {
        return LoyaltyAccount.builder()
                .customerId(customerId)
                .tier("BRONZE")
                .lifetimeSpent(BigDecimal.ZERO)
                .availablePoints(0)
                .totalPointsEarned(0)
                .build();
    }

    private void updateTier(LoyaltyAccount account) {
        BigDecimal spent = account.getLifetimeSpent();
        if (spent.compareTo(BigDecimal.valueOf(50000)) > 0) account.setTier("PLATINUM");
        else if (spent.compareTo(BigDecimal.valueOf(20000)) > 0) account.setTier("GOLD");
        else if (spent.compareTo(BigDecimal.valueOf(5000)) > 0) account.setTier("SILVER");
        else account.setTier("BRONZE");
    }
}