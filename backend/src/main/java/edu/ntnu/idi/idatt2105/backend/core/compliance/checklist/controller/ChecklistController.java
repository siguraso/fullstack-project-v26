package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistService;
import lombok.RequiredArgsConstructor;

// TODO: use Api response from common
@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;

    @PostMapping("/templates")
    public ResponseEntity<?> createTemplate(@RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(service.createTemplate(request));
    }

    @PostMapping("/templates/{id}/generate")
    public ResponseEntity<?> generate(@PathVariable Long id) {
        return ResponseEntity.ok(service.generateInstance(id));
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long id,
            @RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(service.updateTemplate(id, request));
    }

    @GetMapping("/templates")
    public ResponseEntity<?> getTemplates(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getTemplates(tenantId));
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

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id) {
        service.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/templates/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        service.toggleTemplate(id);
        return ResponseEntity.ok().build();
    }
}
