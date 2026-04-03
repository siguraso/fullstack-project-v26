package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import lombok.Data;

@Data
public class DashboardOverviewDTO {

    private DashboardSummaryDTO summary;
    private ChecklistTodaySummaryDTO checklistsToday;
    private List<DeviationDTO> criticalAlerts;
    private List<DeviationDTO> activeDeviations;
    private List<ChecklistInstanceDTO> pendingChecklists;
    private List<DashboardActivityDTO> teamActivity;
}

