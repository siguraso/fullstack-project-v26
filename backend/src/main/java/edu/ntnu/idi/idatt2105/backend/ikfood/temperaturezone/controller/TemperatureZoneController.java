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

@RestController
@RequestMapping("/api/ikfood/temperature-zones")
@RequiredArgsConstructor
@Slf4j
public class TemperatureZoneController {

    private final TemperatureZoneService temperatureZoneService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemperatureZoneDTO>> create(
            @Valid @RequestBody CreateTemperatureZoneRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(temperatureZoneService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<TemperatureZoneDTO>>> getForCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.ok(temperatureZoneService.getForCurrentTenant()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemperatureZoneDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateTemperatureZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(temperatureZoneService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        temperatureZoneService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
