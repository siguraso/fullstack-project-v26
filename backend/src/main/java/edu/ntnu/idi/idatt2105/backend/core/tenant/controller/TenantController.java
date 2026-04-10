package edu.ntnu.idi.idatt2105.backend.core.tenant.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantDTO;
import edu.ntnu.idi.idatt2105.backend.core.tenant.dto.TenantUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing tenant organisations.
 * <p>
 * Provides CRUD operations for tenants. All endpoints require the
 * {@code ADMIN} role. The {@code /current} sub-resource gives convenient
 * access to the organisation of the authenticated user.
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class TenantController {

    private final TenantService tenantService;

    /**
     * Returns the tenant organisation of the currently authenticated user.
     *
     * @return the {@link TenantDTO} for the current tenant
     */
    @GetMapping("/current")
    public ResponseEntity<TenantDTO> getCurrentTenant() {
        return ResponseEntity.ok(tenantService.getCurrentTenant());
    }

    /**
     * Updates the tenant organisation of the currently authenticated user.
     *
     * @param request the updated tenant details
     * @return the updated {@link TenantDTO}
     */
    @PutMapping("/current")
    public ResponseEntity<TenantDTO> updateCurrentTenant(@Valid @RequestBody TenantUpdateRequest request) {
        return ResponseEntity.ok(tenantService.updateCurrentTenant(request));
    }

    /**
     * Creates a new tenant organisation.
     *
     * @param request the tenant creation details including organisation number
     * @return a 201 Created response with the persisted {@link TenantDTO} and
     *         a {@code Location} header pointing to the new resource
     */
    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@Valid @RequestBody TenantCreateRequest request) {
        TenantDTO created = tenantService.createTenant(request);
        return ResponseEntity.created(URI.create("/api/tenants/" + created.getId())).body(created);
    }

    /**
     * Retrieves a single tenant by its identifier.
     *
     * @param id the tenant identifier
     * @return the {@link TenantDTO} for the requested tenant
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    /**
     * Lists all tenants or searches by name.
     *
     * @param name optional name fragment to filter results; returns all tenants
     *             if omitted or blank
     * @return a list of {@link TenantDTO} objects
     */
    @GetMapping
    public ResponseEntity<List<TenantDTO>> getTenants(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(tenantService.searchTenantsByName(name));
        }
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    /**
     * Updates a tenant by its identifier.
     *
     * @param id      identifier of the tenant to update
     * @param request the updated tenant details
     * @return the updated {@link TenantDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantUpdateRequest request) {
        return ResponseEntity.ok(tenantService.updateTenant(id, request));
    }

    /**
     * Permanently deletes a tenant by its identifier.
     *
     * @param id identifier of the tenant to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
