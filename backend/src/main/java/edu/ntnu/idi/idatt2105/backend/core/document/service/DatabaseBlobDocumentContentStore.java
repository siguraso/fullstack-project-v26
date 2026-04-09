package edu.ntnu.idi.idatt2105.backend.core.document.service;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.document.entity.DocumentBlobContent;
import edu.ntnu.idi.idatt2105.backend.core.document.repository.DocumentBlobContentRepository;

@Component
public class DatabaseBlobDocumentContentStore implements DocumentContentStore {
    private final DocumentBlobContentRepository repository;

    public DatabaseBlobDocumentContentStore(DocumentBlobContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Long documentId, byte[] content) {
        repository.save(new DocumentBlobContent(documentId, content));
    }

    @Override
    public byte[] read(Long documentId) {
        return repository.findById(documentId)
                .map(DocumentBlobContent::getContent)
                .orElseThrow(() -> new ResourceNotFoundException("Document content not found"));
    }

    @Override
    public void replace(Long documentId, byte[] content) {
        repository.save(new DocumentBlobContent(documentId, content));
    }

    @Override
    public void delete(Long documentId) {
        repository.deleteById(documentId);
    }
}
