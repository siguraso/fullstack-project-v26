package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.BaseComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;

@Component
public class AlcoholLogMapper extends BaseComplianceLogMapper<AlcoholComplianceLog, AlcoholLogDTO> {

    public AlcoholLogDTO toDTO(AlcoholComplianceLog entity) {
        if (entity == null) {
            return null;
        }

        AlcoholLogDTO dto = new AlcoholLogDTO();
        toBaseDTO(entity, dto);
        dto.setLogType(entity.getLogType());
        dto.setIdChecked(entity.getIdChecked());
        dto.setServiceRefused(entity.getServiceRefused());
        dto.setEstimatedAge(entity.getEstimatedAge());
        return dto;
    }

    public AlcoholComplianceLog toEntity(AlcoholLogCreateRequest request, Tenant tenant, User recordedBy) {
        AlcoholComplianceLog entity = new AlcoholComplianceLog();
        entity.setTenant(tenant);
        entity.setRecordedBy(recordedBy);
        entity.setModule(ComplianceModule.IK_ALCOHOL);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : LogStatus.OPEN);
        entity.setLogType(request.getLogType());
        entity.setIdChecked(request.getIdChecked());
        entity.setServiceRefused(request.getServiceRefused());
        entity.setEstimatedAge(request.getEstimatedAge());
        return entity;
    }
}
