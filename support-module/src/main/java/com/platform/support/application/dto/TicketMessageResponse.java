package com.platform.support.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageResponse {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String senderType;
    private String message;
    private boolean internal;
    private List<String> attachments;
    private boolean isRead;
    private boolean isInternal;
    private Instant createdAt;
}