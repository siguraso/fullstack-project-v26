package edu.ntnu.idi.idatt2105.backend.core.invitation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO used to invite a user to a tenant.
 *
 * <p>
 * Contains the email address of the user to be invited.
 * The system will typically use this to send an invitation
 * and create a pending user association.
 * </p>
 */
public record InviteRequest(
        @NotBlank(message = "Email is required") @Email(message = "Must be a valid email address") String email) {
}
