package edu.ntnu.idi.idatt2105.backend.core.document.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores the binary content of a document.
 *
 * <p>
 * This entity separates large file data from the main {@link Document}
 * entity to optimize performance and avoid loading heavy binary data
 * when only metadata is needed.
 * </p>
 */
@Entity
@Table(name = "document_blob_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentBlobContent {
    @Id
    @Column(name = "document_id")
    private Long documentId;

    @Lob
    @Column(nullable = false)
    private byte[] content;
}
