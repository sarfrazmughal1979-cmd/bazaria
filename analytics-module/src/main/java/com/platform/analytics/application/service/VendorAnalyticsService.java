package com.platform.analytics.application.service;

import com.platform.analytics.application.dto.ProductPerformance;
import com.platform.analytics.application.dto.VendorPerformanceResponse;
import com.platform.analytics.domain.repository.SalesMetricRepository;
import com.platform.analytics.domain.repository.VendorMetricRepository;
import com.platform.analytics.domain.repository.ProductMetricRepository;
import com.platform.core.client.RestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorAnalyticsService {

    private final SalesMetricRepository salesMetricRepository;
    private final VendorMetricRepository vendorMetricRepository;
    private final ProductMetricRepository productMetricRepository;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    private RestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    // DTO for IAM response
    private record VendorInfo(UUID vendorId, String shopName) {}

    public VendorPerformanceResponse getVendorPerformance(UUID vendorId, LocalDate startDate, LocalDate endDate) {
        var metrics = vendorMetricRepository.findByVendorIdAndMetricDateBetweenOrderByMetricDateAsc(
                vendorId, startDate, endDate);

        var summary = metrics.stream().reduce((a, b) -> {
            a.setTotalRevenue(a.getTotalRevenue().add(b.getTotalRevenue()));
            a.setTotalOrders(a.getTotalOrders() + b.getTotalOrders());
            a.setTotalCommission(a.getTotalCommission().add(b.getTotalCommission()));
            return a;
        }).orElseThrow(() -> new ResourceNotFoundException("Vendor metrics", "vendorId", vendorId));

        var topProducts = productMetricRepository.findTopProductsByVendor(
                vendorId, startDate, endDate, PageRequest.of(0, 10));

        // Fetch vendor name via REST
        String shopName = "Unknown Vendor";
        try {
            VendorInfo vendorInfo = iamRestClient.get(
                    "/api/v1/vendors/{vendorId}/info-mini", VendorInfo.class, vendorId);
            if (vendorInfo != null) {
                shopName = vendorInfo.shopName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch vendor name for {}: {}", vendorId, e.getMessage());
        }

        return VendorPerformanceResponse.builder()
                .vendorId(vendorId.toString())
                .shopName(shopName)
                .totalRevenue(summary.getTotalRevenue())
                .totalOrders(summary.getTotalOrders())
                .totalCommission(summary.getTotalCommission())
                .averageRating(summary.getAverageRating())
                .onTimeDeliveryRate(summary.getOnTimeDeliveryRate())
                .disputeRate(summary.getDisputeRate())
                .topProducts(topProducts.getContent().stream()
                        .map(row -> ProductPerformance.builder()
                                .productId(UUID.fromString(row[0].toString()))
                                .quantitySold((Long) row[1])
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}