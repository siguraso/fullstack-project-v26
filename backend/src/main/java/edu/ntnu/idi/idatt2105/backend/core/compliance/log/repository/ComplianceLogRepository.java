package edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.ComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;

public interface ComplianceLogRepository extends JpaRepository<ComplianceLog, Long> {

    List<ComplianceLog> findByTenantId(Long tenantId);

    List<ComplianceLog> findByIdAndModule(Long tenantId, ComplianceModule module);
}
