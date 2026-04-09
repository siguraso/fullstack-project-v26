package edu.ntnu.idi.idatt2105.backend.core.document.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentUpdateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be 255 characters or fewer")
    private String title;

    @Size(max = 2000, message = "Description must be 2000 characters or fewer")
    private String description;

    @NotNull(message = "Area is required")
    private DocumentArea area;

    private List<String> tags = new ArrayList<>();

    private MultipartFile file;
}
