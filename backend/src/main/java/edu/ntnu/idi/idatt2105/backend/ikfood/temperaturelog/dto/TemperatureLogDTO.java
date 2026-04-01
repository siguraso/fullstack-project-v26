package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.BaseComplianceLogDTO;

public class TemperatureLogDTO extends BaseComplianceLogDTO {

    private Long temperatureZoneId;
    private String temperatureZoneName;
    private Double lowerLimitCelsius;
    private Double upperLimitCelsius;
    private Double temperatureCelsius;
    private String note;

    public Long getTemperatureZoneId() {
        return temperatureZoneId;
    }

    public void setTemperatureZoneId(Long temperatureZoneId) {
        this.temperatureZoneId = temperatureZoneId;
    }

    public String getTemperatureZoneName() {
        return temperatureZoneName;
    }

    public void setTemperatureZoneName(String temperatureZoneName) {
        this.temperatureZoneName = temperatureZoneName;
    }

    public Double getLowerLimitCelsius() {
        return lowerLimitCelsius;
    }

    public void setLowerLimitCelsius(Double lowerLimitCelsius) {
        this.lowerLimitCelsius = lowerLimitCelsius;
    }

    public Double getUpperLimitCelsius() {
        return upperLimitCelsius;
    }

    public void setUpperLimitCelsius(Double upperLimitCelsius) {
        this.upperLimitCelsius = upperLimitCelsius;
    }

    public Double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(Double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}


