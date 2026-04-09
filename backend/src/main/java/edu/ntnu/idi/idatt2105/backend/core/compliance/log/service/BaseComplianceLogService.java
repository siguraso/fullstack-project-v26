package edu.ntnu.idi.idatt2105.backend.core.compliance.log.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public abstract class BaseComplianceLogService<T extends BaseComplianceLog> {

    protected abstract BaseComplianceLogRepository<T> getRepository();

    @Transactional(readOnly = true)
    public List<T> getAllForCurrentOrg() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<T> logs = getRepository().findAllByTenantId(tenantId);
        log.debug("Fetched {} compliance logs for tenantId={}", logs.size(), tenantId);
        return logs;
    }

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

