package edu.ntnu.idi.idatt2105.backend.core.document.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used to create a new document.
 *
 * <p>
 * Contains metadata such as title, description, area, and tags,
 * along with the uploaded file content.
 * </p>
 */
@Data
public class DocumentCreateRequest {

    /**
     * Title of the document.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be 255 characters or fewer")
    private String title;

    /**
     * Optional description providing additional context for the document.
     */
    @Size(max = 2000, message = "Description must be 2000 characters or fewer")
    private String description;

    /**
     * Functional area the document belongs to
     * (e.g., IK_FOOD, IK_ALCOHOL).
     */
    @NotNull(message = "Area is required")
    private DocumentArea area;

    /**
     * Optional list of tags used for categorization and search.
     */
    private List<String> tags = new ArrayList<>();

    /**
     * The file uploaded for this document.
     */
    private MultipartFile file;
}