package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;

/**
 * Mapper that converts {@link ChecklistInstance} entities to
 * {@link ChecklistInstanceDTO} objects.
 */
@Component
public class ChecklistInstanceMapper {

    /**
     * Converts a checklist instance entity to its DTO representation,
     * including all nested item instances.
     *
     * @param entity the checklist instance entity; returns {@code null} if
     *               {@code entity} is {@code null}
     * @return the corresponding {@link ChecklistInstanceDTO}
     */
    public ChecklistInstanceDTO toDto(ChecklistInstance entity) {
        if (entity == null)
            return null;
        ChecklistInstanceDTO dto = new ChecklistInstanceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getTemplate().getName());
        dto.setModule(entity.getTemplate().getModule().name());
        dto.setDate(entity.getDate());
        dto.setStatus(entity.getStatus().name());
        dto.setItems(entity.getItems().stream().map(this::mapItem).toList());

        return dto;
    }

    private ChecklistItemDTO mapItem(ChecklistItemInstance item) {
        ChecklistItemDTO dto = new ChecklistItemDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTemplateItem().getTitle());
        dto.setDescription(item.getTemplateItem().getDescription());
        dto.setCompleted(item.isCompleted());
        dto.setComment(item.getComment());
        return dto;
    }
}
