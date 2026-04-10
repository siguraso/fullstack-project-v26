package edu.ntnu.idi.idatt2105.backend.core.invitation.entity;

import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Represents an invitation sent to a user to join a tenant.
 *
 * <p>
 * An invitation contains a unique token, recipient email, expiration time,
 * and status indicating whether it has been accepted.
 * </p>
 */
@Data
@Entity
@Table(name = "invitations")
public class Invitation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(nullable = false, length = 1024)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private boolean accepted = false;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
