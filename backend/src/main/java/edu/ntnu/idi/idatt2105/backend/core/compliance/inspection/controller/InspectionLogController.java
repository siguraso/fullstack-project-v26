package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionExportFilter;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionStatsDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service.InspectionLogService;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service.InspectionPdfExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
@Slf4j
public class InspectionLogController {

    private final InspectionLogService service;
    private final InspectionPdfExportService pdfExportService;

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<InspectionLogDTO>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(service.getInspectionLogs()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<InspectionStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getStats()));
    }

    @PostMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<byte[]> exportPdf(@RequestBody InspectionExportFilter filter) {
        byte[] pdf = pdfExportService.generatePdf(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inspection-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
