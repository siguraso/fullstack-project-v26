package edu.ntnu.idi.idatt2105.backend.core.organization.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationDTO;
import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.organization.entity.Organization;

@Component
public class OrganizationMapper {

    /**
     * Convert Organization entity to DTO (for API responses)
     * 
     * @param entity the organization entity
     * @return the organization DTO
     */
    public OrganizationDTO toDTO(Organization entity) {
        if (entity == null) {
            return null;
        }

        OrganizationDTO dto = new OrganizationDTO();
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
     * Convert OrganizationCreateRequest DTO to Organization entity (for creation)
     * 
     * @param request the create request DTO
     * @return the organization entity
     */
    public Organization toEntity(OrganizationCreateRequest request) {
        if (request == null) {
            return null;
        }

        Organization entity = new Organization();
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
     * Update an Organization entity with data from OrganizationUpdateRequest DTO
     * 
     * @param request the update request DTO
     * @param entity the organization entity to update
     */
    public void updateEntityFromRequest(OrganizationUpdateRequest request, Organization entity) {
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

