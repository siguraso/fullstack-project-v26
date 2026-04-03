package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import lombok.Data;

@Data
public class ChecklistTodaySummaryDTO {

    private int totalChecklists;
    private int completedChecklists;
    private int totalItems;
    private int completedItems;
}

