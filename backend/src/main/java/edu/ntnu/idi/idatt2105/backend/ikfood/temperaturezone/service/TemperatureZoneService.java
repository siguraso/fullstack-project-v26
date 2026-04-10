package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.CreateTemperatureZoneRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.mapper.TemperatureZoneMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository.TemperatureZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing temperature zones in the IK Food module.
 * <p>
 * Temperature zones define monitored areas (e.g. fridges, freezers) with
 * acceptable temperature ranges. Zones are soft-deleted when removed so that
 * existing log history remains intact.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TemperatureZoneService {

    private final TemperatureZoneRepository temperatureZoneRepository;
    private final TenantRepository tenantRepository;
    private final TemperatureZoneMapper temperatureZoneMapper;

    /**
     * Creates a new temperature zone for the current tenant.
     *
     * @param request the zone details including name and temperature limits
     * @return the persisted {@link TemperatureZoneDTO}
     * @throws ValidationException if a zone with the same name already exists
     *                             or the limits are invalid
     */
    public TemperatureZoneDTO create(CreateTemperatureZoneRequest request) {
        log.info("Creating temperature zone for current tenant: name={}", request.getName());
        validateLimits(request.getLowerLimitCelsius(), request.getUpperLimitCelsius());

        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, request.getName().trim())) {
            log.warn("Rejecting temperature zone creation due to duplicate name in tenantId={}", tenantId);
            throw new ValidationException("Temperature zone already exists: " + request.getName());
        }

        TemperatureZone zone = new TemperatureZone();
        zone.setTenant(tenant);
        zone.setName(request.getName().trim());
        zone.setLowerLimitCelsius(request.getLowerLimitCelsius());
        zone.setUpperLimitCelsius(request.getUpperLimitCelsius());
        zone.setActive(true);

        TemperatureZone savedZone = temperatureZoneRepository.save(zone);
        log.info("Created temperature zone id={} for tenantId={}", savedZone.getId(), tenantId);
        return temperatureZoneMapper.toDTO(savedZone);
    }

    /**
     * Returns all active temperature zones for the current tenant.
     *
     * @return a list of {@link TemperatureZoneDTO} objects
     */
    @Transactional(readOnly = true)
    public List<TemperatureZoneDTO> getForCurrentTenant() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<TemperatureZoneDTO> zones = temperatureZoneRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(temperatureZoneMapper::toDTO)
                .toList();
        log.debug("Fetched {} active temperature zones for tenantId={}", zones.size(), tenantId);
        return zones;
    }

    /**
     * Updates a temperature zone's name and temperature limits.
     *
     * @param id      identifier of the zone to update
     * @param request the updated zone details
     * @return the updated {@link TemperatureZoneDTO}
     * @throws ValidationException if the new name conflicts with another active
     *                             zone or the limits are invalid
     */
    public TemperatureZoneDTO update(Long id, CreateTemperatureZoneRequest request) {
        log.info("Updating temperature zone id={}", id);
        validateLimits(request.getLowerLimitCelsius(), request.getUpperLimitCelsius());

        Long tenantId = TenantContext.getCurrentOrg();
        TemperatureZone zone = temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Temperature zone not found"));

        if (temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrueAndIdNot(
            tenantId,
            request.getName().trim(),
            id)) {
            log.warn("Rejecting temperature zone update id={} due to duplicate name in tenantId={}", id, tenantId);
            throw new ValidationException("Temperature zone already exists: " + request.getName());
        }

        zone.setName(request.getName().trim());
        zone.setLowerLimitCelsius(request.getLowerLimitCelsius());
        zone.setUpperLimitCelsius(request.getUpperLimitCelsius());

        TemperatureZone savedZone = temperatureZoneRepository.save(zone);
        log.info("Updated temperature zone id={} in tenantId={}", savedZone.getId(), tenantId);
        return temperatureZoneMapper.toDTO(savedZone);
    }

    /**
     * Soft-deletes a temperature zone by marking it as inactive. Existing
     * temperature logs referencing the zone are preserved.
     *
     * @param id identifier of the zone to delete
     */
    public void delete(Long id) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Soft deleting temperature zone id={} for tenantId={}", id, tenantId);

        TemperatureZone zone = temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Temperature zone not found"));
        zone.setActive(false);
        temperatureZoneRepository.save(zone);
        log.info("Soft deleted temperature zone id={}", id);
    }

    private void validateLimits(Double lowerLimit, Double upperLimit) {
        if (lowerLimit >= upperLimit) {
            log.warn("Invalid temperature zone limits lower={} upper={}", lowerLimit, upperLimit);
            throw new ValidationException("Lower temperature limit must be less than upper limit");
        }
    }

}
