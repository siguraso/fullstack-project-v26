package edu.ntnu.idi.idatt2105.backend.core.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user within a tenant.
 *
 * <p>
 * Users belong to a tenant (organization) and are assigned a role
 * that determines their permissions. This entity stores authentication
 * credentials, personal information, and lifecycle metadata.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @NotBlank(message = "First name is required")
  @Column(length = 100)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Column(length = 100)
  private String lastName;

  @Column(name = "contact_phone", length = 30)
  private String phone;

  @Column(nullable = false)
  private boolean active = true;

  @NotBlank(message = "Username is required")
  @Column(unique = true)
  private String username;

  @NotBlank(message = "email is required")
  @Email
  @Column(unique = true, length = 255)
  private String email;

  @NotBlank(message = "password is required")
  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role = UserRole.STAFF;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
