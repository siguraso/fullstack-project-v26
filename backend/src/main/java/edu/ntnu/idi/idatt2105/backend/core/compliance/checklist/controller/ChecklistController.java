package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;

    @PostMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistTemplateDTO>> createTemplate(
            @Valid @RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.createTemplate(request)));
    }

    @PostMapping("/templates/{id}/generate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistInstanceDTO>> generate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.generateInstance(id)));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateTemplate(id, request)));
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ChecklistTemplateDTO>>> getTemplates() {
        return ResponseEntity.ok(ApiResponse.ok(service.getTemplates()));
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ChecklistInstanceDTO>>> today() {
        return ResponseEntity.ok(ApiResponse.ok(service.getTodayChecklist()));
    }

    @PatchMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<Void>> complete(
            @PathVariable Long id,
            @Valid @RequestBody CompleteChecklistItemRequest request) {
        service.completeItem(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Checklist item updated", null));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        service.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.ok("Checklist template deleted", null));
    }

    @PatchMapping("/templates/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable Long id) {
        service.toggleTemplate(id);
        return ResponseEntity.ok(ApiResponse.ok("Checklist template toggled", null));
    }
}
