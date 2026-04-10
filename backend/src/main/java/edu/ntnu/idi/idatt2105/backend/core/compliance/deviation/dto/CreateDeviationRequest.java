package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import java.time.LocalDate;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used to create a new deviation.
 *
 * <p>
 * Contains all information required to register a compliance deviation,
 * including metadata (module, severity, category, status) and detailed
 * description fields for investigation and resolution.
 * </p>
 */
@Data
public class CreateDeviationRequest {

    @NotNull
    private ComplianceModule module;

    @NotBlank
    private String title;
    private LocalDate reportedDate;
    private String discoveredBy;
    private String reportedTo;
    private String assignedTo;
    private String issueDescription;
    private String immediateAction;
    private String rootCause;
    private String correctiveAction;
    private String completionNotes;

    @NotNull
    private DeviationSeverity severity;

    @NotNull
    private DeviationCategory category;

    @NotNull
    private DeviationStatus status;

    private Long logId;
}
