package com.platform.analytics.application.collector;

import com.platform.analytics.domain.model.ProductMetric;
import com.platform.analytics.domain.repository.ProductMetricRepository;
import com.platform.common.domain.event.ProductAddedToCartEvent;
import com.platform.common.domain.event.ProductViewedEvent;
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
public class ProductMetricCollector {

    private final ProductMetricRepository productMetricRepository;

    @Async
    @EventListener
    @Transactional
    public void onProductViewed(ProductViewedEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        updateProductMetric(UUID.fromString(event.getProductId()), UUID.fromString(event.getVendorId()), today, 1, 0, 0, 0, BigDecimal.ZERO);
    }

    @Async
    @EventListener
    @Transactional
    public void onProductAddedToCart(ProductAddedToCartEvent event) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        updateProductMetric(UUID.fromString(event.getProductId()), UUID.fromString(event.getVendorId()), today, 0, 1, 0, 0, BigDecimal.ZERO);
    }

    private void updateProductMetric(UUID productId, UUID vendorId, LocalDate date,
                                     long views, long addToCarts, long orders,
                                     long quantitySold, BigDecimal revenue) {
        ProductMetric metric = productMetricRepository
            .findByProductIdAndMetricDateBetween(productId, date, date)
            .stream().findFirst()
            .orElse(ProductMetric.builder()
                .productId(productId)
                .vendorId(vendorId)
                .metricDate(date)
                .views(0)
                .addToCarts(0)
                .orders(0)
                .quantitySold(0)
                .revenue(BigDecimal.ZERO)
                .build());

        metric.setViews(metric.getViews() + views);
        metric.setAddToCarts(metric.getAddToCarts() + addToCarts);
        metric.setOrders(metric.getOrders() + orders);
        metric.setQuantitySold(metric.getQuantitySold() + quantitySold);
        metric.setRevenue(metric.getRevenue().add(revenue));

        if (metric.getViews() > 0) {
            double conversion = (double) metric.getOrders() / metric.getViews() * 100;
            metric.setConversionRate(BigDecimal.valueOf(conversion));
        }

        productMetricRepository.save(metric);
    }
}