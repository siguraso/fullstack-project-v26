package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.CreateTemperatureZoneRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository.TemperatureZoneRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.mapper.TemperatureZoneMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (temperatureZoneRepository.existsByTenantIdAndNameIgnoreCase(tenantId, request.getName().trim())) {
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

    private void validateLimits(Double lowerLimit, Double upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new ValidationException("Lower temperature limit must be less than upper limit");
        }
    }

}

