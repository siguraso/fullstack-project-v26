package edu.ntnu.idi.idatt2105.backend.core.document.service;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.document.entity.DocumentBlobContent;
import edu.ntnu.idi.idatt2105.backend.core.document.repository.DocumentBlobContentRepository;

/**
 * Database-backed implementation of {@link DocumentContentStore} that
 * persists binary document content as blobs in the {@code document_blob_content}
 * table via {@link DocumentBlobContentRepository}.
 */
@Component
public class DatabaseBlobDocumentContentStore implements DocumentContentStore {
    private final DocumentBlobContentRepository repository;

    public DatabaseBlobDocumentContentStore(DocumentBlobContentRepository repository) {
        this.repository = repository;
    }

    /**
     * Persists the binary content for the given document identifier.
     *
     * @param documentId the document identifier used as the primary key
     * @param content    the raw file bytes to store
     */
    @Override
    public void save(Long documentId, byte[] content) {
        repository.save(new DocumentBlobContent(documentId, content));
    }

    /**
     * Reads the binary content for the given document identifier.
     *
     * @param documentId the document identifier to look up
     * @return the raw file bytes
     */
    @Override
    public byte[] read(Long documentId) {
        return repository.findById(documentId)
                .map(DocumentBlobContent::getContent)
                .orElseThrow(() -> new ResourceNotFoundException("Document content not found"));
    }

    /**
     * Replaces the binary content for the given document identifier.
     *
     * @param documentId the document identifier whose content is replaced
     * @param content    the new raw file bytes
     */
    @Override
    public void replace(Long documentId, byte[] content) {
        repository.save(new DocumentBlobContent(documentId, content));
    }

    /**
     * Deletes the binary content for the given document identifier.
     *
     * @param documentId the document identifier whose content is removed
     */
    @Override
    public void delete(Long documentId) {
        repository.deleteById(documentId);
    }
}
