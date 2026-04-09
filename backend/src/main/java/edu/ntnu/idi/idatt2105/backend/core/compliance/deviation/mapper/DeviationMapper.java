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
        dto.setReportedDate(entity.getReportedDate() != null ? entity.getReportedDate().toString() : null);
        dto.setDiscoveredBy(entity.getDiscoveredBy());
        dto.setReportedTo(entity.getReportedTo());
        dto.setAssignedTo(entity.getAssignedTo());
        dto.setIssueDescription(entity.getIssueDescription());
        dto.setImmediateAction(entity.getImmediateAction());
        dto.setRootCause(entity.getRootCause());
        dto.setCorrectiveAction(entity.getCorrectiveAction());
        dto.setCompletionNotes(entity.getCompletionNotes());

        dto.setStatus(entity.getStatus().name());
        dto.setSeverity(entity.getSeverity().name());
        dto.setCategory(entity.getCategory().name());
        dto.setModule(entity.getModule());

        dto.setLogId(entity.getLogId());

        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setResolvedAt(
                entity.getResolvedAt() != null ? entity.getResolvedAt().toString() : null);

        return dto;
    }

}
