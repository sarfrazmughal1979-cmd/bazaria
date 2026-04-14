package com.platform.iam.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String status;
    private boolean emailVerified;
    private Set<String> roles;
    private Instant lastLoginAt;
    private Instant createdAt;
}