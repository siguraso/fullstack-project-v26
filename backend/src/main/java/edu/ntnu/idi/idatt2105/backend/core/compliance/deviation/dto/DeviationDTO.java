package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto;

import lombok.Data;

@Data
public class DeviationDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String severity;
    private String category;

    private Long checklistItemId;
    private Long logId;

    private String createdAt;
    private String resolvedAt;
}
