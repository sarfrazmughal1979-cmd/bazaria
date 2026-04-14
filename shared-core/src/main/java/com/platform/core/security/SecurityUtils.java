package com.platform.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<UserContext> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserContext userContext) {
            return Optional.of(userContext);
        }
        return Optional.empty();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser()
                .map(UserContext::getUserId)
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));
    }

    public static Optional<UUID> getCurrentVendorId() {
        return getCurrentUser().map(UserContext::getVendorId);
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser()
                .map(UserContext::getEmail)
                .orElse("anonymous");
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserContext;
    }

    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    public static boolean hasPermission(String permission) {
        return getCurrentUser()
                .map(user -> user.hasPermission(permission))
                .orElse(false);
    }
}