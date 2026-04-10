package edu.ntnu.idi.idatt2105.backend.core.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.dashboard.dto.DashboardOverviewDTO;
import edu.ntnu.idi.idatt2105.backend.core.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing the compliance dashboard endpoints.
 * <p>
 * Provides a single aggregated overview of today's checklists, active
 * deviations, critical alerts and recent team activity for the current tenant.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Compliance dashboard endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Returns an aggregated dashboard overview for the current tenant,
     * including today's checklist summary, pending checklists, active
     * deviations, critical alerts, and recent team activity.
     *
     * @return a {@link DashboardOverviewDTO} containing all dashboard sections
     */
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @Operation(summary = "Get dashboard overview")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getOverview()));
    }
}
