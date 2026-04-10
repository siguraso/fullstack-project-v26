package edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;

@NoRepositoryBean
public interface BaseComplianceLogRepository<T extends BaseComplianceLog> extends JpaRepository<T, Long> {

    List<T> findAllByTenantId(Long tenantId);

    Page<T> findAllByTenantId(Long tenantId, Pageable pageable);

    List<T> findAllByTenantIdAndModule(Long tenantId, ComplianceModule module);

    List<T> findAllByTenantIdAndStatus(Long tenantId, LogStatus status);

    long countByTenantIdAndStatus(Long tenantId, LogStatus status);
}
