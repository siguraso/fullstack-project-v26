package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used to update the completion state of a checklist item.
 *
 * <p>
 * This is typically sent from the frontend when a user toggles a checklist item
 * and optionally provides a comment.
 * </p>
 */
@Data
public class CompleteChecklistItemRequest {

    /**
     * Indicates whether the checklist item is completed or not.
     */
    @NotNull(message = "Completed is required")
    private Boolean completed;

    /**
     * Optional comment describing the completion or any notes related to the item.
     */
    @Size(max = 2000, message = "Comment must be 2000 characters or fewer")
    private String comment;
}