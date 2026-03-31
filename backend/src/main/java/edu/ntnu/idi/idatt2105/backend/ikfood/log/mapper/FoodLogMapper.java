package edu.ntnu.idi.idatt2105.backend.ikfood.log.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.BaseComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.entity.FoodComplianceLog;

@Component
public class FoodLogMapper extends BaseComplianceLogMapper<FoodComplianceLog, FoodLogDTO> {

    public FoodLogDTO toDTO(FoodComplianceLog entity) {
        if (entity == null) {
            return null;
        }

        FoodLogDTO dto = new FoodLogDTO();
        toBaseDTO(entity, dto);
        dto.setLogType(entity.getLogType());
        dto.setTemperatureLogId(entity.getTemperatureLogId());
        dto.setChecklistInstanceId(entity.getChecklistInstanceId());
        return dto;
    }

    public FoodComplianceLog toEntity(FoodLogCreateRequest request, Tenant tenant, User recordedBy) {
        FoodComplianceLog entity = new FoodComplianceLog();
        entity.setTenant(tenant);
        entity.setRecordedBy(recordedBy);
        entity.setModule(ComplianceModule.IK_FOOD);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : LogStatus.OPEN);
        entity.setLogType(request.getLogType());
        entity.setTemperatureLogId(request.getTemperatureLogId());
        entity.setChecklistInstanceId(request.getChecklistInstanceId());
        return entity;
    }
}
