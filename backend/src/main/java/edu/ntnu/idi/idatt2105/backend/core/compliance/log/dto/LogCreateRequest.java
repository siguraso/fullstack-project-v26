package edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogCreateRequest {

    @NotNull
    private Long tenantId;

    @NotNull
    private Long userId;

    @NotNull
    private ComplianceModule module;

    @NotNull
    private LogType type;

    @NotNull
    private String value;
}
