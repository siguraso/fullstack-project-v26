package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.BaseComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;

/**
 * Mapper for converting between {@link TemperatureComplianceLog} entities,
 * {@link TemperatureLogDTO} data objects and
 * {@link TemperatureLogCreateRequest} requests.
 */
@Component
public class TemperatureLogMapper extends BaseComplianceLogMapper<TemperatureComplianceLog, TemperatureLogDTO> {

    /**
     * Converts a temperature compliance log entity to its DTO representation,
     * including zone limits and recorder details.
     *
     * @param entity the entity to convert; returns {@code null} if
     *               {@code entity} is {@code null}
     * @return the corresponding {@link TemperatureLogDTO}
     */
    public TemperatureLogDTO toDTO(TemperatureComplianceLog entity) {
        if (entity == null) {
            return null;
        }

        TemperatureLogDTO dto = new TemperatureLogDTO();
        toBaseDTO(entity, dto);
        if (entity.getRecordedBy() != null) {
            dto.setRecordedByName(entity.getRecordedBy().getFirstName() + " " + entity.getRecordedBy().getLastName());
            dto.setRecordedByEmail(entity.getRecordedBy().getEmail());
            dto.setRecordedByRole(entity.getRecordedBy().getRole().name());
        }
        dto.setTemperatureZoneId(entity.getTemperatureZone().getId());
        dto.setTemperatureZoneName(entity.getTemperatureZone().getName());
        dto.setLowerLimitCelsius(entity.getTemperatureZone().getLowerLimitCelsius());
        dto.setUpperLimitCelsius(entity.getTemperatureZone().getUpperLimitCelsius());
        dto.setTemperatureCelsius(entity.getTemperatureCelsius());
        dto.setNote(entity.getDescription());
        return dto;
    }

    /**
     * Creates a {@link TemperatureComplianceLog} entity from a creation
     * request.
     *
     * @param request    the creation request with temperature and zone details
     * @param tenant     the tenant to associate the log with
     * @param recordedBy the user recording the temperature
     * @param zone       the temperature zone being logged
     * @param status     the resolved log status based on zone limits
     * @return a new, unpersisted {@link TemperatureComplianceLog} entity
     */
    public TemperatureComplianceLog toEntity(
            TemperatureLogCreateRequest request,
            Tenant tenant,
            User recordedBy,
            TemperatureZone zone,
            LogStatus status) {
        TemperatureComplianceLog entity = new TemperatureComplianceLog();
        entity.setTenant(tenant);
        entity.setRecordedBy(recordedBy);
        entity.setModule(ComplianceModule.IK_FOOD);
        entity.setTemperatureZone(zone);
        entity.setTemperatureCelsius(request.getTemperatureCelsius());
        entity.setDescription(request.getNote());
        entity.setStatus(status);
        entity.setTitle("Temperature reading - " + zone.getName());
        return entity;
    }
}

