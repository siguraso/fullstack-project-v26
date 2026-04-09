package edu.ntnu.idi.idatt2105.backend.core.document.service;

public interface DocumentContentStore {
    void save(Long documentId, byte[] content);

    byte[] read(Long documentId);

    void replace(Long documentId, byte[] content);

    void delete(Long documentId);
}
