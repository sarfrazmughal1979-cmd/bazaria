package com.platform.fraud.application.service;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.fraud.domain.model.ConditionNode;
import com.platform.fraud.domain.model.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEvaluationEngine {

//    private final ObjectMapper objectMapper;

    public EvaluationResult evaluate(FraudRule rule, OrderContext ctx) {
        if (rule.getConditions() == null) {
            return EvaluationResult.empty(); // fallback to old logic
        }

        boolean matched = evaluateNode(rule.getConditions(), ctx);
        return matched ? new EvaluationResult(rule.getAction(), rule.getPoints(), rule.getRuleName()) : EvaluationResult.empty();
    }

    private boolean evaluateNode(ConditionNode node, OrderContext ctx) {
        if (node instanceof ConditionNode.Leaf leaf) {
            return evaluateLeaf(leaf, ctx);
        } else if (node instanceof ConditionNode.Composite composite) {
            List<ConditionNode> children = composite.getChildren();
            if (children == null || children.isEmpty()) return true;

            boolean result = "AND".equalsIgnoreCase(composite.getOperator());
            for (ConditionNode child : children) {
                boolean childResult = evaluateNode(child, ctx);
                if ("AND".equalsIgnoreCase(composite.getOperator())) {
                    result = result && childResult;
                } else { // OR
                    result = result || childResult;
                }
            }
            return result;
        }
        return false;
    }

    private boolean evaluateLeaf(ConditionNode.Leaf leaf, OrderContext ctx) {
        Object fieldValue = ctx.getValue(leaf.getField());
        Object ruleValue = leaf.getValue();

        if (fieldValue == null || ruleValue == null) return false;

        String op = leaf.getOperator().toUpperCase();
        try {
            return switch (op) {
                case "GT" -> compare(fieldValue, ruleValue) > 0;
                case "LT" -> compare(fieldValue, ruleValue) < 0;
                case "GTE" -> compare(fieldValue, ruleValue) >= 0;
                case "LTE" -> compare(fieldValue, ruleValue) <= 0;
                case "EQ" -> fieldValue.toString().equalsIgnoreCase(ruleValue.toString());
                case "NEQ" -> !fieldValue.toString().equalsIgnoreCase(ruleValue.toString());
                case "IN" -> {
                    if (ruleValue instanceof List<?> list) {
                        yield list.stream().anyMatch(item -> item.toString().equalsIgnoreCase(fieldValue.toString()));
                    }
                    throw new IllegalArgumentException("IN operator requires a list value");
                }
                case "CONTAINS" -> fieldValue.toString().toLowerCase().contains(ruleValue.toString().toLowerCase());
                case "STARTSWITH" -> fieldValue.toString().toLowerCase().startsWith(ruleValue.toString().toLowerCase());
                case "REGEX" -> fieldValue.toString().matches(ruleValue.toString());
                default -> throw new IllegalArgumentException("Unknown operator: " + op);
            };
        } catch (Exception e) {
            log.error("Failed to evaluate condition: field={}, operator={}, value={}", leaf.getField(), op, ruleValue, e);
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compare(Object a, Object b) {
        if (a instanceof Number number1 && b instanceof Number number2) {
            return Double.compare(number1.doubleValue(), number2.doubleValue());
        }
        if (a instanceof Comparable comparable1 && b instanceof Comparable) {
            return comparable1.compareTo(b);
        }
        return a.toString().compareTo(b.toString());
    }

    public record EvaluationResult(String action, int points, String ruleName) {
        public static EvaluationResult empty() {
            return new EvaluationResult("NONE", 0, null);
        }
    }
}