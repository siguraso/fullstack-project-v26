package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;

@Component
public class ChecklistItemLibraryMapper {

    public ChecklistItemLibraryDTO toDto(ChecklistItemLibrary entity) {
        ChecklistItemLibraryDTO dto = new ChecklistItemLibraryDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setPriority(entity.getPriority());
        return dto;
    }
}