package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlcoholLogDTO extends BaseComplianceLogDTO {

    private AlcoholLogType logType;
    private Boolean idChecked;
    private Boolean serviceRefused;
    private Integer estimatedAge;
}
