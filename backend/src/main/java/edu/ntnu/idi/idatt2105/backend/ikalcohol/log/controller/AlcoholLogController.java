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
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service.AlcoholLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ikalcohol/logs")
public class AlcoholLogController {

    private final AlcoholLogService service;

    public AlcoholLogController(AlcoholLogService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> createLog(@Valid @RequestBody AlcoholLogCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.createLog(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<AlcoholLogDTO>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllForCurrentOrgAsDTO()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AlcoholLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByIdAsDTO(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Alcohol compliance log deleted", null));
    }
}
