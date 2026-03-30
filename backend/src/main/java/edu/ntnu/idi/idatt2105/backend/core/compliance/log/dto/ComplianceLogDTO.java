package edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogType;
import lombok.Data;

@Data
public class ComplianceLogDTO {

    private Long id;
    private Long tenantId;
    private Long userId;
    private ComplianceModule module;
    private LogType type;
    private String value;
    private LogStatus status;
    private String createdAt;
}
