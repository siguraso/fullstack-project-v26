package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service.InspectionLogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inspection/logs")
@RequiredArgsConstructor
public class InspectionLogController {

    private final InspectionLogService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<InspectionLogDTO>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(service.getInspectionLogs()));
    }
}