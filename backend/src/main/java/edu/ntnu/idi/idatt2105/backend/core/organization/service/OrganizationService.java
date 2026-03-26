package edu.ntnu.idi.idatt2105.backend.core.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationDTO;
import edu.ntnu.idi.idatt2105.backend.core.organization.dto.OrganizationUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.organization.entity.Organization;
import edu.ntnu.idi.idatt2105.backend.core.organization.mapper.OrganizationMapper;
import edu.ntnu.idi.idatt2105.backend.core.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;


    public OrganizationDTO createOrganization(OrganizationCreateRequest request) {
        log.info("Creating new organization with org number: {}", request.getOrgNumber());

        if (organizationRepository.findByOrgNumber(request.getOrgNumber()) != null) {
            log.warn("Organization with org number {} already exists", request.getOrgNumber());
            throw new IllegalArgumentException("Organization number already exists: " + request.getOrgNumber());
        }

        // Map DTO to entity
        Organization organization = organizationMapper.toEntity(request);
        organization.setActive(true); // New organizations are active by default

        // Save to database
        Organization savedOrganization = organizationRepository.save(organization);
        log.info("Organization created successfully with ID: {}", savedOrganization.getId());

        return organizationMapper.toDTO(savedOrganization);
    }


    @Transactional(readOnly = true)
    public OrganizationDTO getOrganizationById(Long id) {
        log.debug("Fetching organization with ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Organization not found with ID: {}", id);
                    return new IllegalArgumentException("Organization not found with ID: " + id);
                });

        return organizationMapper.toDTO(organization);
    }


    @Transactional(readOnly = true)
    public List<OrganizationDTO> getAllOrganizations() {
        log.debug("Fetching all organizations");

        List<Organization> organizations = organizationRepository.findAll();
        log.debug("Retrieved {} organizations", organizations.size());

        return organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrganizationDTO> searchOrganizationsByName(String name) {
        log.debug("Searching organizations with name containing: {}", name);

        List<Organization> organizations = organizationRepository.findByName(name);
        log.debug("Found {} organizations matching name: {}", organizations.size(), name);

        return organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
    }


    public OrganizationDTO updateOrganization(Long id, OrganizationUpdateRequest request) {
        log.info("Updating organization with ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Organization not found with ID: {}", id);
                    return new IllegalArgumentException("Organization not found with ID: " + id);
                });

        // Check if new org number is already taken by another organization
        if (request.getOrgNumber() != null
            && !request.getOrgNumber().equals(organization.getOrgNumber())
            && organizationRepository.findByOrgNumber(request.getOrgNumber()) != null) {
                log.warn("Organization number {} already exists", request.getOrgNumber());
                throw new IllegalArgumentException("Organization number already exists: " + request.getOrgNumber());
        }

        // Update fields
        organizationMapper.updateEntityFromRequest(request, organization);

        Organization updatedOrganization = organizationRepository.save(organization);
        log.info("Organization with ID {} updated successfully", id);

        return organizationMapper.toDTO(updatedOrganization);
    }

    public void deleteOrganization(Long id) {
        log.info("Deleting organization with ID: {}", id);

        if (!organizationRepository.existsById(id)) {
            log.warn("Organization not found with ID: {}", id);
            throw new IllegalArgumentException("Organization not found with ID: " + id);
        }

        organizationRepository.deleteById(id);
        log.info("Organization with ID {} deleted successfully", id);
    }
}
