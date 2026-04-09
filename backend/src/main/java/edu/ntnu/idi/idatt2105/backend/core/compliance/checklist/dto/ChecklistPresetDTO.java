package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

/**
 * Read-only DTO for a global checklist preset item.
 *
 * <p>The frontend groups these by {@code tab} and then by {@code groupLabel}
 * to render the categorised preset picker. The {@code sortOrder} field controls
 * item order within each group.
 */
@Data
public class ChecklistPresetDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String tab;
    private String groupLabel;
    private int sortOrder;
}
