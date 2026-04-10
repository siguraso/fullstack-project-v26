package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a checklist item within a checklist template.
 *
 * <p>
 * This is a snapshot of a {@link ChecklistItemLibrary} item at the time the
 * template is created. It allows templates to remain stable even if the library
 * item changes later.
 * </p>
 */
@Entity
@Data
@Table(name = "checklist_item_templates")
public class ChecklistItemTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The checklist template this item belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ChecklistTemplate checklistTemplate;

    /**
     * The title of the checklist item at the time of template creation.
     */
    private String title;

    /**
     * The description of the checklist item at the time of template creation.
     */
    private String description;

    /**
     * Determines the display order of the item within the checklist.
     */
    private int sortOrder;

    /**
     * Reference to the original library item this template item was created from.
     */
    @ManyToOne
    @JoinColumn(name = "library_item_id")
    private ChecklistItemLibrary libraryItem;
}