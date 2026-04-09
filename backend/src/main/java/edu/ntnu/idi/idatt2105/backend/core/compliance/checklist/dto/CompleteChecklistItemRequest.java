package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompleteChecklistItemRequest {

    @NotNull(message = "Completed is required")
    private Boolean completed;

    @Size(max = 2000, message = "Comment must be 2000 characters or fewer")
    private String comment;
}
