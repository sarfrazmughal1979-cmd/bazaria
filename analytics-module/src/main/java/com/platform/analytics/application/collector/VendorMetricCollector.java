package com.platform.analytics.application.collector;

import com.platform.analytics.domain.repository.VendorMetricRepository;
import com.platform.common.domain.event.ProductApprovedEvent;
import com.platform.common.domain.event.VendorApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendorMetricCollector {

    private final VendorMetricRepository vendorMetricRepository;

    @Async
    @EventListener
    @Transactional
    public void onVendorApproved(VendorApprovedEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        UUID vendorId = UUID.fromString(event.getVendorId());

        // Initialize vendor metrics
        var metric = vendorMetricRepository.findByVendorIdAndMetricDate(vendorId, today)
            .orElse(com.platform.analytics.domain.model.VendorMetric.builder()
                .vendorId(vendorId)
                .metricDate(today)
                .totalProducts(0)
                .activeProducts(0)
                .totalOrders(0)
                .totalRevenue(java.math.BigDecimal.ZERO)
                .totalCommission(java.math.BigDecimal.ZERO)
                .averageRating(java.math.BigDecimal.ZERO)
                .onTimeDeliveryRate(java.math.BigDecimal.ZERO)
                .disputeRate(java.math.BigDecimal.ZERO)
                .build());
        vendorMetricRepository.save(metric);
    }

    @Async
    @EventListener
    @Transactional
    public void onProductApproved(ProductApprovedEvent event) {
        LocalDate today = LocalDate.now();
        UUID vendorId = UUID.fromString(event.getVendorId());

        vendorMetricRepository.findByVendorIdAndMetricDate(vendorId, today)
            .ifPresent(metric -> {
                metric.setTotalProducts(metric.getTotalProducts() + 1);
                metric.setActiveProducts(metric.getActiveProducts() + 1);
                vendorMetricRepository.save(metric);
            });
    }
}