package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;

public interface AlcoholLogRepository extends BaseComplianceLogRepository<AlcoholComplianceLog> {

	@Override
	@EntityGraph(attributePaths = {"recordedBy"})
	List<AlcoholComplianceLog> findAllByTenantId(Long tenantId);

	@Override
	@EntityGraph(attributePaths = {"recordedBy"})
	Page<AlcoholComplianceLog> findAllByTenantId(Long tenantId, Pageable pageable);
}

