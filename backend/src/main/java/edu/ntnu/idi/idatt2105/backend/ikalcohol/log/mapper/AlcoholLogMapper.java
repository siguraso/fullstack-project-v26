package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.BaseComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
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

    public AlcoholComplianceLog toEntity(AlcoholLogCreateRequest request, Tenant tenant, User recordedBy, LogStatus status) {
        AlcoholComplianceLog entity = new AlcoholComplianceLog();
        entity.setTenant(tenant);
        entity.setRecordedBy(recordedBy);
        entity.setModule(ComplianceModule.IK_ALCOHOL);
        entity.setTitle(request.getTitle());
        entity.setDescription(resolveDescription(request));
        entity.setStatus(status);
        entity.setLogType(request.getLogType());
        entity.setIdChecked(request.getIdChecked());
        entity.setServiceRefused(request.getServiceRefused());
        entity.setEstimatedAge(request.getEstimatedAge());
        return entity;
    }

    private String resolveDescription(AlcoholLogCreateRequest request) {
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            return request.getDescription();
        }

        if (request.getNote() != null && !request.getNote().isBlank()) {
            return request.getNote();
        }

        return null;
    }
}
