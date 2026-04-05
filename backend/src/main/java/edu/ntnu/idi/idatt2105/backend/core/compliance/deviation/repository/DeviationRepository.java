package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;

public interface DeviationRepository extends JpaRepository<Deviation, Long> {

    List<Deviation> findByTenantId(Long tenantId);

    List<Deviation> findByTenantIdAndStatusInOrderByCreatedAtDesc(Long tenantId, List<DeviationStatus> statuses);

    List<Deviation> findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
            Long tenantId,
            List<DeviationStatus> statuses,
            DeviationSeverity severity);

    Page<Deviation> findByTenantId(Long tenantId, Pageable pageable);
}
