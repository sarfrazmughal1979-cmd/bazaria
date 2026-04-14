package com.platform.analytics.application.collector;

import com.platform.analytics.domain.model.CustomerMetric;
import com.platform.analytics.domain.repository.CustomerMetricRepository;
import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.common.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerMetricCollector {

    private final CustomerMetricRepository customerMetricRepository;

    @Async
    @EventListener
    @Transactional
    public void onUserRegistered(UserRegisteredEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        UUID customerId = UUID.fromString(event.getUserId());

        CustomerMetric metric = CustomerMetric.builder()
            .customerId(customerId)
            .metricDate(today)
            .totalOrders(0)
            .totalSpent(BigDecimal.ZERO)
            .lastOrderDate(null)
            .active(true)
            .build();
        customerMetricRepository.save(metric);
    }

    @Async
    @EventListener
    @Transactional
    public void onOrderPlaced(OrderPlacedEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        UUID customerId = UUID.fromString(event.getCustomerId());

        CustomerMetric metric = customerMetricRepository.findById(customerId)
            .orElse(CustomerMetric.builder()
                .customerId(customerId)
                .metricDate(today)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build());

        metric.setTotalOrders(metric.getTotalOrders() + 1);
        metric.setTotalSpent(metric.getTotalSpent().add(event.getTotalAmount()));
        metric.setLastOrderDate(today);
        metric.setActive(true);
        customerMetricRepository.save(metric);
    }
}