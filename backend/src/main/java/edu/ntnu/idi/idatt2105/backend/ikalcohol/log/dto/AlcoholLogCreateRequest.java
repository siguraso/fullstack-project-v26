package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import jakarta.validation.constraints.NotNull;

public class AlcoholLogCreateRequest extends BaseLogCreateRequest {

    @NotNull
    private AlcoholLogType logType;

    private Boolean idChecked;
    private Boolean serviceRefused;
    private Integer estimatedAge;

    public AlcoholLogType getLogType() {
        return logType;
    }

    public void setLogType(AlcoholLogType logType) {
        this.logType = logType;
    }

    public Boolean getIdChecked() {
        return idChecked;
    }

    public void setIdChecked(Boolean idChecked) {
        this.idChecked = idChecked;
    }

    public Boolean getServiceRefused() {
        return serviceRefused;
    }

    public void setServiceRefused(Boolean serviceRefused) {
        this.serviceRefused = serviceRefused;
    }

    public Integer getEstimatedAge() {
        return estimatedAge;
    }

    public void setEstimatedAge(Integer estimatedAge) {
        this.estimatedAge = estimatedAge;
    }
}

