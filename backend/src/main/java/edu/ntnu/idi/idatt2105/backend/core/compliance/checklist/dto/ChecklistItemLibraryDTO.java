package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChecklistItemLibraryDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Priority is required")
    private String priority;
}