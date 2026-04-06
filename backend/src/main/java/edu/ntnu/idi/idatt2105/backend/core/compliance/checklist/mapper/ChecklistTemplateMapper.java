package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;

@Component
public class ChecklistTemplateMapper {

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