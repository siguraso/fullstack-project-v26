package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.controller;

import java.util.List;

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

@RestController
@RequestMapping("/api/ikfood/temperature-logs")
public class TemperatureLogController {

    private final TemperatureLogService service;
    private final TemperatureLogMapper mapper;

    public TemperatureLogController(TemperatureLogService service, TemperatureLogMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<TemperatureLogDTO>> createLog(@Valid @RequestBody TemperatureLogCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.createLog(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<TemperatureLogDTO>>> getLogs() {
        List<TemperatureLogDTO> logs = service.getAllForCurrentOrg().stream().map(mapper::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<TemperatureLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDTO(service.getById(id))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Temperature log deleted", null));
    }
}


