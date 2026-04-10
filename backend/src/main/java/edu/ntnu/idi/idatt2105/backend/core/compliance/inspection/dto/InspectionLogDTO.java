package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto;

import lombok.Data;

@Data
public class InspectionLogDTO {

    private String type; // DEVIATION | TEMPERATURE | ALCOHOL

    private Long referenceId;

    private String title;
    private String description;

    private String status;
    private String severity;

    private String module;

    private String actor;

    private String timestamp;
}