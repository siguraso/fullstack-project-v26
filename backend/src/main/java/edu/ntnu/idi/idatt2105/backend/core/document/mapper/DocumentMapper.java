package edu.ntnu.idi.idatt2105.backend.core.document.mapper;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentDTO;
import edu.ntnu.idi.idatt2105.backend.core.document.entity.Document;

@Component
public class DocumentMapper {
    public DocumentDTO toDTO(Document entity) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(entity.getId());
        dto.setArea(entity.getArea());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setOriginalFilename(entity.getOriginalFilename());
        dto.setMimeType(entity.getMimeType());
        dto.setSizeBytes(entity.getSizeBytes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getUploadedBy() != null) {
            dto.setUploadedById(entity.getUploadedBy().getId());
            dto.setUploadedByRole(entity.getUploadedBy().getRole());
            String fullName = ((entity.getUploadedBy().getFirstName() != null ? entity.getUploadedBy().getFirstName().trim() : "")
                    + " "
                    + (entity.getUploadedBy().getLastName() != null ? entity.getUploadedBy().getLastName().trim() : "")).trim();
            dto.setUploadedByName(fullName.isBlank() ? null : fullName);
        }

        dto.setTags(entity.getTags().stream().sorted(Comparator.naturalOrder()).toList());
        return dto;
    }
}
