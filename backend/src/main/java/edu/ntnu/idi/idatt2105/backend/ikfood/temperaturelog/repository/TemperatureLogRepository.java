package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;

public interface TemperatureLogRepository extends BaseComplianceLogRepository<TemperatureComplianceLog> {

	@Override
	@EntityGraph(attributePaths = {"recordedBy", "temperatureZone"})
	List<TemperatureComplianceLog> findAllByTenantId(Long tenantId);

	@Override
	@EntityGraph(attributePaths = {"recordedBy", "temperatureZone"})
	Page<TemperatureComplianceLog> findAllByTenantId(Long tenantId, Pageable pageable);
}


