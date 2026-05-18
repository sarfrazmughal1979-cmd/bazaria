package com.platform.fraud.application.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class OrderContext {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private String ipAddress;
    private String shippingCountry;
    private Instant customerCreatedAt;
    private int recentOrderCount;       // velocity (orders in last hour)
    private Map<String, Object> extras; // for any additional fields

    public Object getValue(String field) {
        return switch (field) {
            case "amount" -> amount != null ? amount.doubleValue() : 0.0;
            case "geo" -> ipAddress;
            case "velocity" -> recentOrderCount;
            case "shippingCountry" -> shippingCountry;
            case "customerAgeDays" -> {
                if (customerCreatedAt == null) yield 0;
                yield java.time.Duration.between(customerCreatedAt, Instant.now()).toDays();
            }
            default -> extras != null ? extras.get(field) : null;
        };
    }
}