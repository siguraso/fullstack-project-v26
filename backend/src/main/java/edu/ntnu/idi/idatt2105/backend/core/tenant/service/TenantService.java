package edu.ntnu.idi.idatt2105.backend.core.tenant.service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantDTO;
import edu.ntnu.idi.idatt2105.backend.core.tenant.mapper.TenantMapper;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;


    public TenantDTO createTenant(TenantCreateRequest request) {
        log.info("Creating new tenant with org number: {}", request.getOrgNumber());

        if (tenantRepository.findByOrgNumber(request.getOrgNumber()) != null) {
            log.warn("Tenant with org number {} already exists", request.getOrgNumber());
            throw new ValidationException("Tenant number already exists: " + request.getOrgNumber());
        }

        // Map DTO to entity
        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setActive(true); // New tenants are active by default

        // Save to database
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant created successfully with ID: {}", savedTenant.getId());

        return tenantMapper.toDTO(savedTenant);
    }


    @Transactional(readOnly = true)
    public TenantDTO getTenantById(Long id) {
        log.debug("Fetching tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tenant not found with ID: {}", id);
                    return new ResourceNotFoundException("Tenant not found with ID: " + id);
                });

        return tenantMapper.toDTO(tenant);
    }


    @Transactional(readOnly = true)
    public List<TenantDTO> getAllTenants() {
        log.debug("Fetching all tenants");

        List<Tenant> tenants = tenantRepository.findAll();
        log.debug("Retrieved {} tenants", tenants.size());

        return tenants.stream()
                .map(tenantMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<TenantDTO> searchTenantsByName(String name) {
        log.debug("Searching tenants with name containing: {}", name);

        List<Tenant> tenants = tenantRepository.findByName(name);
        log.debug("Found {} tenants matching name: {}", tenants.size(), name);

        return tenants.stream()
                .map(tenantMapper::toDTO)
                .collect(Collectors.toList());
    }


    public TenantDTO updateTenant(Long id, TenantUpdateRequest request) {
        log.info("Updating tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tenant not found with ID: {}", id);
                    return new ResourceNotFoundException("Tenant not found with ID: " + id);
                });

        // Check if new org number is already taken by another tenant
        if (request.getOrgNumber() != null
            && !request.getOrgNumber().equals(tenant.getOrgNumber())
            && tenantRepository.findByOrgNumber(request.getOrgNumber()) != null) {
                log.warn("Tenant number {} already exists", request.getOrgNumber());
                throw new ValidationException("Tenant number already exists: " + request.getOrgNumber());
        }

        // Update fields
        tenantMapper.updateEntityFromRequest(request, tenant);

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Tenant with ID {} updated successfully", id);

        return tenantMapper.toDTO(updatedTenant);
    }

    public void deleteTenant(Long id) {
        log.info("Deleting tenants with ID: {}", id);

        if (!tenantRepository.existsById(id)) {
            log.warn("Tenant not found with ID: {}", id);
            throw new ResourceNotFoundException("Tenant not found with ID: " + id);
        }

        tenantRepository.deleteById(id);
        log.info("Tenant with ID {} deleted successfully", id);
    }
}
