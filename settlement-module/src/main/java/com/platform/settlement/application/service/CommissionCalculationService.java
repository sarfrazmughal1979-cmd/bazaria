package com.platform.settlement.application.service;

import com.platform.settlement.domain.model.CommissionRule;
import com.platform.settlement.domain.repository.CommissionRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommissionCalculationService {

    private final CommissionRuleRepository commissionRuleRepository;
    private static final BigDecimal DEFAULT_COMMISSION_RATE = BigDecimal.valueOf(10); // 10%

    public BigDecimal getCommissionRate(UUID vendorId, UUID categoryId) {
        // Priority: vendor-specific > category-specific > default
        Optional<CommissionRule> vendorRule = commissionRuleRepository.findByVendorId(vendorId);
        if (vendorRule.isPresent()) return vendorRule.get().getRate();

        Optional<CommissionRule> categoryRule = commissionRuleRepository.findByCategoryId(categoryId);
        if (categoryRule.isPresent()) return categoryRule.get().getRate();

        return commissionRuleRepository.findByIsDefaultTrue()
                .stream()
                .findFirst()
                .map(CommissionRule::getRate)
                .orElse(DEFAULT_COMMISSION_RATE);
    }

    public BigDecimal calculateCommission(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate.divide(BigDecimal.valueOf(100)));
    }
}