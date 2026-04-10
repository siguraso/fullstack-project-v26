package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO representing a temperature compliance log.
 *
 * <p>
 * Extends {@link BaseComplianceLogDTO} with temperature-specific details,
 * including zone information, measured value, limits, and deviation state.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TemperatureLogDTO extends BaseComplianceLogDTO {

    private Long id;
    private String recordedByName;
    private String recordedByEmail;
    private String recordedByRole;
    private Long temperatureZoneId;
    private String temperatureZoneName;
    private Double lowerLimitCelsius;
    private Double upperLimitCelsius;
    private Double temperatureCelsius;
    private String note;
    private boolean deviationCreated;
}
