package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistStatus;
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

/**
 * Represents a concrete checklist instance for a specific date.
 *
 * <p>
 * An instance is generated from a {@link ChecklistTemplate} and contains
 * individual checklist item instances that can be completed by users.
 * Each instance belongs to a tenant and tracks its own completion status.
 * </p>
 */
@Entity
@Data
@Table(name = "checklist_instances")
public class ChecklistInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The tenant that owns this checklist instance.
     */
    @ManyToOne
    private Tenant tenant;

    /**
     * The template this instance was generated from.
     */
    @ManyToOne
    private ChecklistTemplate template;

    /**
     * The date this checklist instance applies to.
     */
    private LocalDate date;

    /**
     * Current completion status of the checklist instance.
     */
    @Enumerated(EnumType.STRING)
    private ChecklistStatus status;

    /**
     * The checklist items belonging to this instance.
     * These are created from the template and tracked individually.
     */
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemInstance> items = new ArrayList<>();
}