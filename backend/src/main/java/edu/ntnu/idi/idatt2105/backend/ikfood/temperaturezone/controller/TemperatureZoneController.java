package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.controller;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.CreateTemperatureZoneRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.service.TemperatureZoneService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ikfood/temperature-zones")
@RequiredArgsConstructor
public class TemperatureZoneController {

    private final TemperatureZoneService temperatureZoneService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TemperatureZoneDTO>> create(
            @Valid @RequestBody CreateTemperatureZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Temperature zone created", temperatureZoneService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<TemperatureZoneDTO>>> getForCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.ok(temperatureZoneService.getForCurrentTenant()));
    }
}

