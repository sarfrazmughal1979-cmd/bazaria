package com.platform.shipping.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierWebhookPayload {
    private String trackingNumber;
    private String carrier;
    private String newStatus;
    private String location;
    private String description;
    private String rawData;
}