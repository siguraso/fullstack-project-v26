package edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;

public abstract class BaseComplianceLogMapper<E extends BaseComplianceLog, D extends BaseComplianceLogDTO> {

    protected void toBaseDTO(E entity, D dto) {
        String firstName = entity.getRecordedBy() != null ? entity.getRecordedBy().getFirstName() : null;
        String lastName = entity.getRecordedBy() != null ? entity.getRecordedBy().getLastName() : null;
        String fullName = String.format("%s %s", firstName != null ? firstName.trim() : "",
                lastName != null ? lastName.trim() : "").trim();
        String displayName = fullName;

        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenant().getId());
        dto.setRecordedById(entity.getRecordedBy().getId());
        dto.setRecordedByName(displayName == null || displayName.isBlank() ? null : displayName);
        dto.setModule(entity.getModule());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setRecordedAt(entity.getRecordedAt() != null ? entity.getRecordedAt().toString() : null);
    }
}

