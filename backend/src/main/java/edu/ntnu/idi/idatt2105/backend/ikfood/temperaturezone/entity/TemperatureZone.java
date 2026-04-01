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

@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
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

