package edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import lombok.Data;

@Data
public class BaseComplianceLogDTO {

    private Long id;
    private Long tenantId;
    private Long recordedById;
    private String recordedByName;
    private ComplianceModule module;
    private String title;
    private String description;
    private LogStatus status;
    private String recordedAt;
}

