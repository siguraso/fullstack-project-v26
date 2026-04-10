package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.mapper;

import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts {@link TemperatureZone} entities to
 * {@link TemperatureZoneDTO} objects.
 */
@Component
public class TemperatureZoneMapper {

    /**
     * Converts a temperature zone entity to its DTO representation.
     *
     * @param zone the temperature zone entity; returns {@code null} if
     *             {@code zone} is {@code null}
     * @return the corresponding {@link TemperatureZoneDTO}
     */
    public TemperatureZoneDTO toDTO(TemperatureZone zone) {
        if (zone == null) {
            return null;
        }

        TemperatureZoneDTO dto = new TemperatureZoneDTO();
        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setLowerLimitCelsius(zone.getLowerLimitCelsius());
        dto.setUpperLimitCelsius(zone.getUpperLimitCelsius());
        dto.setActive(zone.isActive());
        return dto;
    }
}

