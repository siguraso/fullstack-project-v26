package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

@Data
public class ChecklistItemTemplateDTO {

    private Long id;
    private String description;
    private int sortOrder;
}