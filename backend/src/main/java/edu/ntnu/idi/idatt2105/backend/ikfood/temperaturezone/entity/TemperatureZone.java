package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity;

import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a temperature zone within a tenant.
 *
 * <p>
 * A temperature zone defines a named storage or handling area
 * (e.g., fridge, freezer) with acceptable temperature limits.
 * It is used to validate temperature logs for compliance.
 * </p>
 */
@Entity
@Data
@Table(name = "temperature_zones")
public class TemperatureZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "lower_limit_celsius", nullable = false)
    private Double lowerLimitCelsius;

    @Column(name = "upper_limit_celsius", nullable = false)
    private Double upperLimitCelsius;

    @Column(nullable = false)
    private boolean active = true;
}
