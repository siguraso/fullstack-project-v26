package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeviationRequest {

    @NotNull
    private Long tenantId;

    @NotNull
    private ComplianceModule module;

    @NotBlank
    private String title;
    private String description;

    @NotBlank
    private DeviationSeverity severity;

    @NotBlank
    private DeviationCategory category;
}
