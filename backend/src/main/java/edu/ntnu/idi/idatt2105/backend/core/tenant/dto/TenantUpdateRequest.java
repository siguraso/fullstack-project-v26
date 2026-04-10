package edu.ntnu.idi.idatt2105.backend.core.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO used to update an existing tenant (organization).
 *
 * <p>
 * All fields are optional and represent partial updates to tenant data,
 * such as name, contact information, and active status.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUpdateRequest {

    private String name;

    @Pattern(regexp = "^\\d{9}$", message = "Tenant number must be exactly 9 digits")
    private String orgNumber;

    private String address;

    private String city;

    private String country;

    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    private String contactPhone;

    private Boolean active;
}
