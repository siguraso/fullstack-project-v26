package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity;

import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a reusable checklist item stored in the library.
 *
 * <p>
 * Library items act as templates that can be selected when creating
 * checklist templates. These are tenant-specific and define the base
 * content (title, description, category, priority) used across checklists.
 * </p>
 */
@Entity
@Table(name = "checklist_item_library")
@Data
public class ChecklistItemLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The tenant that owns this library item.
     */
    @ManyToOne
    private Tenant tenant;

    /**
     * The title of the checklist item.
     */
    private String title;

    /**
     * Detailed description of what the item requires.
     */
    private String description;

    /**
     * Category of the item (e.g., IK_FOOD, IK_ALCOHOL).
     */
    private String category;

    /**
     * Priority level of the item (e.g., HIGH, LOW).
     */
    private String priority;
}