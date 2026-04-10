package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used to update the status of an existing deviation.
 *
 * <p>
 * Typically used to transition a deviation through its lifecycle
 * (e.g., OPEN → IN_PROGRESS → RESOLVED).
 * </p>
 */
@Data
public class UpdateDeviationRequest {

    /**
     * The new status of the deviation.
     */
    @NotNull(message = "Status is required")
    private DeviationStatus status;
}