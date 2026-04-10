package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import lombok.Data;

/**
 * DTO representing the full dashboard overview response.
 *
 * <p>
 * This aggregates all key data needed to render the main dashboard view,
 * including summary metrics, checklist progress, deviations, and recent
 * activity.
 * </p>
 */
@Data
public class DashboardOverviewDTO {

    /**
     * High-level summary metrics for the dashboard.
     */
    private DashboardSummaryDTO summary;

    /**
     * Summary of today's checklist progress.
     */
    private ChecklistTodaySummaryDTO checklistsToday;

    /**
     * List of critical deviations requiring immediate attention.
     */
    private List<DeviationDTO> criticalAlerts;

    /**
     * List of active (open or in-progress) deviations.
     */
    private List<DeviationDTO> activeDeviations;

    /**
     * List of checklist instances that are not yet completed.
     */
    private List<ChecklistInstanceDTO> pendingChecklists;

    /**
     * Recent team activity for display in the dashboard feed.
     */
    private List<DashboardActivityDTO> teamActivity;
}