package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemperatureLogDTO extends BaseComplianceLogDTO {

    private Long temperatureZoneId;
    private String temperatureZoneName;
    private Double lowerLimitCelsius;
    private Double upperLimitCelsius;
    private Double temperatureCelsius;
    private String note;
    private boolean deviationCreated;
}
