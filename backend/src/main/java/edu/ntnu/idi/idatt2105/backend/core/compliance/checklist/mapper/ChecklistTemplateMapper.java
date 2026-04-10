package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;

/**
 * Mapper that converts {@link ChecklistTemplate} entities to
 * {@link ChecklistTemplateDTO} objects.
 */
@Component
public class ChecklistTemplateMapper {

    /**
     * Converts a checklist template entity to its DTO representation,
     * including all template item snapshots.
     *
     * @param entity the checklist template entity
     * @return the corresponding {@link ChecklistTemplateDTO}
     */
    public ChecklistTemplateDTO toDto(ChecklistTemplate entity) {
        ChecklistTemplateDTO dto = new ChecklistTemplateDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setModule(entity.getModule().name());
        dto.setFrequency(entity.getFrequency().name());
        dto.setActive(entity.isActive());

        dto.setItems(
                entity.getItems().stream().map(item -> {
                    ChecklistItemTemplateDTO i = new ChecklistItemTemplateDTO();
                    if (item.getLibraryItem() == null) {
                        throw new IllegalStateException("Missing library item");
                    }
                    i.setId(item.getLibraryItem().getId());
                    i.setTitle(item.getLibraryItem().getTitle());
                    i.setDescription(item.getDescription());
                    i.setSortOrder(item.getSortOrder());
                    return i;
                }).toList());

        return dto;
    }
}