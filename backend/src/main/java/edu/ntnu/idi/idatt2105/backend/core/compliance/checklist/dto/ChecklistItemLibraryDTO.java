package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

@Data
public class ChecklistItemLibraryDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
}