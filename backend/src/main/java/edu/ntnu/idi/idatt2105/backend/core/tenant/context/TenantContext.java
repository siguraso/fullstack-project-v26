package edu.ntnu.idi.idatt2105.backend.core.tenant.context;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;

/**
 * Holds the current tenant (organization) context for the executing thread.
 *
 * <p>
 * Uses a {@link ThreadLocal} to store the tenant ID for the duration of a
 * request.
 * Typically populated by authentication/filters and cleared after the request
 * completes.
 * </p>
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_ORG = new ThreadLocal<>();

    private TenantContext() {
    }

    /**
     * Sets the current organization (tenant) ID for this thread.
     *
     * @param organizationId the tenant ID to associate with the current execution
     *                       context
     */
    public static void setCurrentOrg(Long organizationId) {
        CURRENT_ORG.set(organizationId);
    }

    /**
     * Returns the current organization (tenant) ID.
     *
     * @return the tenant ID associated with this thread
     * @throws UnauthorizedException if no tenant context is set
     */
    public static Long getCurrentOrg() {
        Long organizationId = CURRENT_ORG.get();
        if (organizationId == null) {
            throw new UnauthorizedException("Organization context is missing");
        }
        return organizationId;
    }

    /**
     * Clears the tenant context from the current thread.
     *
     * <p>
     * Must be called after request completion to avoid leaking tenant data
     * across threads in pooled environments.
     * </p>
     */
    public static void clear() {
        CURRENT_ORG.remove();
    }
}