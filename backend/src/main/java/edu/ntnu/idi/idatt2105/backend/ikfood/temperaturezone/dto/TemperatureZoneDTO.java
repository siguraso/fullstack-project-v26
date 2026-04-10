package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto;

import lombok.Data;

@Data
public class TemperatureZoneDTO {

    private Long id;
    private String name;
    private Double lowerLimitCelsius;
    private Double upperLimitCelsius;
    private boolean active;

}
