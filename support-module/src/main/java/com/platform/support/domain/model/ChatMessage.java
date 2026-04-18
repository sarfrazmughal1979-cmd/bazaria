package com.platform.support.domain.model;
import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false, insertable = false,updatable = false)
    private ChatSession session;


    @Column(name = "session_id", nullable = false, insertable = false,updatable = false)
    private UUID sessionId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "sender_type", nullable = false)
    private String senderType;  // CUSTOMER, VENDOR, AGENT

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "read_at")
    private Instant readAt;
}