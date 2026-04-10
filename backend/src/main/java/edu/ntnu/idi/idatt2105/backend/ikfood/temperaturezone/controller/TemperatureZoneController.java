package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.CreateTemperatureZoneRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.service.TemperatureZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing temperature zones in the IK Food module.
 * <p>
 * Temperature zones define the monitored areas (e.g. fridges, freezers) with
 * their acceptable temperature ranges. Logs are evaluated against these limits
 * when recorded.
 */
@RestController
@RequestMapping("/api/ikfood/temperature-zones")
@RequiredArgsConstructor
@Slf4j
public class TemperatureZoneController {

    private final TemperatureZoneService temperatureZoneService;

    /**
     * Creates a new temperature zone for the current tenant.
     *
     * @param request the zone details including name and temperature limits
     * @return a 201 Created response containing the persisted
     *         {@link TemperatureZoneDTO}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemperatureZoneDTO>> create(
            @Valid @RequestBody CreateTemperatureZoneRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(temperatureZoneService.create(request)));
    }

    /**
     * Returns all active temperature zones for the current tenant.
     *
     * @return a list of {@link TemperatureZoneDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<TemperatureZoneDTO>>> getForCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.ok(temperatureZoneService.getForCurrentTenant()));
    }

    /**
     * Updates an existing temperature zone's name and temperature limits.
     *
     * @param id      identifier of the zone to update
     * @param request the updated zone details
     * @return the updated {@link TemperatureZoneDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemperatureZoneDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateTemperatureZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(temperatureZoneService.update(id, request)));
    }

    /**
     * Soft-deletes a temperature zone by marking it as inactive.
     *
     * @param id identifier of the zone to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        temperatureZoneService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
