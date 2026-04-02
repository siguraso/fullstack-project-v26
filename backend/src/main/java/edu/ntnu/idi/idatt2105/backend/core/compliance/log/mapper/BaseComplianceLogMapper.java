package edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;

public abstract class BaseComplianceLogMapper<E extends BaseComplianceLog, D extends BaseComplianceLogDTO> {

    protected void toBaseDTO(E entity, D dto) {
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenant().getId());
        dto.setRecordedById(entity.getRecordedBy().getId());
        dto.setModule(entity.getModule());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setRecordedAt(entity.getRecordedAt() != null ? entity.getRecordedAt().toString() : null);
    }
}

