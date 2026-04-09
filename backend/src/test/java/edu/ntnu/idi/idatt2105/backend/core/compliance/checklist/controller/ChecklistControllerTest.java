package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistService;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;

@ExtendWith(MockitoExtension.class)
class ChecklistControllerTest {

    @Mock
    private ChecklistService checklistService;

    @InjectMocks
    private ChecklistController checklistController;

    @Test
    void createTemplateReturns201Created() {
        CreateChecklistTemplateRequest request = new CreateChecklistTemplateRequest();
        request.setName("Daily kitchen");
        request.setModule(ComplianceModule.IK_FOOD);
        request.setFrequency(ChecklistFrequency.DAILY);
        request.setItemIds(List.of(1L));

        ChecklistTemplateDTO dto = new ChecklistTemplateDTO();
        dto.setId(10L);
        dto.setName("Daily kitchen");

        when(checklistService.createTemplate(request)).thenReturn(dto);

        ResponseEntity<ApiResponse<ChecklistTemplateDTO>> response = checklistController.createTemplate(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(10L, response.getBody().getData().getId());
        verify(checklistService).createTemplate(request);
    }

    @Test
    void generateReturns201Created() {
        ChecklistInstanceDTO dto = new ChecklistInstanceDTO();
        dto.setId(20L);

        when(checklistService.generateInstance(20L)).thenReturn(dto);

        ResponseEntity<ApiResponse<ChecklistInstanceDTO>> response = checklistController.generate(20L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(20L, response.getBody().getData().getId());
        verify(checklistService).generateInstance(20L);
    }

    @Test
    void deleteTemplateReturns204NoContent() {
        ResponseEntity<Void> response = checklistController.deleteTemplate(30L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(checklistService).deleteTemplate(30L);
    }
}

