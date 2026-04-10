package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing endpoints for managing checklist templates and
 * instances in the compliance module.
 * <p>
 * Provides operations for creating, updating, listing and deleting checklist
 * templates, generating checklist instances and marking checklist items as
 * complete.
 */
@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
@Slf4j
public class ChecklistController {

    private final ChecklistService service;

    /**
     * Creates a new checklist template for the current tenant.
     *
     * @param request the template definition including title, frequency and
     *                items
     * @return a 201 Created response containing the persisted
     *         {@link ChecklistTemplateDTO}
     */
    @PostMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistTemplateDTO>> createTemplate(
            @Valid @RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.createTemplate(request)));
    }

    /**
     * Generates a new checklist instance from the given template identifier.
     * Typically used to create today's checklist based on a reusable
     * definition.
     *
     * @param id the identifier of the checklist template
     * @return a 201 Created response containing the generated
     *         {@link ChecklistInstanceDTO}
     */
    @PostMapping("/templates/{id}/generate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistInstanceDTO>> generate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.generateInstance(id)));
    }

    /**
     * Updates an existing checklist template.
     *
     * @param id      identifier of the template to update
     * @param request the new template definition
     * @return the updated {@link ChecklistTemplateDTO}
     */
    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CreateChecklistTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateTemplate(id, request)));
    }

    /**
     * Retrieves all checklist templates available to the current tenant.
     *
     * @return a list of {@link ChecklistTemplateDTO} objects
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ChecklistTemplateDTO>>> getTemplates() {
        return ResponseEntity.ok(ApiResponse.ok(service.getTemplates()));
    }

    /**
     * Retrieves checklist instances that are relevant for the current day for
     * the authenticated tenant.
     *
     * @return a list of today's {@link ChecklistInstanceDTO} objects
     */
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ChecklistInstanceDTO>>> today() {
        return ResponseEntity.ok(ApiResponse.ok(service.getTodayChecklist()));
    }

    /**
     * Marks a single checklist item as completed or updates its completion
     * state.
     *
     * @param id      identifier of the checklist item to update
     * @param request completion details such as status and optional comment
     * @return a 200 OK response with a success message
     */
    @PatchMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<Void>> complete(
            @PathVariable Long id,
            @Valid @RequestBody CompleteChecklistItemRequest request) {
        service.completeItem(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Checklist item updated", null));
    }

    /**
     * Deletes a checklist template. Existing checklist instances created from
     * the template are not affected.
     *
     * @param id identifier of the template to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        service.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggles the active state of a checklist template, effectively enabling
     * or disabling its use for generating new instances.
     *
     * @param id identifier of the template to toggle
     * @return a 200 OK response with a success message
     */
    @PatchMapping("/templates/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable Long id) {
        service.toggleTemplate(id);
        return ResponseEntity.ok(ApiResponse.ok("Checklist template toggled", null));
    }
}
