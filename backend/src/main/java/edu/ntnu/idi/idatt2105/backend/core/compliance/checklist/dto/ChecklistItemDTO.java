package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

/**
 * Data transfer object representing a single checklist item belonging to a
 * checklist instance or template.
 * <p>
 * Captures the current completion state together with optional user comments
 * used during compliance checks.
 */
@Data
public class ChecklistItemDTO {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String comment;
}
