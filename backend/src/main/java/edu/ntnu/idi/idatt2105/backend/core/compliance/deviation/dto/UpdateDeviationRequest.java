package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import lombok.Data;

@Data
public class UpdateDeviationRequest {

    private DeviationStatus status;
}
