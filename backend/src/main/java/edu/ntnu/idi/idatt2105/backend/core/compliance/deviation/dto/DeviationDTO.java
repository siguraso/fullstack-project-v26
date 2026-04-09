package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import lombok.Data;

@Data
public class DeviationDTO {

    private Long id;
    private String title;
    private String reportedDate;
    private String discoveredBy;
    private String reportedTo;
    private String assignedTo;
    private String issueDescription;
    private String immediateAction;
    private String rootCause;
    private String correctiveAction;
    private String completionNotes;
    private String status;
    private String severity;
    private String category;
    private ComplianceModule module;

    private Long logId;

    private String createdAt;
    private String resolvedAt;
}
