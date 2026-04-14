package com.platform.support.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private UUID id;
    private UUID sessionId;
    private UUID senderId;
    private UUID recipientId;
    private String senderName;
    private String senderType;
    private String message;
    private Instant sentAt;
    private boolean delivered;
    private boolean read;
}