package edu.ntnu.idi.idatt2105.backend.core.compliance.log.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;

@Transactional
public abstract class BaseComplianceLogService<T extends BaseComplianceLog> {

    protected abstract BaseComplianceLogRepository<T> getRepository();

    @Transactional(readOnly = true)
    public List<T> getAllForCurrentOrg() {
        return getRepository().findAllByTenantId(TenantContext.getCurrentOrg());
    }

    @Transactional(readOnly = true)
    public T getById(Long id) {
        T log = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance log not found"));

        Long currentOrgId = TenantContext.getCurrentOrg();
        if (!log.getTenant().getId().equals(currentOrgId)) {
            throw new UnauthorizedException("You do not have access to this compliance log");
        }

        return log;
    }

    public void delete(Long id) {
        T log = getById(id);
        getRepository().delete(log);
    }
}

