package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto;

public class TemperatureZoneDTO {

    private Long id;
    private String name;
    private Double lowerLimitCelsius;
    private Double upperLimitCelsius;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

