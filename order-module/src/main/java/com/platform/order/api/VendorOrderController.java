package com.platform.order.api;

import com.platform.common.application.dto.ProductResponse;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.BusinessException;
import com.platform.core.security.SecurityUtils;
import com.platform.order.domain.model.SubOrder;
import com.platform.order.domain.repository.SubOrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import org.springframework.data.web.PageableDefault;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vendor/orders")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorOrderController {

    private final SubOrderRepository subOrderRepository;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;
    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    private ResilientRestClient iamRestClient;
    private ResilientRestClient catalogClient;
    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
        catalogClient = restClientFactory.create(catalogBaseUrl, 10);
    }

    // ---------- DTOs ----------
    // We'll use a simple record to hold the combined info
    public record VendorOrderDTO(
            UUID subOrderId,
            UUID orderId,
            String orderNumber,
            UUID customerId,
            String customerName,
            String status,
            BigDecimal subtotal,
            Instant createdAt
    ) {}

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VendorOrderDTO>>> getVendorOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Current user is not a vendor"));

        Page<SubOrder> subOrders = subOrderRepository.findByVendorId(vendorId, pageable);

        Page<VendorOrderDTO> dtoPage = subOrders.map(so -> {
            String customerName = "Unknown";
            try {
                // fetch customer name via IAM module (using a small info endpoint)
                var user = iamRestClient.get(
                        "/api/v1/users/{userId}/info-mini", UserInfo.class, so.getOrder().getCustomerId());
                if (user != null) customerName = user.fullName();
            } catch (Exception e) {
                // ignore, keep "Unknown"
            }
            return new VendorOrderDTO(
                    so.getId(),
                    so.getOrder().getId(),
                    so.getOrder().getOrderNumber(),
                    so.getOrder().getCustomerId(),
                    customerName,
                    so.getStatus().name(),
                    so.getSubtotal().getAmount(),
                    so.getCreatedAt()
            );
        });

        return ResponseEntity.ok(ApiResponse.success(dtoPage));
    }
    @GetMapping("/api/v1/vendor/products")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getVendorProducts(@PageableDefault(size=20) Pageable pageable) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Not a vendor"));
        String path = "/api/v1/products/vendor/{vendorId}?page={page}&size={size}&sort={sort}";

        ParameterizedTypeReference<ApiResponse<PagedResponse<ProductResponse>>> typeRef =
                new ParameterizedTypeReference<>() {};

        ApiResponse<PagedResponse<ProductResponse>> response = catalogClient.get(
                path,
                ApiResponse.class,
                vendorId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString().replace(": ", ",")   // optionally convert sort
        );

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch vendor products from Catalog");
        }

        return ResponseEntity.ok(response);
    }
    // Helper DTO for IAM response
    private record UserInfo(UUID userId, String fullName, String email) {}
}