package com.platform.pricing.application.service;

import com.platform.pricing.application.dto.TaxCalculationRequest;
import com.platform.pricing.application.dto.TaxCalculationResponse;
import com.platform.pricing.domain.model.TaxRule;
import com.platform.pricing.domain.repository.TaxRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaxService {

    private final TaxRuleRepository taxRuleRepository;

    @Value("${platform.tax.default-rate:15.0}")
    private BigDecimal defaultRate;

    public TaxCalculationResponse calculateTax(TaxCalculationRequest request) {
        List<TaxRule> rules = taxRuleRepository.findApplicableRules(
                request.getCountryCode(),
                request.getStateCode(),
                request.getCategoryId());

        BigDecimal appliedRate = defaultRate;
        String taxType = "VAT";
        if (!rules.isEmpty()) {
            TaxRule topRule = rules.get(0); // highest priority
            appliedRate = topRule.getRate();
            taxType = topRule.getTaxType();
        }

        BigDecimal taxAmount = request.getSubtotal()
                .multiply(appliedRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = request.getSubtotal().add(taxAmount);

        return TaxCalculationResponse.builder()
                .subtotal(request.getSubtotal())
                .taxAmount(taxAmount)
                .total(total)
                .appliedRate(appliedRate)
                .taxType(taxType)
                .currency("PKR")
                .build();
    }
}