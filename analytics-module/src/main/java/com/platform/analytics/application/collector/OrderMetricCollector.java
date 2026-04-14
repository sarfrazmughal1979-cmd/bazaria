package com.platform.analytics.application.collector;

import com.platform.analytics.domain.model.SalesMetric;
import com.platform.analytics.domain.model.VendorMetric;
import com.platform.analytics.domain.model.ProductMetric;
import com.platform.analytics.domain.repository.SalesMetricRepository;
import com.platform.analytics.domain.repository.VendorMetricRepository;
import com.platform.analytics.domain.repository.ProductMetricRepository;
import com.platform.common.domain.event.OrderDeliveredEvent;
import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.core.event.DomainEventPublisher;
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
public class OrderMetricCollector {

    private final SalesMetricRepository salesMetricRepository;
    private final VendorMetricRepository vendorMetricRepository;
    private final ProductMetricRepository productMetricRepository;

    @Async
    @EventListener
    @Transactional
    public void onOrderPlaced(OrderPlacedEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        BigDecimal orderAmount = event.getTotalAmount();

        // Update platform-wide sales metric
        updateSalesMetric(null, today, orderAmount, 1, 0, 0, 0, 1, event.getCustomerId());

        // For each vendor, update vendor metric (from sub-orders)
        // This would iterate over sub-orders in the event payload
        // Simplified: assume we have vendorId and amount
        UUID vendorId = extractVendorIdFromEvent(event);
        if (vendorId != null) {
            updateVendorMetric(vendorId, today, orderAmount, 0, 0, 0, 0, 0);
        }

        // Update product metrics for each item in the order
        // This would be done via a separate event with line items
    }

    @Async
    @EventListener
    @Transactional
    public void onOrderDelivered(OrderDeliveredEvent event) {
        // Update on-time delivery metrics
        LocalDate today = LocalDate.now();
        // Update vendor metric's on_time_delivery_rate
        // This requires tracking promised vs actual delivery dates
    }

    private void updateSalesMetric(UUID vendorId, LocalDate date, BigDecimal revenue,
                                   long orders, long commission, long tax,
                                   long itemsSold, long uniqueCustomers, String customerId) {
        SalesMetric metric = salesMetricRepository
            .findByMetricDateAndPeriodTypeAndVendorId(date, "DAY", vendorId)
            .orElse(SalesMetric.builder()
                .metricDate(date)
                .periodType("DAY")
                .vendorId(vendorId)
                .totalRevenue(BigDecimal.ZERO)
                .totalOrders(0)
                .totalCommission(BigDecimal.ZERO)
                .totalTax(BigDecimal.ZERO)
                .itemsSold(0)
                .uniqueCustomers(0)
                .build());

        metric.setTotalRevenue(metric.getTotalRevenue().add(revenue));
        metric.setTotalOrders(metric.getTotalOrders() + orders);
        metric.setTotalCommission(metric.getTotalCommission().add(BigDecimal.valueOf(commission)));
        metric.setTotalTax(metric.getTotalTax().add(BigDecimal.valueOf(tax)));
        metric.setItemsSold(metric.getItemsSold() + itemsSold);
        if (uniqueCustomers > 0) {
            // Simplified: would need to track unique customer IDs per day
            metric.setUniqueCustomers(metric.getUniqueCustomers() + uniqueCustomers);
        }
        metric.setAverageOrderValue(metric.getTotalOrders() > 0
            ? metric.getTotalRevenue().divide(BigDecimal.valueOf(metric.getTotalOrders()), 2, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO);

        salesMetricRepository.save(metric);
    }

    private void updateVendorMetric(UUID vendorId, LocalDate date, BigDecimal revenue,
                                    long orders, long commission, long tax,
                                    long productsSold, long newProducts) {
        VendorMetric metric = vendorMetricRepository
            .findByVendorIdAndMetricDate(vendorId, date)
            .orElse(VendorMetric.builder()
                .vendorId(vendorId)
                .metricDate(date)
                .totalRevenue(BigDecimal.ZERO)
                .totalOrders(0)
                .totalCommission(BigDecimal.ZERO)
                .totalProducts(0)
                .activeProducts(0)
                .build());

        metric.setTotalRevenue(metric.getTotalRevenue().add(revenue));
        metric.setTotalOrders(metric.getTotalOrders() + orders);
        metric.setTotalCommission(metric.getTotalCommission().add(BigDecimal.valueOf(commission)));
        if (newProducts > 0) {
            metric.setTotalProducts(metric.getTotalProducts() + (int) newProducts);
        }
        vendorMetricRepository.save(metric);
    }

    private UUID extractVendorIdFromEvent(OrderPlacedEvent event) {
        // In real implementation, the event would contain sub-order vendor IDs
        // For now, return a dummy or fetch from order repository
        return null;
    }
}