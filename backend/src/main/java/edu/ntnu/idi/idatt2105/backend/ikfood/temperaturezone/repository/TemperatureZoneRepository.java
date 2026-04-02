package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository;

import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureZoneRepository extends JpaRepository<TemperatureZone, Long> {

    List<TemperatureZone> findByTenantIdAndActiveTrue(Long tenantId);

    boolean existsByTenantIdAndNameIgnoreCase(Long tenantId, String name);
}

