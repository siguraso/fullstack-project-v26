package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import lombok.Data;

@Data
public class CreateDeviationRequest {

    private Long tenantId;
    private ComplianceModule module;

    private String title;
    private String description;

    private DeviationSeverity severity;
    private DeviationCategory category;
}
