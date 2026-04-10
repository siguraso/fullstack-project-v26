package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used to create a new temperature zone.
 *
 * <p>
 * Defines a named zone with lower and upper temperature limits
 * used for compliance monitoring.
 * </p>
 */
@Data
public class CreateTemperatureZoneRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    private Double lowerLimitCelsius;

    @NotNull
    private Double upperLimitCelsius;
}
