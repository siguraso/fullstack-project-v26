package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used to create a new temperature compliance log.
 *
 * <p>
 * Captures a temperature measurement for a specific zone,
 * along with an optional note for context or deviations.
 * </p>
 */
@Data
public class TemperatureLogCreateRequest {

    @NotNull
    private Long temperatureZoneId;

    @NotNull
    private Double temperatureCelsius;

    @Size(max = 2000)
    private String note;

}
