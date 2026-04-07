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

@Component
public class TemperatureLogMapper extends BaseComplianceLogMapper<TemperatureComplianceLog, TemperatureLogDTO> {

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

