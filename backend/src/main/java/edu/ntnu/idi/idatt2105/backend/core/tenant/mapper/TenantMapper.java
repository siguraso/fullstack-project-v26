package edu.ntnu.idi.idatt2105.backend.core.tenant.mapper;

import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantDTO;

@Component
public class TenantMapper {

    /**
     * Convert Tenant entity to DTO (for API responses)
     * 
     * @param entity the Tenant entity
     * @return the Tenant DTO
     */
    public TenantDTO toDTO(Tenant entity) {
        if (entity == null) {
            return null;
        }

        TenantDTO dto = new TenantDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setOrgNumber(entity.getOrgNumber());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setContactEmail(entity.getContactEmail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setActive(entity.isActive());
        
        // Format timestamps as strings
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().toString());
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().toString());
        }

        return dto;
    }

    /**
     * Convert TenantCreateRequest DTO to Tenant entity (for creation)
     * 
     * @param request the create request DTO
     * @return the Tenant entity
     */
    public Tenant toEntity(TenantCreateRequest request) {
        if (request == null) {
            return null;
        }

        Tenant entity = new Tenant();
        entity.setName(request.getName());
        entity.setOrgNumber(request.getOrgNumber());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setCountry(request.getCountry());
        entity.setContactEmail(request.getContactEmail());
        entity.setContactPhone(request.getContactPhone());

        return entity;
    }

    /**
     * Update an Tenant entity with data from TenantUpdateRequest DTO
     * 
     * @param request the update request DTO
     * @param entity the Tenant entity to update
     */
    public void updateEntityFromRequest(TenantUpdateRequest request, Tenant entity) {
        if (request == null || entity == null) {
            return;
        }

        // Only update fields that are provided (not null)
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getOrgNumber() != null) {
            entity.setOrgNumber(request.getOrgNumber());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            entity.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            entity.setCountry(request.getCountry());
        }
        if (request.getContactEmail() != null) {
            entity.setContactEmail(request.getContactEmail());
        }
        if (request.getContactPhone() != null) {
            entity.setContactPhone(request.getContactPhone());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}

