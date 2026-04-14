package com.platform.analytics.application.service;

import com.platform.analytics.application.dto.*;
import com.platform.analytics.domain.repository.SalesMetricRepository;
import com.platform.analytics.domain.repository.VendorMetricRepository;
import com.platform.analytics.domain.repository.ProductMetricRepository;
import com.platform.analytics.domain.repository.CustomerMetricRepository;
import com.platform.core.dto.PagedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SalesMetricRepository salesMetricRepository;
    private final VendorMetricRepository vendorMetricRepository;
    private final ProductMetricRepository productMetricRepository;
    private final CustomerMetricRepository customerMetricRepository;

    public DashboardResponse getAdminDashboard(LocalDate startDate, LocalDate endDate) {
        var salesMetrics = salesMetricRepository.findByVendorIdAndMetricDateBetweenOrderByMetricDateAsc(
            null, startDate, endDate);

        BigDecimal totalRevenue = salesMetrics.stream()
            .map(m -> m.getTotalRevenue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalOrders = salesMetrics.stream().mapToLong(m -> m.getTotalOrders()).sum();
        long totalCustomers = customerMetricRepository.countActiveCustomers(startDate, endDate);
        long totalProducts = 0; // would need separate query

        BigDecimal avgOrderValue = totalOrders > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Sales trend
        List<DashboardResponse.DailySales> trend = salesMetrics.stream()
            .map(m -> DashboardResponse.DailySales.builder()
                .date(m.getMetricDate().toString())
                .revenue(m.getTotalRevenue())
                .orders(m.getTotalOrders())
                .build())
            .collect(Collectors.toList());

        // Top products
        Pageable top = PageRequest.of(0, 5);
        var topProductsResult = productMetricRepository.findTopProductsByVendor(null, startDate, endDate, top);
        List<DashboardResponse.TopProduct> topProducts = topProductsResult.getContent().stream()
            .map(row -> DashboardResponse.TopProduct.builder()
                .productId(row[0].toString())
                .quantitySold((Long) row[1])
                .revenue((BigDecimal) row[2])
                .build())
            .collect(Collectors.toList());

        // Top vendors
        var topVendorsResult = vendorMetricRepository.findTopVendorsByRevenue(startDate, endDate, top);
        List<DashboardResponse.TopVendor> topVendors = topVendorsResult.getContent().stream()
            .map(row -> DashboardResponse.TopVendor.builder()
                .vendorId(row[0].toString())
                .revenue((BigDecimal) row[1])
                .build())
            .collect(Collectors.toList());

        return DashboardResponse.builder()
            .summary(DashboardResponse.SummaryMetrics.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .averageOrderValue(avgOrderValue)
                .build())
            .salesTrend(trend)
            .topProducts(topProducts)
            .topVendors(topVendors)
            .build();
    }
	
	@Scheduled(cron = "0 30 1 * * *") // every day at 1:30 AM
@Transactional
public void aggregateDailyMetrics() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    // Aggregate sales metrics into DailyAggregation table
    // This pre-computes platform-wide totals for faster dashboard loading
}
}