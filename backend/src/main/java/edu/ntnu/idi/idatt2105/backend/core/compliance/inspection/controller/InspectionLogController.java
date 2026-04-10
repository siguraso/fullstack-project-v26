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

/**
 * REST controller exposing endpoints for the inspection overview module.
 * <p>
 * Provides access to aggregated compliance log statistics, a flat log listing
 * across all modules, and PDF export with optional filtering.
 */
@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
@Slf4j
public class InspectionLogController {

    private final InspectionLogService service;
    private final InspectionPdfExportService pdfExportService;

    /**
     * Returns all inspection logs for the current tenant across all compliance
     * modules (deviations, temperature, alcohol), sorted newest first.
     *
     * @return a list of {@link InspectionLogDTO} objects
     */
    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<InspectionLogDTO>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(service.getInspectionLogs()));
    }

    /**
     * Returns aggregated statistics for all compliance modules for the current
     * tenant, including deviation counts by status and severity and log counts
     * by status for temperature and alcohol.
     *
     * @return an {@link InspectionStatsDTO} containing all stat fields
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<InspectionStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getStats()));
    }

    /**
     * Generates and streams a PDF report of inspection logs matching the given
     * filter criteria. The response is sent as an attachment.
     *
     * @param filter optional filters for type, severity, status and date range
     * @return a PDF byte stream with {@code Content-Disposition: attachment}
     */
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
