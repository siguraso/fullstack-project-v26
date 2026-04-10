package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;

public interface DeviationRepository extends JpaRepository<Deviation, Long> {

    @EntityGraph(attributePaths = {"createdBy"})
    List<Deviation> findByTenantId(Long tenantId);

    @EntityGraph(attributePaths = {"createdBy"})
    List<Deviation> findByTenantIdAndStatusInOrderByCreatedAtDesc(Long tenantId, List<DeviationStatus> statuses);

    @EntityGraph(attributePaths = {"createdBy"})
    List<Deviation> findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
            Long tenantId,
            List<DeviationStatus> statuses,
            DeviationSeverity severity);

    @EntityGraph(attributePaths = {"createdBy"})
    Page<Deviation> findByTenantId(Long tenantId, Pageable pageable);
}
