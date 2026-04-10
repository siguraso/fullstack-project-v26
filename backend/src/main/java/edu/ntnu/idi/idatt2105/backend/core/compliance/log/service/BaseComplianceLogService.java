package edu.ntnu.idi.idatt2105.backend.core.compliance.log.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base service providing common CRUD operations for compliance log
 * entities.
 * <p>
 * Concrete subclasses supply the repository via {@link #getRepository()} and
 * may add module-specific creation logic. All operations enforce tenant
 * ownership.
 *
 * @param <T> the concrete compliance log entity type
 */
@Transactional
@Slf4j
public abstract class BaseComplianceLogService<T extends BaseComplianceLog> {

    /**
     * Returns the repository used for persistence of the concrete log type.
     *
     * @return the {@link BaseComplianceLogRepository} for type {@code T}
     */
    protected abstract BaseComplianceLogRepository<T> getRepository();

    /**
     * Returns all compliance logs for the current tenant.
     *
     * @return a list of all log entities belonging to the current organisation
     */
    @Transactional(readOnly = true)
    public List<T> getAllForCurrentOrg() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<T> logs = getRepository().findAllByTenantId(tenantId);
        log.debug("Fetched {} compliance logs for tenantId={}", logs.size(), tenantId);
        return logs;
    }

    /**
     * Retrieves a single compliance log by its identifier, enforcing that it
     * belongs to the current tenant.
     *
     * @param id the log identifier
     * @return the log entity
     */
    @Transactional(readOnly = true)
    public T getById(Long id) {
        log.debug("Fetching compliance log id={}", id);
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance log not found"));

        Long currentOrgId = TenantContext.getCurrentOrg();
        if (!entity.getTenant().getId().equals(currentOrgId)) {
            log.warn("Access denied to compliance log id={} for tenantId={}", id, currentOrgId);
            throw new UnauthorizedException("You do not have access to this compliance log");
        }

        return entity;
    }

    /**
     * Deletes a compliance log entry, enforcing that it belongs to the current
     * tenant.
     *
     * @param id identifier of the log to delete
     */
    public void delete(Long id) {
        log.info("Deleting compliance log id={}", id);
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance log not found"));

        Long currentOrgId = TenantContext.getCurrentOrg();
        if (!entity.getTenant().getId().equals(currentOrgId)) {
            log.warn("Access denied to compliance log id={} for tenantId={}", id, currentOrgId);
            throw new UnauthorizedException("You do not have access to this compliance log");
        }

        getRepository().delete(entity);
        log.info("Deleted compliance log id={}", id);
    }
}

