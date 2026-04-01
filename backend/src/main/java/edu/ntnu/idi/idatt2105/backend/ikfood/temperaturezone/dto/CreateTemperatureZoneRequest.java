package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTemperatureZoneRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    private Double lowerLimitCelsius;

    @NotNull
    private Double upperLimitCelsius;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

