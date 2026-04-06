package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlcoholLogCreateRequest extends BaseLogCreateRequest {

    @NotNull
    private AlcoholLogType logType;

    private Boolean idChecked;
    private Boolean serviceRefused;
    private Integer estimatedAge;

    @Size(max = 2000)
    private String note;
}
