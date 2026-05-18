package com.platform.fraud.application.service;

import com.platform.fraud.application.dto.FraudEvaluationResponse;
import com.platform.fraud.domain.model.FraudCheck;
import com.platform.fraud.domain.model.FraudRule;
import com.platform.fraud.domain.repository.FraudCheckRepository;
import com.platform.fraud.domain.repository.FraudRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudService {

    private final FraudRuleRepository ruleRepository;
    private final FraudCheckRepository checkRepository;
    private final FraudEvaluationEngine engine;
    @Value("${platform.fraud.auto-approve-score-below:30}")
    private int autoApproveBelow;

    @Value("${platform.fraud.auto-reject-score-above:80}")
    private int autoRejectAbove;

    public FraudEvaluationResponse evaluateOrder(UUID orderId, UUID customerId, double amount, String ipAddress) {
        List<String> triggered = new ArrayList<>();
        int totalScore = 0;
        String status="";
        for (FraudRule rule : ruleRepository.findByActiveTrue()) {
            if (rule.getConditions() != null) {
                // Dynamic rule – use engine
                OrderContext ctx = buildContext(customerId, amount, ipAddress);
                FraudEvaluationEngine.EvaluationResult result = engine.evaluate(rule, ctx);
                if (!result.action().equals("NONE")) {
                    totalScore += result.points();
                    triggered.add(rule.getRuleName());
                    if ("BLOCK".equals(result.action())) {
                        status = "REJECTED";
                        break; // no need to process further rules if blocked
                    }
                }
            } else {
                // Backward compatible static rule (old logic)
                int ruleScore = evaluateRule(rule, amount, ipAddress, customerId);
                if (ruleScore > 0) {
                    totalScore += ruleScore;
                    triggered.add(rule.getRuleName());
                }
            }
        }


        if (!status.equalsIgnoreCase("REJECTED")) {
            if (totalScore <= autoApproveBelow) status = "APPROVED";
            else if (totalScore >= autoRejectAbove) status = "REJECTED";
            else status = "REVIEW";
        }

        FraudCheck check = FraudCheck.builder()
                .orderId(orderId).customerId(customerId)
                .riskScore(totalScore).status(status)
                .reasons(String.join(",", triggered)).build();
        checkRepository.save(check);

        return FraudEvaluationResponse.builder()
                .orderId(orderId.toString())
                .riskScore(totalScore).status(status)
                .reasons(String.join(", ", triggered)).build();
    }

    private int evaluateRule(FraudRule rule, double amount, String ipAddress, UUID customerId) {
        // Simplified rules Ã¢â‚¬â€œ extend for velocity, device fingerprint, etc.
        switch (rule.getRuleType()) {
            case "AMOUNT": return amount > 50000 ? rule.getPoints() : 0;
            case "GEO":    return ipAddress.startsWith("10.") ? rule.getPoints() : 0;
            case "VELOCITY": {
                long recent = checkRepository.countByCustomerIdAndCheckedAtAfter(customerId, Instant.now().minus(1, ChronoUnit.HOURS));
                return recent >= 5 ? rule.getPoints() : 0;
            }
            default: return 0;
        }
    }

    public void reviewCheck(UUID checkId, boolean approved, UUID reviewerId, String notes) {
        FraudCheck check = checkRepository.findById(checkId).orElseThrow();
        check.setStatus(approved ? "APPROVED" : "REJECTED");
        check.setReviewedBy(reviewerId);
        check.setReviewNotes(notes);
        check.setReviewedAt(java.time.Instant.now());
        checkRepository.save(check);
    }
    private OrderContext buildContext(UUID customerId, double amount, String ipAddress) {
        return OrderContext.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(amount))
                .ipAddress(ipAddress)
                .recentOrderCount((int) checkRepository.countByCustomerIdAndCheckedAtAfter(customerId, Instant.now().minus(1, ChronoUnit.HOURS)))
                .build();
    }
}