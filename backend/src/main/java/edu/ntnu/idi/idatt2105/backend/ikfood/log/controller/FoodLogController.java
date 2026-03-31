package edu.ntnu.idi.idatt2105.backend.ikfood.log.controller;

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
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.mapper.FoodLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.service.FoodLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ikfood/logs")
public class FoodLogController {

    private final FoodLogService service;
    private final FoodLogMapper mapper;

    public FoodLogController(FoodLogService service, FoodLogMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<FoodLogDTO>> createLog(@Valid @RequestBody FoodLogCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.createLog(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<FoodLogDTO>>> getLogs() {
        List<FoodLogDTO> logs = service.getAllForCurrentOrg().stream().map(mapper::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<FoodLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDTO(service.getById(id))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Food compliance log deleted", null));
    }
}

