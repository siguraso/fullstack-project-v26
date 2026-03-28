package edu.ntnu.idi.idatt2105.backend.core.tenant.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener.class)
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tenant name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Tenant number is required")
    @Pattern(regexp = "^\\d{9}$", message = "Tenant number must be exactly 9 digits")
    @Column(name = "org_number", nullable = false, unique = true, length = 20)
    private String orgNumber;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @NotBlank(message = "Country is required")
    @Column(nullable = false, length = 100)
    private String country;

    @Email(message = "Contact email must be a valid email address")
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}


