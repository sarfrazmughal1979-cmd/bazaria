package com.platform.support.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisputeRequest {
    @NotNull
    private UUID orderId;
    private UUID subOrderId;
    @NotBlank
    private String reason;
    private String description;
    @NotNull
    private BigDecimal disputedAmount;
    private String currency;
    private List<String> evidenceUrls;
}