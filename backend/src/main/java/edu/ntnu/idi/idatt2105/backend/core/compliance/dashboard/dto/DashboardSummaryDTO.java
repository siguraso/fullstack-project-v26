package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {

    private int activeDeviationCount;
    private int criticalDeviationCount;
    private boolean hasCriticalAlerts;
    private int pendingChecklistCount;
    private int completedChecklistCount;
}


