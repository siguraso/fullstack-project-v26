package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistItemLibraryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/checklist-library")
@RequiredArgsConstructor
public class ChecklistItemLibraryController {

    private final ChecklistItemLibraryService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam Long tenantId,
            @RequestBody ChecklistItemLibraryDTO request) {
        return ResponseEntity.ok(service.create(tenantId, request));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getAll(tenantId));
    }
}