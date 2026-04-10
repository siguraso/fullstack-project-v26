package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;

public interface TemperatureZoneRepository extends JpaRepository<TemperatureZone, Long> {

    List<TemperatureZone> findByTenantIdAndActiveTrue(Long tenantId);

    boolean existsByTenantIdAndNameIgnoreCaseAndActiveTrue(Long tenantId, String name);

    boolean existsByTenantIdAndNameIgnoreCaseAndActiveTrueAndIdNot(Long tenantId, String name, Long id);

    boolean existsByIdAndTenantIdAndActiveTrue(Long id, Long tenantId);

    Optional<TemperatureZone> findByIdAndTenantIdAndActiveTrue(Long id, Long tenantId);

}
