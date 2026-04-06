package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import lombok.Data;

@Data
public class CreateChecklistTemplateRequest {

    private Long tenantId;
    private String name;
    private ComplianceModule module;
    private ChecklistFrequency frequency;

    private List<Long> itemIds;
}
