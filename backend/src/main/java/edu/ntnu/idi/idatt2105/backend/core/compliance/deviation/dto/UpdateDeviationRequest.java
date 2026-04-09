package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeviationRequest {

    @NotNull(message = "Status is required")
    private DeviationStatus status;
}
