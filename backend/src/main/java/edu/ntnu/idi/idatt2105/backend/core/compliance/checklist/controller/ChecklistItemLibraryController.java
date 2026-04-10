package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistItemLibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing items in the checklist item library.
 * <p>
 * The checklist item library contains reusable checklist item definitions that
 * can be referenced by checklist templates across the compliance module.
 */
@RestController
@RequestMapping("/api/checklist-library")
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemLibraryController {

    private final ChecklistItemLibraryService service;

    /**
     * Creates a new checklist item definition in the library.
     *
     * @param request DTO describing the checklist item to create
     * @return a 201 Created response containing the stored
     *         {@link ChecklistItemLibraryDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistItemLibraryDTO>> create(@Valid @RequestBody ChecklistItemLibraryDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(request)));
    }

    /**
     * Retrieves all checklist item definitions from the library for the
     * current tenant.
     *
     * @return a list of {@link ChecklistItemLibraryDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ChecklistItemLibraryDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    /**
     * Deletes a checklist item definition from the library.
     *
     * @param id identifier of the checklist item to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing checklist item definition in the library.
     *
     * @param id      identifier of the item to update
     * @param request DTO containing the updated item details
     * @return the updated {@link ChecklistItemLibraryDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ChecklistItemLibraryDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ChecklistItemLibraryDTO request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    /**
     * Checks whether the given checklist item is currently referenced by any
     * checklist templates or instances.
     *
     * @param id identifier of the checklist item to check
     * @return {@code true} if the item is in use, {@code false} otherwise
     */
    @GetMapping("/{id}/in-use")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<Boolean> isInUse(@PathVariable Long id) {
        return ResponseEntity.ok(service.isItemInUse(id));
    }
}