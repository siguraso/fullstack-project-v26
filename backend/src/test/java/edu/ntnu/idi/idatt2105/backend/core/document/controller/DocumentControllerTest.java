package edu.ntnu.idi.idatt2105.backend.core.document.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.document.dto.DocumentDTO;
import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import edu.ntnu.idi.idatt2105.backend.core.document.service.DocumentDownloadResult;
import edu.ntnu.idi.idatt2105.backend.core.document.service.DocumentService;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService service;

    @InjectMocks
    private DocumentController controller;

    @Test
    void searchReturns200Ok() {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(11L);
        dto.setArea(DocumentArea.GENERAL);
        dto.setTitle("Opening checklist");

        when(service.searchDocuments(DocumentArea.GENERAL, "opening", List.of("kitchen")))
                .thenReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<DocumentDTO>>> response =
                controller.searchDocuments(DocumentArea.GENERAL, "opening", List.of("kitchen"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(service).searchDocuments(DocumentArea.GENERAL, "opening", List.of("kitchen"));
    }

    @Test
    void createReturns201Created() {
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setTitle("Food guide");
        request.setArea(DocumentArea.IK_FOOD);

        DocumentDTO dto = new DocumentDTO();
        dto.setId(21L);

        when(service.createDocument(request)).thenReturn(dto);

        ResponseEntity<ApiResponse<DocumentDTO>> response = controller.createDocument(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(21L, response.getBody().getData().getId());
        verify(service).createDocument(request);
    }

    @Test
    void downloadReturnsAttachmentResponse() {
        byte[] content = "hello".getBytes();
        when(service.downloadDocument(55L))
                .thenReturn(new DocumentDownloadResult("guide.pdf", "application/pdf", content));

        ResponseEntity<byte[]> response = controller.downloadDocument(55L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("guide.pdf"));
        assertArrayEquals(content, response.getBody());
        verify(service).downloadDocument(55L);
    }

    @Test
    void deleteReturns204NoContent() {
        ResponseEntity<Void> response = controller.deleteDocument(89L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).deleteDocument(89L);
    }
}
