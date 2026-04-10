package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.CreateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.UpdateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing compliance deviations.
 * <p>
 * Provides endpoints for creating, listing and updating deviations for the
 * current tenant. Deviations can originate from manual reports or be
 * automatically generated from critical compliance log entries.
 */
@RestController
@RequestMapping("/api/deviations")
@RequiredArgsConstructor
@Slf4j
public class DeviationController {

    private final DeviationService service;

    /**
     * Creates a new deviation for the current tenant.
     *
     * @param request the deviation details including title, module, severity
     *                and status
     * @return a 201 Created response containing the persisted
     *         {@link DeviationDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<DeviationDTO>> create(
            @Valid @RequestBody CreateDeviationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.create(request)));
    }

    /**
     * Retrieves all deviations for the current tenant, ordered newest first.
     *
     * @return a list of {@link DeviationDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<DeviationDTO>>> getForCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.ok(service.getForCurrentTenant()));
    }

    /**
     * Updates an existing deviation, typically used to change status or add
     * resolution details.
     *
     * @param id      identifier of the deviation to update
     * @param request fields to update, such as status or resolution notes
     * @return the updated {@link DeviationDTO}
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DeviationDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeviationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }
}
