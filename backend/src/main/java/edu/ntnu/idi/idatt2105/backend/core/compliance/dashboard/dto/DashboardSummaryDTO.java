package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import lombok.Data;

/**
 * DTO representing high-level summary metrics for the dashboard.
 *
 * <p>
 * Provides a quick overview of the current compliance state,
 * including deviation counts and checklist progress.
 * </p>
 */
@Data
public class DashboardSummaryDTO {

    /**
     * Total number of active (open or in-progress) deviations.
     */
    private int activeDeviationCount;

    /**
     * Number of deviations marked as critical severity.
     */
    private int criticalDeviationCount;

    /**
     * Indicates whether any critical deviations currently exist.
     */
    private boolean hasCriticalAlerts;

    /**
     * Number of checklist instances that are not yet completed.
     */
    private int pendingChecklistCount;

    /**
     * Number of checklist instances that are completed.
     */
    private int completedChecklistCount;
}