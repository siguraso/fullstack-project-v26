package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import lombok.Data;

@Data
public class CompleteChecklistItemRequest {

    private boolean completed;
    private String comment;
}
