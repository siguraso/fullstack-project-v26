package edu.ntnu.idi.idatt2105.backend.core.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.document.entity.DocumentBlobContent;

public interface DocumentBlobContentRepository extends JpaRepository<DocumentBlobContent, Long> {
}
