package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO representing an alcohol compliance log.
 *
 * <p>
 * Extends {@link BaseComplianceLogDTO} with alcohol-specific details
 * such as log type, ID verification, service refusal, and estimated age.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AlcoholLogDTO extends BaseComplianceLogDTO {

    private AlcoholLogType logType;
    private Boolean idChecked;
    private Boolean serviceRefused;
    private Integer estimatedAge;
}
