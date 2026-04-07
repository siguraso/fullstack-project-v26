package edu.ntnu.idi.idatt2105.backend.core.invitation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteRequest(
	@NotBlank(message = "Email is required")
	@Email(message = "Must be a valid email address")
	String email
) {}
