package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistService;
import lombok.RequiredArgsConstructor;

// TODO: use Api response from common
@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;
    private final ChecklistInstanceMapper mapper;

    @PostMapping("/templates")
    public ResponseEntity<?> createTemplate(@RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(service.createTemplate(request));
    }

    @PostMapping("/templates/{id}/generate")
    public ResponseEntity<?> generate(@PathVariable Long id) {
        return ResponseEntity.ok(service.generateInstance(id));
    }

    @GetMapping("/today")
    public ResponseEntity<?> today(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getTodayChecklist(tenantId));
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<?> complete(@PathVariable Long id, @RequestBody CompleteChecklistItemRequest request) {
        service.completeItem(id, request);
        return ResponseEntity.ok().build();
    }
}
