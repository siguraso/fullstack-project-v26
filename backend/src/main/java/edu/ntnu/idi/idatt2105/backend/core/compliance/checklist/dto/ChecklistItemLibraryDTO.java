package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data transfer object representing a reusable checklist item definition in
 * the checklist item library.
 * <p>
 * Items from this library can be referenced by checklist templates to ensure
 * consistent wording and categorization across the compliance module.
 */
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