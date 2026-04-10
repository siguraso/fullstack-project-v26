package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.util.List;

import lombok.Data;

/**
 * Data transfer object representing a checklist template definition used in
 * the compliance module.
 * <p>
 * A template groups one or more {@link ChecklistItemTemplateDTO} entries under
 * a name, module and execution frequency, and can be activated or deactivated
 * for generating concrete checklist instances.
 */
@Data
public class ChecklistTemplateDTO {

    private Long id;
    private String name;
    private String module;
    private String frequency;
    private boolean active;

    private List<ChecklistItemTemplateDTO> items;
}