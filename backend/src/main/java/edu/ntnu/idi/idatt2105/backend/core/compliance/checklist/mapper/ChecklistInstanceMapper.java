package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;

@Component
public class ChecklistInstanceMapper {

    public ChecklistInstanceDTO toDto(ChecklistInstance entity) {
        if (entity == null)
            return null;
        ChecklistInstanceDTO dto = new ChecklistInstanceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getTemplate().getName());
        dto.setDate(entity.getDate());
        dto.setStatus(entity.getStatus().name());
        dto.setItems(entity.getItems().stream().map(this::mapItem).toList());

        return dto;
    }

    private ChecklistItemDTO mapItem(ChecklistItemInstance item) {
        ChecklistItemDTO dto = new ChecklistItemDTO();
        dto.setId(item.getId());
        dto.setDescription(item.getTemplateItem().getDescription());
        dto.setCompleted(item.isCompleted());
        dto.setComment(item.getComment());
        return dto;
    }
}
