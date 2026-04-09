package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "deviations")
public class Deviation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceModule module;

    @Column(nullable = false)
    private String title;

    private LocalDate reportedDate;

    private String discoveredBy;

    private String reportedTo;

    private String assignedTo;

    @Column(length = 2000)
    private String issueDescription;

    @Column(length = 2000)
    private String immediateAction;

    @Column(length = 2000)
    private String rootCause;

    @Column(length = 2000)
    private String correctiveAction;

    @Column(length = 2000)
    private String completionNotes;

    @Enumerated(EnumType.STRING)
    private DeviationSeverity severity;

    @Enumerated(EnumType.STRING)
    private DeviationCategory category;

    @Enumerated(EnumType.STRING)
    private DeviationStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private Long logId;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

}
