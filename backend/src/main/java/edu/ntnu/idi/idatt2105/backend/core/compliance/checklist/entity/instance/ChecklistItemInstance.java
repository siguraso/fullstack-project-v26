package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance;

import java.time.LocalDate;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistItemTemplate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a single checklist item within a checklist instance.
 *
 * <p>
 * This is a runtime copy of a {@link ChecklistItemTemplate}, created when a
 * checklist instance is generated. It tracks the completion state, optional
 * comments, and when the item was completed.
 * </p>
 */
@Entity
@Data
@Table(name = "checklist_item_instances")
public class ChecklistItemInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The checklist instance this item belongs to.
     */
    @ManyToOne
    private ChecklistInstance checklist;

    /**
     * Indicates whether the item has been completed.
     */
    private boolean completed;

    /**
     * Optional comment added when completing the item.
     */
    private String comment;

    /**
     * The date when the item was marked as completed.
     */
    private LocalDate completedAt;

    /**
     * Reference to the original template item this instance was created from.
     */
    @ManyToOne
    @JoinColumn(name = "template_item_id")
    private ChecklistItemTemplate templateItem;
}