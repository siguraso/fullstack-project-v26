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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
@Slf4j
public class InvitationController {
  private final InvitationService invitationService;

  @PostMapping({"/invitations", "/users/invite"})
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> sendInvite(@Valid @RequestBody InviteRequest request) {
	invitationService.sendStaffInvite(request.email());
	return ResponseEntity.ok(ApiResponse.ok("Invitation sent"));
  }

  @GetMapping("/invitations/validate")
  public ResponseEntity<ApiResponse<InviteValidationResponse>> validateToken(
    @RequestParam("token") @NotBlank(message = "Token is required") String token) {
	InviteValidationResponse response = invitationService.validateInviteToken(token);
	return ResponseEntity.ok(ApiResponse.ok(response));
  }

}
