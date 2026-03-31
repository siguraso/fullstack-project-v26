package edu.ntnu.idi.idatt2105.backend.ikfood.log.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.enums.FoodLogType;
import jakarta.validation.constraints.NotNull;

public class FoodLogCreateRequest extends BaseLogCreateRequest {

    @NotNull
    private FoodLogType logType;

    private Long temperatureLogId;
    private Long checklistInstanceId;

    public FoodLogType getLogType() {
        return logType;
    }

    public void setLogType(FoodLogType logType) {
        this.logType = logType;
    }

    public Long getTemperatureLogId() {
        return temperatureLogId;
    }

    public void setTemperatureLogId(Long temperatureLogId) {
        this.temperatureLogId = temperatureLogId;
    }

    public Long getChecklistInstanceId() {
        return checklistInstanceId;
    }

    public void setChecklistInstanceId(Long checklistInstanceId) {
        this.checklistInstanceId = checklistInstanceId;
    }
}

