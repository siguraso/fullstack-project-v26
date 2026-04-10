package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used to create a new checklist template.
 *
 * <p>
 * Contains the core configuration for a checklist, including its name,
 * associated compliance module, execution frequency, and the list of
 * library item IDs that should be included in the template.
 * </p>
 */
@Data
public class CreateChecklistTemplateRequest {

    /**
     * The name of the checklist template.
     */
    @NotBlank(message = "Template name is required")
    private String name;

    /**
     * The compliance module the checklist belongs to
     * (e.g., IK_FOOD, IK_ALCOHOL).
     */
    @NotNull(message = "Compliance module is required")
    private ComplianceModule module;

    /**
     * How often the checklist should be executed.
     */
    @NotNull(message = "Checklist frequency is required")
    private ChecklistFrequency frequency;

    /**
     * List of library item IDs to include in the template.
     */
    @NotEmpty(message = "At least one checklist item must be selected")
    private List<Long> itemIds;
}
