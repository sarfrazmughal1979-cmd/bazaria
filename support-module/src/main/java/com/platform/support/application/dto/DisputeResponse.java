package com.platform.support.application.dto;

import com.platform.support.domain.model.DisputeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisputeResponse {
    private UUID id;
    private String disputeNumber;
    private UUID orderId;
    private UUID subOrderId;
    private UUID customerId;
    private String customerName;
    private UUID vendorId;
    private String vendorName;
    private String reason;
    private String description;
    private BigDecimal disputedAmount;
    private DisputeStatus status;
    private String resolution;
    private BigDecimal resolutionAmount;
    private List<String> evidenceUrls;
    private Instant createdAt;
    private Instant resolvedAt;
    private UUID resolvedBy;


}