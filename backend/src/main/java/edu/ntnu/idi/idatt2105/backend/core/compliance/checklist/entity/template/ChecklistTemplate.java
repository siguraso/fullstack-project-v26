package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "checklist_templates")
public class ChecklistTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private ComplianceModule module;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChecklistFrequency frequency;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemTemplate> items = new ArrayList<>();
}
