package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;

@Component
public class DeviationMapper {

    public DeviationDTO toDTO(Deviation entity) {
        DeviationDTO dto = new DeviationDTO();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());

        dto.setStatus(entity.getStatus().name());
        dto.setSeverity(entity.getSeverity().name());
        dto.setCategory(entity.getCategory().name());

        dto.setChecklistItemId(entity.getChecklistItemId());
        dto.setLogId(entity.getLogId());

        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setResolvedAt(
                entity.getResolvedAt() != null ? entity.getResolvedAt().toString() : null);

        return dto;
    }

}
