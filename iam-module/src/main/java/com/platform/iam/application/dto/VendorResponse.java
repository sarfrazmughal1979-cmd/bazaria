package com.platform.iam.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorResponse {

    private UUID id;
    private UUID userId;
    private String shopName;
    private String slug;
    private String shopDescription;
    private String shopLogoUrl;
    private String shopBannerUrl;
    private String status;
    private BigDecimal commissionRate;
    private BigDecimal rating;
    private int totalProducts;
    private int totalOrders;
    private Instant approvedAt;
    private Instant createdAt;
}