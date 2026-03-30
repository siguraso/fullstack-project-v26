package edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.ComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.ComplianceLog;

@Component
public class ComplianceLogMapper {

    public ComplianceLogDTO toDTO(ComplianceLog entity) {
        if (entity == null) {
            return null;
        }
        ComplianceLogDTO dto = new ComplianceLogDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenant().getId());
        dto.setUserId(entity.getCreatedBy().getId());
        dto.setModule(entity.getModule());
        dto.setType(entity.getType());
        dto.setValue(entity.getValue());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        return dto;
    }
}
