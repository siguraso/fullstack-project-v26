package edu.ntnu.idi.idatt2105.backend.core.invitation.dto;

/**
 * Response DTO returned when an invite link is generated without sending an email.
 *
 * <p>
 * Contains the invite URL that can be shared manually with the recipient.
 * </p>
 */
public record InviteLinkResponse(String inviteUrl) {
}
