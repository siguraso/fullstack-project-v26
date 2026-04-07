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

@Service
@RequiredArgsConstructor
@Transactional
public class TemperatureZoneService {

    private final TemperatureZoneRepository temperatureZoneRepository;
    private final TenantRepository tenantRepository;
    private final TemperatureZoneMapper temperatureZoneMapper;

    public TemperatureZoneDTO create(CreateTemperatureZoneRequest request) {
        validateLimits(request.getLowerLimitCelsius(), request.getUpperLimitCelsius());

        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, request.getName().trim())) {
            throw new ValidationException("Temperature zone already exists: " + request.getName());
        }

        TemperatureZone zone = new TemperatureZone();
        zone.setTenant(tenant);
        zone.setName(request.getName().trim());
        zone.setLowerLimitCelsius(request.getLowerLimitCelsius());
        zone.setUpperLimitCelsius(request.getUpperLimitCelsius());
        zone.setActive(true);

        return temperatureZoneMapper.toDTO(temperatureZoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<TemperatureZoneDTO> getForCurrentTenant() {
        Long tenantId = TenantContext.getCurrentOrg();
        return temperatureZoneRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(temperatureZoneMapper::toDTO)
                .toList();
    }

    public TemperatureZoneDTO update(Long id, CreateTemperatureZoneRequest request) {
        validateLimits(request.getLowerLimitCelsius(), request.getUpperLimitCelsius());

        Long tenantId = TenantContext.getCurrentOrg();
        TemperatureZone zone = temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Temperature zone not found"));

        if (temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrueAndIdNot(
            tenantId,
            request.getName().trim(),
            id)) {
            throw new ValidationException("Temperature zone already exists: " + request.getName());
        }

        zone.setName(request.getName().trim());
        zone.setLowerLimitCelsius(request.getLowerLimitCelsius());
        zone.setUpperLimitCelsius(request.getUpperLimitCelsius());

        return temperatureZoneMapper.toDTO(temperatureZoneRepository.save(zone));
    }

    public void delete(Long id) {
        Long tenantId = TenantContext.getCurrentOrg();

        TemperatureZone zone = temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Temperature zone not found"));
        zone.setActive(false);
        temperatureZoneRepository.save(zone);
    }

    private void validateLimits(Double lowerLimit, Double upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new ValidationException("Lower temperature limit must be less than upper limit");
        }
    }

}
