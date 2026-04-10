package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

/**
 * Data transfer object representing a checklist item defined as part of a
 * checklist template.
 * <p>
 * Contains the display title, description and sort order used to position the
 * item within a template when rendering or generating checklist instances.
 */
@Data
public class ChecklistItemTemplateDTO {

    private Long id;
    private String title;
    private String description;
    private int sortOrder;
}