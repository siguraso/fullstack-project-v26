package edu.ntnu.idi.idatt2105.backend.core.invitation.controller;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.invitation.dto.InviteRequest;
import edu.ntnu.idi.idatt2105.backend.core.invitation.dto.InviteValidationResponse;
import edu.ntnu.idi.idatt2105.backend.core.invitation.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.NotBlank;

/**
 * REST controller for managing staff invitations.
 * <p>
 * Provides endpoints for sending invitation emails to new staff members and
 * for validating invitation tokens before account registration.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
@Slf4j
public class InvitationController {
  private final InvitationService invitationService;

  /**
   * Sends a staff invitation email to the specified address. The recipient
   * receives a time-limited token they can use to register their account.
   *
   * @param request the invitation request containing the recipient email
   * @return a 200 OK response confirming the invitation was sent
   */
  @PostMapping({"/invitations", "/users/invite"})
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> sendInvite(@Valid @RequestBody InviteRequest request) {
	invitationService.sendStaffInvite(request.email());
	return ResponseEntity.ok(ApiResponse.ok("Invitation sent"));
  }

  /**
   * Validates an invitation token and returns the associated email and
   * organisation if the token is valid and not expired.
   *
   * @param token the invitation token from the registration link
   * @return an {@link InviteValidationResponse} with validation details
   */
  @GetMapping("/invitations/validate")
  public ResponseEntity<ApiResponse<InviteValidationResponse>> validateToken(
    @RequestParam("token") @NotBlank(message = "Token is required") String token) {
	InviteValidationResponse response = invitationService.validateInviteToken(token);
	return ResponseEntity.ok(ApiResponse.ok(response));
  }

}
