package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import lombok.Data;

@Data
public class DashboardActivityDTO {

    private String type;
    private String title;
    private String description;
    private String actor;
    private String occurredAt;
    private Long referenceId;
}

