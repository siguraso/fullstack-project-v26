package edu.ntnu.idi.idatt2105.backend.core.tenant.service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantDTO;
import edu.ntnu.idi.idatt2105.backend.core.tenant.mapper.TenantMapper;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing tenant organisations.
 * <p>
 * Provides CRUD operations and name-based search for tenants. The
 * {@code getCurrentTenant()} method resolves the organisation from the
 * authenticated user's
 * {@link edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private static final String TENANT_NOT_FOUND_LOG = "Tenant not found with ID: {}";
    private static final String TENANT_NOT_FOUND_MSG = "Tenant not found with ID: ";

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    /**
     * Creates a new tenant organisation.
     *
     * @param request the creation request including organisation number and
     *                contact details
     * @return the persisted {@link TenantDTO}
     * @throws ValidationException if the organisation number already exists
     */
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

    /**
     * Retrieves a tenant by its identifier.
     *
     * @param id the tenant identifier
     * @return the {@link TenantDTO} for the requested tenant
     */
    @Transactional(readOnly = true)
    public TenantDTO getTenantById(Long id) {
        Long currentTenantId = TenantContext.getCurrentOrg();

        if (!id.equals(currentTenantId)) {
            throw new UnauthorizedException("Access denied");
        }

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        return tenantMapper.toDTO(tenant);
    }

    /**
     * Returns the tenant of the currently authenticated user.
     *
     * @return the {@link TenantDTO} for the current tenant
     */
    @Transactional(readOnly = true)
    public TenantDTO getCurrentTenant() {
        Long currentOrgId = TenantContext.getCurrentOrg();

        Tenant tenant = tenantRepository.findById(currentOrgId)
                .orElseThrow(() -> {
                    log.warn(TENANT_NOT_FOUND_LOG, currentOrgId);
                    return new ResourceNotFoundException(TENANT_NOT_FOUND_MSG + currentOrgId);
                });

        return tenantMapper.toDTO(tenant);
    }

    /**
     * Updates a tenant's details by its identifier.
     *
     * @param id      identifier of the tenant to update
     * @param request the updated tenant fields
     * @return the updated {@link TenantDTO}
     * @throws ValidationException if the new organisation number is already in
     *                             use by another tenant
     */
    public TenantDTO updateTenant(Long id, TenantUpdateRequest request) {
        log.info("Updating tenant with ID: {}", id);

        Long currentTenantId = TenantContext.getCurrentOrg();

        if (!id.equals(currentTenantId)) {
            throw new UnauthorizedException("Access denied");
        }

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(TENANT_NOT_FOUND_LOG, id);
                    return new ResourceNotFoundException(TENANT_NOT_FOUND_MSG + id);
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

    /**
     * Updates the tenant of the currently authenticated user.
     *
     * @param request the updated tenant fields
     * @return the updated {@link TenantDTO}
     */
    public TenantDTO updateCurrentTenant(TenantUpdateRequest request) {
        return updateTenant(TenantContext.getCurrentOrg(), request);
    }

    /**
     * Permanently deletes a tenant by its identifier.
     *
     * @param id identifier of the tenant to delete
     */
    public void deleteTenant(Long id) {
        Long currentTenantId = TenantContext.getCurrentOrg();

        if (!id.equals(currentTenantId)) {
            throw new UnauthorizedException("Access denied");
        }

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        tenantRepository.delete(tenant);
    }
}
