package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing an alcohol compliance log entry in the IK Alcohol
 * module.
 * <p>
 * Extends {@link BaseComplianceLog} with alcohol-specific fields such as log
 * type, ID check result, service refusal flag and estimated age.
 */
@Entity
@Table(name = "alcohol_compliance_logs")
@Getter
@Setter
public class AlcoholComplianceLog extends BaseComplianceLog {

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private AlcoholLogType logType;

    @Column(name = "id_checked")
    private Boolean idChecked;

    @Column(name = "service_refused")
    private Boolean serviceRefused;

    @Column(name = "estimated_age")
    private Integer estimatedAge;

    @PreUpdate
    public void enforceModule() {
        setModule(ComplianceModule.IK_ALCOHOL);
    }
}
