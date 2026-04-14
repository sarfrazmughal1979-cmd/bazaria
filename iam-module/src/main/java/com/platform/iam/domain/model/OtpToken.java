package com.platform.iam.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otp_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token", nullable = false, length = 10)
    private String token;

    @Column(name = "purpose", nullable = false, length = 30)
    private String purpose; // EMAIL_VERIFICATION, PASSWORD_RESET, PHONE_VERIFICATION

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used")
    @Builder.Default
    private boolean used = false;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}