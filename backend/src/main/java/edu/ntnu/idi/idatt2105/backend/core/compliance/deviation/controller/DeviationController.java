package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.CreateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.UpdateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DeviationController {

    private final DeviationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DeviationDTO>> create(
            @RequestBody CreateDeviationRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(service.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviationDTO>>> getByTenant(
            @RequestParam Long tenantId) {

        return ResponseEntity.ok(ApiResponse.ok(service.getByTenant(tenantId)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviationDTO>> update(
            @PathVariable Long id,
            @RequestBody UpdateDeviationRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }
}
