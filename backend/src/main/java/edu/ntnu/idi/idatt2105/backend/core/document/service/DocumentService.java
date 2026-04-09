package edu.ntnu.idi.idatt2105.backend.core.document.service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentDTO;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.document.entity.Document;
import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import edu.ntnu.idi.idatt2105.backend.core.document.mapper.DocumentMapper;
import edu.ntnu.idi.idatt2105.backend.core.document.repository.DocumentRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class DocumentService {
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "image/png",
            "image/jpeg",
            "image/webp",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf",
            "txt",
            "png",
            "jpg",
            "jpeg",
            "webp",
            "doc",
            "docx",
            "xls",
            "xlsx");

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    private static final List<String> EMPTY_TAG_SENTINEL = List.of("__no_tag_match__");

    private final DocumentRepository documentRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final DocumentMapper mapper;
    private final DocumentContentStore contentStore;
    private final long maxFileSizeBytes;

    public DocumentService(
            DocumentRepository documentRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            DocumentMapper mapper,
            DocumentContentStore contentStore,
            @Value("${app.documents.max-file-size-bytes:10485760}") long maxFileSizeBytes) {
        this.documentRepository = documentRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.contentStore = contentStore;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> searchDocuments(DocumentArea area, String query, List<String> tags) {
        Long tenantId = TenantContext.getCurrentOrg();
        String normalizedQuery = normalizeQuery(query);
        List<String> normalizedTags = normalizeTags(tags).stream().toList();
        boolean tagsEmpty = normalizedTags.isEmpty();

        return documentRepository.searchDocuments(
                        tenantId,
                        area,
                        normalizedQuery,
                        tagsEmpty,
                        tagsEmpty ? EMPTY_TAG_SENTINEL : normalizedTags)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentDTO getDocument(Long id) {
        return mapper.toDTO(getAuthorizedDocument(id));
    }

    public DocumentDTO createDocument(DocumentCreateRequest request) {
        validateCreateRequest(request);

        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        User uploadedBy = resolveAuthenticatedUser(tenantId);
        MultipartFile file = request.getFile();
        byte[] content = readBytes(file);

        Document document = new Document();
        document.setTenant(tenant);
        document.setUploadedBy(uploadedBy);
        applyMetadata(document, request.getTitle(), request.getDescription(), request.getArea(), request.getTags());
        document.setOriginalFilename(resolveFilename(file));
        document.setMimeType(resolveMimeType(file));
        document.setSizeBytes(file.getSize());

        Document saved = documentRepository.save(document);
        contentStore.save(saved.getId(), content);
        log.info("Created document id={} tenantId={} area={}", saved.getId(), tenantId, saved.getArea());
        return mapper.toDTO(saved);
    }

    public DocumentDTO updateDocument(Long id, DocumentUpdateRequest request) {
        validateUpdateRequest(request);

        Document document = getAuthorizedDocument(id);
        applyMetadata(document, request.getTitle(), request.getDescription(), request.getArea(), request.getTags());

        MultipartFile file = request.getFile();
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            document.setOriginalFilename(resolveFilename(file));
            document.setMimeType(resolveMimeType(file));
            document.setSizeBytes(file.getSize());
            contentStore.replace(document.getId(), readBytes(file));
        }

        Document saved = documentRepository.save(document);
        log.info("Updated document id={} tenantId={} area={} replacedFile={}",
                saved.getId(),
                TenantContext.getCurrentOrg(),
                saved.getArea(),
                file != null && !file.isEmpty());
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public DocumentDownloadResult downloadDocument(Long id) {
        Document document = getAuthorizedDocument(id);
        byte[] content = contentStore.read(id);
        return new DocumentDownloadResult(document.getOriginalFilename(), document.getMimeType(), content);
    }

    public void deleteDocument(Long id) {
        Document document = getAuthorizedDocument(id);
        contentStore.delete(id);
        documentRepository.delete(document);
        log.info("Deleted document id={} tenantId={}", id, TenantContext.getCurrentOrg());
    }

    private void validateCreateRequest(DocumentCreateRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new ValidationException("A file is required");
        }
        validateSharedRequest(request.getTitle(), request.getDescription(), request.getArea(), request.getTags());
        validateFile(request.getFile());
    }

    private void validateUpdateRequest(DocumentUpdateRequest request) {
        validateSharedRequest(request.getTitle(), request.getDescription(), request.getArea(), request.getTags());
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            validateFile(request.getFile());
        }
    }

    private void validateSharedRequest(String title, String description, DocumentArea area, List<String> tags) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title is required");
        }
        if (title.trim().length() > 255) {
            throw new ValidationException("Title must be 255 characters or fewer");
        }
        if (description != null && description.length() > 2000) {
            throw new ValidationException("Description must be 2000 characters or fewer");
        }
        if (area == null) {
            throw new ValidationException("Area is required");
        }
        for (String tag : normalizeTags(tags)) {
            if (tag.length() > 80) {
                throw new ValidationException("Tags must be 80 characters or fewer");
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("A file is required");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new ValidationException("File exceeds the 10 MB upload limit");
        }

        String filename = resolveFilename(file);
        String mimeType = resolveMimeType(file);
        String extension = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)
                : "";

        if (!ALLOWED_MIME_TYPES.contains(mimeType) && !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ValidationException("Unsupported file type");
        }
    }

    private void applyMetadata(Document document, String title, String description, DocumentArea area, List<String> tags) {
        document.setTitle(title.trim());
        document.setDescription(description == null || description.isBlank() ? null : description.trim());
        document.setArea(area);
        document.setTags(normalizeTags(tags));
    }

    private LinkedHashSet<String> normalizeTags(List<String> tags) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (tags == null) {
            return normalized;
        }

        for (String tag : tags) {
            if (tag == null) {
                continue;
            }

            String normalizedTag = tag.trim().toLowerCase(Locale.ROOT);
            if (!normalizedTag.isEmpty()) {
                normalized.add(normalizedTag);
            }
        }

        return normalized;
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        return "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
    }

    private String resolveFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new ValidationException("File must have a filename");
        }
        return filename.trim();
    }

    private String resolveMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType == null || mimeType.isBlank() ? DEFAULT_MIME_TYPE : mimeType.trim().toLowerCase(Locale.ROOT);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new ValidationException("Failed to read uploaded file");
        }
    }

    private Document getAuthorizedDocument(Long id) {
        Long tenantId = TenantContext.getCurrentOrg();
        return documentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }

    private User resolveAuthenticatedUser(Long tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Authenticated user is required");
        }

        String email = authentication.getPrincipal() instanceof String string
                ? string
                : authentication.getPrincipal().toString();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));

        if (user.getTenant() == null || user.getTenant().getId() == null || !user.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Authenticated user does not belong to current organization");
        }

        return user;
    }
}
