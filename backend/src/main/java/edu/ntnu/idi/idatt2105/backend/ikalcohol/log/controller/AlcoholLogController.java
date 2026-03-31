package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.controller;

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
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper.AlcoholLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service.AlcoholLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ikalcohol/logs")
public class AlcoholLogController {

    private final AlcoholLogService service;
    private final AlcoholLogMapper mapper;

    public AlcoholLogController(AlcoholLogService service, AlcoholLogMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> createLog(@Valid @RequestBody AlcoholLogCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.createLog(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<AlcoholLogDTO>>> getLogs() {
        List<AlcoholLogDTO> logs = service.getAllForCurrentOrg().stream().map(mapper::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDTO(service.getById(id))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Alcohol compliance log deleted", null));
    }
}

