package edu.ntnu.idi.idatt2105.backend.core.tenant.context;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;

public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_ORG = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentOrg(Long organizationId) {
        CURRENT_ORG.set(organizationId);
    }

    public static Long getCurrentOrg() {
        Long organizationId = CURRENT_ORG.get();
        if (organizationId == null) {
            throw new UnauthorizedException("Organization context is missing");
        }
        return organizationId;
    }

    public static void clear() {
        CURRENT_ORG.remove();
    }
}

