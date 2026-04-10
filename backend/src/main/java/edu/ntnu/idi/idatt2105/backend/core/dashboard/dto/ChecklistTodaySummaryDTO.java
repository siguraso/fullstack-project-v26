package edu.ntnu.idi.idatt2105.backend.core.dashboard.dto;

import lombok.Data;

/**
 * DTO representing a summary of today's checklist progress.
 *
 * <p>
 * Used by the dashboard to display overall completion metrics
 * for checklists and their items within the current day.
 * </p>
 */
@Data
public class ChecklistTodaySummaryDTO {

    /**
     * Total number of checklist instances for today.
     */
    private int totalChecklists;

    /**
     * Number of checklist instances that are fully completed.
     */
    private int completedChecklists;

    /**
     * Total number of checklist items across all checklists.
     */
    private int totalItems;

    /**
     * Number of checklist items that have been completed.
     */
    private int completedItems;
}