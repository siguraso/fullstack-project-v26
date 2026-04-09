package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistPresetDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistPresetService;
import lombok.RequiredArgsConstructor;

/**
 * Read-only endpoint for global checklist preset items.
 *
 * <p>Presets are Mattilsynet-derived IK compliance items available to all authenticated users.
 * The response is a flat list; the frontend groups by {@code tab} and {@code groupLabel}.
 *
 * <p><b>How to add more presets:</b> Insert rows into the {@code checklist_presets} table in
 * {@code data.sql}. Required fields: {@code title}, {@code description}, {@code category}
 * ({@code IK_FOOD} or {@code IK_ALCOHOL}), {@code priority} ({@code LOW} or {@code HIGH}),
 * {@code tab} ({@code IK_FOOD}, {@code IK_ALCOHOL}, or {@code HACCP}),
 * {@code group_label} (free text sub-category), {@code sort_order} (integer, lower = first).
 */
@RestController
@RequestMapping("/api/checklist-presets")
@RequiredArgsConstructor
public class ChecklistPresetController {

    private final ChecklistPresetService service;

    /**
     * Returns all compliance preset items ordered by tab and sort order.
     *
     * @return 200 OK with list of {@link ChecklistPresetDTO}
     */
    @GetMapping
    public ResponseEntity<List<ChecklistPresetDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
