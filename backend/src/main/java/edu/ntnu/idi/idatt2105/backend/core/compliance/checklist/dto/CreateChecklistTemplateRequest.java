package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChecklistTemplateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    @NotNull(message = "Compliance module is required")
    private ComplianceModule module;

    @NotNull(message = "Checklist frequency is required")
    private ChecklistFrequency frequency;

    @NotEmpty(message = "At least one checklist item must be selected")
    private List<Long> itemIds;
}
