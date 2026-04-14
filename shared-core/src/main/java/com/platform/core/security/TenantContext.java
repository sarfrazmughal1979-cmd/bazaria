package com.platform.core.security;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    private TenantContext() {}

    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }
}