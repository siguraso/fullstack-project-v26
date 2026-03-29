package edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogType;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "compliance_logs")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ComplianceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceModule module;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private LogType type;

    // Flexible value
    @Column(name = "log_value", nullable = false, length = 1000)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_status", nullable = false)
    private LogStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
