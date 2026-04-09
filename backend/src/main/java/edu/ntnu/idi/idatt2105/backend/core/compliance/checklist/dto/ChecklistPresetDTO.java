package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

/**
 * Read-only DTO for a global checklist preset item.
 *
 * <p>The frontend groups these by {@code tab} and then by {@code groupLabel}
 * to render the categorised preset picker. The {@code sortOrder} field controls
 * item order within each group.
 */
@Data
public class ChecklistPresetDTO {

    private Long id;

    /** Item title. */
    private String title;

    /** Full instruction / description text. */
    private String description;

    /**
     * Compliance category for the resulting library item.
     * Values: {@code IK_FOOD} or {@code IK_ALCOHOL}.
     */
    private String category;

    /** Priority level. Values: {@code LOW} or {@code HIGH}. */
    private String priority;

    /**
     * Picker tab this preset belongs to.
     * Values: {@code IK_FOOD}, {@code IK_ALCOHOL}, or {@code HACCP}.
     */
    private String tab;

    /**
     * Sub-group label within the tab.
     * Examples: "Temperature Control", "Age Verification", "Critical Control Points".
     */
    private String groupLabel;

    /** Display order within the group (ascending). */
    private int sortOrder;
}
