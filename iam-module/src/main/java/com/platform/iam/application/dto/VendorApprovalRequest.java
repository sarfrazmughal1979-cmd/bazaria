package com.platform.iam.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorApprovalRequest {

    @NotNull
    private Boolean approved;

    private String reason; // Rejection reason
    private java.math.BigDecimal commissionRate;
}