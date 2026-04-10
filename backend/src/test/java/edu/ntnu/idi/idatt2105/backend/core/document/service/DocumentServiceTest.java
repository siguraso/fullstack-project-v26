package edu.ntnu.idi.idatt2105.backend.core.document.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
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
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentContentStore contentStore;

    private DocumentService service;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentOrg(1L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("manager@example.com", "password"));
        service = new DocumentService(
                documentRepository,
                tenantRepository,
                userRepository,
                new DocumentMapper(),
                contentStore,
                10 * 1024 * 1024);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void createDocumentPersistsMetadataAndBlob() {
        Tenant tenant = tenant(1L);
        User user = user(3L, tenant);
        Document saved = document(44L, tenant, user);

        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setTitle("Kitchen guide");
        request.setDescription("Use this during inspection");
        request.setArea(DocumentArea.IK_FOOD);
        request.setTags(List.of("Opening", "Food"));
        request.setFile(new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                "pdf-content".getBytes()));

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(user));
        when(documentRepository.save(any(Document.class))).thenReturn(saved);

        DocumentDTO result = service.createDocument(request);

        assertNotNull(result);
        assertEquals(44L, result.getId());
        verify(documentRepository).save(any(Document.class));
        verify(contentStore).save(eq(44L), argThat(bytes -> java.util.Arrays.equals(bytes, "pdf-content".getBytes())));
    }

    @Test
    void searchDocumentsNormalizesTagsAndMapsResults() {
        Tenant tenant = tenant(1L);
        User user = user(4L, tenant);
        Document document = document(8L, tenant, user);
        document.setTags(new java.util.LinkedHashSet<>(List.of("cold-room", "manual")));

        when(documentRepository.searchDocuments(
                1L,
                DocumentArea.GENERAL,
                "%manual%",
                false,
                List.of("cold-room", "manual")))
                .thenReturn(List.of(document));

        List<DocumentDTO> results = service.searchDocuments(
                DocumentArea.GENERAL,
                "Manual",
                List.of("Cold-Room", "manual"));

        assertEquals(1, results.size());
        assertEquals("Safety manual", results.getFirst().getTitle());
        assertEquals(List.of("cold-room", "manual"), results.getFirst().getTags());
    }

    @Test
    void updateDocumentReplacesFileWhenProvided() {
        Tenant tenant = tenant(1L);
        User user = user(3L, tenant);
        Document existing = document(11L, tenant, user);

        DocumentUpdateRequest request = new DocumentUpdateRequest();
        request.setTitle("Updated safety manual");
        request.setDescription("Latest version");
        request.setArea(DocumentArea.IK_ALCOHOL);
        request.setTags(List.of("bar", "policy"));
        request.setFile(new MockMultipartFile(
                "file",
                "manual.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "updated".getBytes()));

        when(documentRepository.findByIdAndTenantId(11L, 1L)).thenReturn(Optional.of(existing));
        when(documentRepository.save(existing)).thenReturn(existing);

        DocumentDTO result = service.updateDocument(11L, request);

        assertEquals("Updated safety manual", result.getTitle());
        assertEquals(DocumentArea.IK_ALCOHOL, result.getArea());
        verify(contentStore).replace(eq(11L), argThat(bytes -> java.util.Arrays.equals(bytes, "updated".getBytes())));
    }

    @Test
    void downloadDocumentRejectsForeignTenantAccess() {
        when(documentRepository.findByIdAndTenantId(99L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.downloadDocument(99L));

        assertEquals("Document not found", exception.getMessage());
        verify(contentStore, never()).read(any());
    }

    @Test
    void createDocumentRejectsUnsupportedFileType() {
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setTitle("Executable");
        request.setArea(DocumentArea.GENERAL);
        request.setTags(List.of("unsafe"));
        request.setFile(new MockMultipartFile("file", "run.exe", "application/octet-stream", "x".getBytes()));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createDocument(request));

        assertEquals("Unsupported file type", exception.getMessage());
        verify(documentRepository, never()).save(any());
    }

    private Tenant tenant(Long id) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        tenant.setName("Tenant");
        tenant.setOrgNumber("123456789");
        tenant.setCountry("Norway");
        return tenant;
    }

    private User user(Long id, Tenant tenant) {
        User user = new User();
        user.setId(id);
        user.setTenant(tenant);
        user.setFirstName("Taylor");
        user.setLastName("Manager");
        user.setEmail("manager@example.com");
        user.setRole(UserRole.MANAGER);
        return user;
    }

    private Document document(Long id, Tenant tenant, User user) {
        Document document = new Document();
        document.setId(id);
        document.setTenant(tenant);
        document.setUploadedBy(user);
        document.setArea(DocumentArea.GENERAL);
        document.setTitle("Safety manual");
        document.setDescription("Manual");
        document.setOriginalFilename("manual.pdf");
        document.setMimeType("application/pdf");
        document.setSizeBytes(1024L);
        document.setTags(new java.util.LinkedHashSet<>(List.of("manual")));
        return document;
    }
}
