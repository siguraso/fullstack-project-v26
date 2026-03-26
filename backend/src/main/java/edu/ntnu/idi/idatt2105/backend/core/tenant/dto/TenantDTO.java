package edu.ntnu.idi.idatt2105.backend.core.tenant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {
  private long id;
  private String name;
  
  @JsonProperty("org_number")
  private String orgNumber;
  
  private String address;
  private String city;
  private String country;
  
  @JsonProperty("contact_email")
  private String contactEmail;
  
  @JsonProperty("contact_phone")
  private String contactPhone;
  
  private boolean active;
  
  @JsonProperty("created_at")
  private String createdAt;
  
  @JsonProperty("updated_at")
  private String updatedAt;
}
