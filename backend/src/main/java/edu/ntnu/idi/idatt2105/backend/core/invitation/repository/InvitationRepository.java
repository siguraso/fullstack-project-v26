package edu.ntnu.idi.idatt2105.backend.core.invitation.repository;

import edu.ntnu.idi.idatt2105.backend.core.invitation.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
