package com.platform.shipping.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    private String trackingNumber;
    private String carrier;
    private String status;
    private List<TrackingEvent> events;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEvent {
        private Instant timestamp;
        private String location;
        private String description;
        private String status;
    }
}