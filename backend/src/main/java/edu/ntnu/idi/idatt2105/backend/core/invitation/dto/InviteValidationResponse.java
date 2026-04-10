package edu.ntnu.idi.idatt2105.backend.core.invitation.dto;

/**
 * Response DTO used to validate an invitation.
 *
 * <p>
 * Returned when checking whether an invitation link or token is valid.
 * Contains the validation result along with basic invitation context.
 * </p>
 */
public record InviteValidationResponse(
		boolean valid,
		String email,
		Long organizationId) {
}
