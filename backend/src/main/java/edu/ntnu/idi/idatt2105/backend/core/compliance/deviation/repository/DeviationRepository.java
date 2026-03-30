package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;

public interface DeviationRepository extends JpaRepository<Deviation, Long> {

    List<Deviation> findByTenantId(Long tenantId);

    List<Deviation> findByTenantIdAndModule(Long tenantId, ComplianceModule module);

    List<Deviation> findByTenantIdAndStatus(Long tenantId, boolean completed);
}
