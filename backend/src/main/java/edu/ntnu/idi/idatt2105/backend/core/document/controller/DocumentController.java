package edu.ntnu.idi.idatt2105.backend.core.document.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentDTO;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import edu.ntnu.idi.idatt2105.backend.core.document.service.DocumentDownloadResult;
import edu.ntnu.idi.idatt2105.backend.core.document.service.DocumentService;
import jakarta.validation.Valid;

/**
 * REST controller for managing documents in the document library.
 * <p>
 * Provides endpoints for searching, retrieving, uploading, updating and
 * deleting tenant-scoped documents, as well as downloading their binary
 * content.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    /**
     * Searches documents for the current tenant with optional filters.
     *
     * @param area  optional document area to filter by
     * @param query optional free-text search term matched against title
     * @param tags  optional list of tags; all provided tags must match
     * @return a list of matching {@link DocumentDTO} objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<DocumentDTO>>> searchDocuments(
            @RequestParam(required = false) DocumentArea area,
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "tags", required = false) List<String> tags) {
        return ResponseEntity.ok(ApiResponse.ok(service.searchDocuments(area, query, tags)));
    }

    /**
     * Retrieves a single document by its identifier.
     *
     * @param id the document identifier
     * @return the {@link DocumentDTO} for the requested document
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<DocumentDTO>> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getDocument(id)));
    }

    /**
     * Downloads the binary content of a document as an attachment. The
     * {@code Content-Type} header is resolved from the stored MIME type.
     *
     * @param id the document identifier
     * @return the raw file bytes with appropriate content headers
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DocumentDownloadResult result = service.downloadDocument(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(result.filename())
                .orElseGet(() -> MediaType.parseMediaType(result.mimeType()));

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(result.filename(), StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(result.content());
    }

    /**
     * Uploads a new document with its associated metadata.
     *
     * @param request multipart form containing the file and metadata fields
     * @return a 201 Created response with the persisted {@link DocumentDTO}
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DocumentDTO>> createDocument(@Valid @ModelAttribute DocumentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.createDocument(request)));
    }

    /**
     * Updates the metadata or file content of an existing document.
     *
     * @param id      identifier of the document to update
     * @param request multipart form with updated metadata and optional new file
     * @return the updated {@link DocumentDTO}
     */
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DocumentDTO>> updateDocument(
            @PathVariable Long id,
            @Valid @ModelAttribute DocumentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateDocument(id, request)));
    }

    /**
     * Permanently deletes a document and its stored file content.
     *
     * @param id identifier of the document to delete
     * @return a 204 No Content response on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        service.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
