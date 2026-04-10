package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service.AlcoholLogService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing alcohol compliance logs in the IK Alcohol
 * module.
 * <p>
 * Provides endpoints for creating, retrieving and deleting alcohol compliance
 * log entries scoped to the current tenant.
 */
@RestController
@RequestMapping("/api/ikalcohol/logs")
@Slf4j
public class AlcoholLogController {

    private final AlcoholLogService service;

    public AlcoholLogController(AlcoholLogService service) {
        this.service = service;
    }

    /**
     * Creates a new alcohol compliance log entry for the current tenant.
     *
     * @param request the log details including log type, status and optional
     *                refusal information
     * @return a 201 Created response containing the persisted
     *         {@link AlcoholLogDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> createLog(@Valid @RequestBody AlcoholLogCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.createLog(request)));
    }

    /**
     * Returns all alcohol compliance logs for the current tenant.
     *
     * @return a list of {@link AlcoholLogDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<AlcoholLogDTO>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllForCurrentOrgAsDTO()));
    }

    /**
     * Retrieves a single alcohol compliance log by its identifier.
     *
     * @param id the log identifier
     * @return the {@link AlcoholLogDTO} for the requested entry
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByIdAsDTO(id)));
    }

    /**
     * Deletes an alcohol compliance log entry.
     *
     * @param id identifier of the log to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
