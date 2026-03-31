package edu.ntnu.idi.idatt2105.backend.ikfood.log.entity;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.enums.FoodLogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "food_compliance_logs")
public class FoodComplianceLog extends BaseComplianceLog {

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private FoodLogType logType;

    @Column(name = "temperature_log_id")
    private Long temperatureLogId;

    @Column(name = "checklist_instance_id")
    private Long checklistInstanceId;

    @PrePersist
    @PreUpdate
    public void enforceModule() {
        setModule(ComplianceModule.IK_FOOD);
    }

    public FoodLogType getLogType() {
        return logType;
    }

    public void setLogType(FoodLogType logType) {
        this.logType = logType;
    }

    public Long getTemperatureLogId() {
        return temperatureLogId;
    }

    public void setTemperatureLogId(Long temperatureLogId) {
        this.temperatureLogId = temperatureLogId;
    }

    public Long getChecklistInstanceId() {
        return checklistInstanceId;
    }

    public void setChecklistInstanceId(Long checklistInstanceId) {
        this.checklistInstanceId = checklistInstanceId;
    }
}
