package com.platform.core.security;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    private UUID userId;
    private String email;
    private String fullName;
    private UUID vendorId;
    private Set<String> roles;
    private Set<String> permissions;

    public boolean isVendor() {
        return roles != null && roles.contains("VENDOR");
    }

    public boolean isAdmin() {
        return roles != null && (roles.contains("ADMIN") || roles.contains("SUPER_ADMIN"));
    }

    public boolean isCustomer() {
        return roles != null && roles.contains("CUSTOMER");
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasAnyRole(String... roleNames) {
        if (roles == null) return false;
        for (String role : roleNames) {
            if (roles.contains(role)) return true;
        }
        return false;
    }
}