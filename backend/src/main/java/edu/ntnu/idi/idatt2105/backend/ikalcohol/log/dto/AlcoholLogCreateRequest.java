package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request DTO used to create a new alcohol compliance log.
 *
 * <p>
 * Extends {@link BaseLogCreateRequest} with alcohol-specific fields such as
 * log type, ID verification, service refusal, and estimated age.
 * </p>
 */
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
