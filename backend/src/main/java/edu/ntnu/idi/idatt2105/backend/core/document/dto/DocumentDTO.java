package edu.ntnu.idi.idatt2105.backend.core.document.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import lombok.Data;

/**
 * DTO representing a document in the system.
 *
 * <p>
 * Used to transfer document metadata to the frontend, including file
 * information, ownership details, and categorization.
 * </p>
 */
@Data
public class DocumentDTO {
    private Long id;
    private DocumentArea area;
    private String title;
    private String description;
    private String originalFilename;
    private String mimeType;
    private Long sizeBytes;
    private Long uploadedById;
    private String uploadedByName;
    private UserRole uploadedByRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags = new ArrayList<>();
}
