package edu.ntnu.idi.idatt2105.backend.core.invitation.dto;

public record InviteValidationResponse(
	boolean valid,
	String email,
	Long organizationId
) {}
