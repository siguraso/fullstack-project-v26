package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TemperatureLogCreateRequest {

    @NotNull
    private Long temperatureZoneId;

    @NotNull
    private Double temperatureCelsius;

    @Size(max = 2000)
    private String note;

    public Long getTemperatureZoneId() {
        return temperatureZoneId;
    }

    public void setTemperatureZoneId(Long temperatureZoneId) {
        this.temperatureZoneId = temperatureZoneId;
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


