package com.platform.analytics.application.collector;

import com.platform.analytics.domain.model.ProductMetric;
import com.platform.analytics.domain.model.SalesMetric;
import com.platform.analytics.domain.model.VendorMetric;
import com.platform.analytics.domain.repository.ProductMetricRepository;
import com.platform.analytics.domain.repository.SalesMetricRepository;
import com.platform.analytics.domain.repository.VendorMetricRepository;
import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMetricCollector {

    private final SalesMetricRepository salesMetricRepository;
    private final VendorMetricRepository vendorMetricRepository;
    private final ProductMetricRepository productMetricRepository;
    private final RestClientFactory restClientFactory;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    // DTO classes for Order service response (must match the actual response structure)
    private record OrderDetailResponse(
            UUID orderId,
            String orderNumber,
            UUID customerId,
            BigDecimal totalAmount,
            List<SubOrderDetailResponse> subOrders
    ) {}

    private record SubOrderDetailResponse(
            UUID vendorId,
            List<OrderItemDetailResponse> items
    ) {}

    private record OrderItemDetailResponse(
            UUID productId,
            int quantity,
            BigDecimal totalPrice
    ) {}

    @Async
    @EventListener
    @Transactional
    public void onOrderPlaced(OrderPlacedEvent event) {
        try {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            UUID orderId = UUID.fromString(event.getOrderId());

            // Fetch full order details via REST
            OrderDetailResponse orderDetail = orderRestClient.get(
                    "/api/v1/orders/{orderId}/detail", OrderDetailResponse.class, orderId);

            if (orderDetail == null || orderDetail.subOrders() == null) {
                log.warn("No sub-order details found for order {}", orderId);
                return;
            }

            // Platform-wide sales metric
            updateSalesMetric(null, today, event.getTotalAmount(), 1,
                    0, 0, orderDetail.subOrders().stream()
                            .flatMap(s -> s.items().stream())
                            .mapToLong(OrderItemDetailResponse::quantity)
                            .sum(),
                    1, event.getCustomerId());

            // Process each sub-order
            for (SubOrderDetailResponse subOrder : orderDetail.subOrders()) {
                UUID vendorId = subOrder.vendorId();
                BigDecimal subTotal = BigDecimal.ZERO;
                long itemsSold = 0;
                long subOrderCount = 1; // count this sub-order as 1 order

                for (OrderItemDetailResponse item : subOrder.items()) {
                    subTotal = subTotal.add(item.totalPrice());
                    itemsSold += item.quantity();

                    // Update product metrics
                    updateProductMetric(item.productId(), vendorId, today,
                            0, 0, item.quantity(), item.quantity(), item.totalPrice());
                }

                // Update vendor metrics
                updateVendorMetric(vendorId, today, subTotal, subOrderCount,
                        0, 0, itemsSold, 0);
            }
        } catch (Exception e) {
            log.error("Failed to collect order metrics for order {}", event.getOrderId(), e);
        }
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
        metric.setUniqueCustomers(metric.getUniqueCustomers() + uniqueCustomers);
        metric.setAverageOrderValue(metric.getTotalOrders() > 0
                ? metric.getTotalRevenue().divide(BigDecimal.valueOf(metric.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP)
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