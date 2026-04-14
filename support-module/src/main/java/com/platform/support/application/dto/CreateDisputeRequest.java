package com.platform.support.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDisputeRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID vendorId;
    @NotBlank
    private String reason;
    private String description;
}