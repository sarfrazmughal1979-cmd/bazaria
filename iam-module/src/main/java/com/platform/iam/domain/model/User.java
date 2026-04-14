package com.platform.iam.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_phone", columnList = "phone_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "phone_verified")
    private boolean phoneVerified;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
}