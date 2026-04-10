package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a temperature compliance log entry.
 *
 * <p>
 * Extends {@link BaseComplianceLog} with temperature-specific data,
 * capturing a measurement for a given {@link TemperatureZone}.
 * Used to monitor compliance with defined temperature limits.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "temperature_compliance_logs")
public class TemperatureComplianceLog extends BaseComplianceLog {
    @ManyToOne
    @JoinColumn(name = "temperature_zone_id", nullable = false)
    private TemperatureZone temperatureZone;

    @Column(name = "temperature_celsius", nullable = false)
    private Double temperatureCelsius;
}
