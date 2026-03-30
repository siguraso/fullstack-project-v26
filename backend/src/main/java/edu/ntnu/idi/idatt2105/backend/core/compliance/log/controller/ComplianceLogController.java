package edu.ntnu.idi.idatt2105.backend.core.compliance.log.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.ComplianceLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.LogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.ComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.service.ComplianceLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ComplianceLogController {

    private final ComplianceLogService service;
    private final ComplianceLogMapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<ComplianceLogDTO>> createLog(@Valid @RequestBody LogCreateRequest request) {
        var saved = service.createLog(request);
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDTO(saved)));
    }
}
