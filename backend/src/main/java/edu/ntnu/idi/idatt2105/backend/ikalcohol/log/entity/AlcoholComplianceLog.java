package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "alcohol_compliance_logs")
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

    @PrePersist
    @PreUpdate
    public void enforceModule() {
        setModule(ComplianceModule.IK_ALCOHOL);
    }

    public AlcoholLogType getLogType() {
        return logType;
    }

    public void setLogType(AlcoholLogType logType) {
        this.logType = logType;
    }

    public Boolean getIdChecked() {
        return idChecked;
    }

    public void setIdChecked(Boolean idChecked) {
        this.idChecked = idChecked;
    }

    public Boolean getServiceRefused() {
        return serviceRefused;
    }

    public void setServiceRefused(Boolean serviceRefused) {
        this.serviceRefused = serviceRefused;
    }

    public Integer getEstimatedAge() {
        return estimatedAge;
    }

    public void setEstimatedAge(Integer estimatedAge) {
        this.estimatedAge = estimatedAge;
    }
}
