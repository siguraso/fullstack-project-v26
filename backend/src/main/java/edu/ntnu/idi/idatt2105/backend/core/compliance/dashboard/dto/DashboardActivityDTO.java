package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto;

import lombok.Data;

/**
 * DTO representing a single activity entry in the dashboard feed.
 *
 * <p>
 * Used to display recent events such as deviations, checklist updates,
 * or compliance logs. Each activity contains a type, descriptive content,
 * the actor responsible, and a reference to the underlying entity.
 * </p>
 */
@Data
public class DashboardActivityDTO {

    /**
     * The type of activity (e.g., DEVIATION, CHECKLIST, TEMPERATURE_LOG).
     */
    private String type;

    /**
     * Short title summarizing the activity.
     */
    private String title;

    /**
     * Detailed description of what happened.
     */
    private String description;

    /**
     * Name or identifier of the user who performed the action.
     */
    private String actor;

    /**
     * Timestamp of when the activity occurred (ISO string).
     */
    private String occurredAt;

    /**
     * Reference ID to the related entity (e.g., deviation ID, checklist ID).
     */
    private Long referenceId;
}