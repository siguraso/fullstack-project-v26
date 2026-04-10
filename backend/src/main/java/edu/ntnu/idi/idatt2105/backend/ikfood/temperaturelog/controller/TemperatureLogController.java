package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.controller;

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
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.mapper.TemperatureLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.service.TemperatureLogService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing temperature compliance logs in the IK Food
 * module.
 * <p>
 * Provides endpoints for recording, listing and deleting temperature
 * compliance log entries. Each log is linked to a temperature zone and
 * automatically evaluated against its configured limits.
 */
@RestController
@RequestMapping("/api/ikfood/temperature-logs")
@Slf4j
public class TemperatureLogController {

    private final TemperatureLogService service;
    private final TemperatureLogMapper mapper;

    public TemperatureLogController(TemperatureLogService service, TemperatureLogMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Records a new temperature compliance log for the current tenant. The
     * log status is automatically set based on the measured temperature
     * relative to the zone's configured limits.
     *
     * @param request the log details including zone identifier and temperature
     * @return a 201 Created response containing the persisted
     *         {@link TemperatureLogDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<TemperatureLogDTO>> createLog(
            @Valid @RequestBody TemperatureLogCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.createLog(request)));
    }

    /**
     * Returns all temperature compliance logs for the current tenant.
     *
     * @return a list of {@link TemperatureLogDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<TemperatureLogDTO>>> getLogs() {
        List<TemperatureLogDTO> logs = service.getAllForCurrentOrg().stream().map(mapper::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    /**
     * Retrieves a single temperature compliance log by its identifier.
     *
     * @param id the log identifier
     * @return the {@link TemperatureLogDTO} for the requested entry
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<TemperatureLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDTO(service.getById(id))));
    }

    /**
     * Deletes a temperature compliance log entry.
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
