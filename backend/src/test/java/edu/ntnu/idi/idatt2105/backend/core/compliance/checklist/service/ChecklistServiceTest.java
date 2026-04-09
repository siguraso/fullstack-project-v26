package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistFrequency;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistTemplateMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemLibraryRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;

@ExtendWith(MockitoExtension.class)
class ChecklistServiceTest {

    @Mock
    private ChecklistTemplateRepository templateRepo;

    @Mock
    private ChecklistItemTemplateRepository itemTemplateRepo;

    @Mock
    private ChecklistInstanceRepository instanceRepo;

    @Mock
    private ChecklistItemInstanceRepository itemInstanceRepo;

    @Mock
    private TenantRepository tenantRepo;

    @Mock
    private ChecklistInstanceMapper instanceMapper;

    @Mock
    private ChecklistTemplateMapper templateMapper;

    @Mock
    private ChecklistItemLibraryRepository libraryRepo;

    @InjectMocks
    private ChecklistService service;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentOrg(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createTemplateRejectsForeignLibraryItems() {
        when(tenantRepo.findById(1L)).thenReturn(Optional.of(tenant(1L)));
        when(libraryRepo.findById(101L)).thenReturn(Optional.of(libraryItem(101L, 2L)));

        CreateChecklistTemplateRequest request = new CreateChecklistTemplateRequest();
        request.setName("Kitchen checklist");
        request.setModule(ComplianceModule.IK_FOOD);
        request.setFrequency(ChecklistFrequency.DAILY);
        request.setItemIds(List.of(101L));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.createTemplate(request));

        assertEquals("Library item does not belong to current organization", exception.getMessage());
        verify(templateRepo, never()).save(any());
    }

    @Test
    void generateInstanceRejectsForeignTemplate() {
        ChecklistTemplate template = template(5L, 2L);
        when(templateRepo.findById(5L)).thenReturn(Optional.of(template));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.generateInstance(5L));

        assertEquals("Template does not belong to current organization", exception.getMessage());
        verify(instanceRepo, never()).save(any());
    }

    @Test
    void updateTemplateRejectsForeignTemplate() {
        ChecklistTemplate template = template(6L, 2L);
        when(templateRepo.findById(6L)).thenReturn(Optional.of(template));

        CreateChecklistTemplateRequest request = new CreateChecklistTemplateRequest();
        request.setName("Updated");
        request.setModule(ComplianceModule.IK_FOOD);
        request.setFrequency(ChecklistFrequency.WEEKLY);
        request.setItemIds(List.of(101L));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.updateTemplate(6L, request));

        assertEquals("Template does not belong to current organization", exception.getMessage());
        verify(templateRepo, never()).save(any());
    }

    @Test
    void completeItemRejectsForeignChecklistItem() {
        ChecklistItemInstance item = checklistItemInstance(9L, 2L);
        when(itemInstanceRepo.findById(9L)).thenReturn(Optional.of(item));

        CompleteChecklistItemRequest request = new CompleteChecklistItemRequest();
        request.setCompleted(true);
        request.setComment("Done");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.completeItem(9L, request));

        assertEquals("Checklist item does not belong to current organization", exception.getMessage());
        verify(itemInstanceRepo, never()).save(any());
    }

    @Test
    void deleteTemplateRejectsForeignTemplate() {
        ChecklistTemplate template = template(7L, 2L);
        when(templateRepo.findById(7L)).thenReturn(Optional.of(template));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.deleteTemplate(7L));

        assertEquals("Template does not belong to current organization", exception.getMessage());
        verify(instanceRepo, never()).deleteByTemplate_id(anyLong());
        verify(templateRepo, never()).delete(any());
    }

    @Test
    void toggleTemplateRejectsForeignTemplate() {
        ChecklistTemplate template = template(8L, 2L);
        when(templateRepo.findById(8L)).thenReturn(Optional.of(template));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.toggleTemplate(8L));

        assertEquals("Template does not belong to current organization", exception.getMessage());
        verify(templateRepo, never()).save(any());
    }

    private Tenant tenant(Long id) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        return tenant;
    }

    private ChecklistTemplate template(Long id, Long tenantId) {
        ChecklistTemplate template = new ChecklistTemplate();
        template.setId(id);
        template.setTenant(tenant(tenantId));
        return template;
    }

    private ChecklistItemLibrary libraryItem(Long id, Long tenantId) {
        ChecklistItemLibrary item = new ChecklistItemLibrary();
        item.setId(id);
        item.setTenant(tenant(tenantId));
        item.setTitle("Item");
        item.setDescription("Desc");
        item.setCategory("Category");
        item.setPriority("Priority");
        return item;
    }

    private ChecklistItemInstance checklistItemInstance(Long id, Long tenantId) {
        ChecklistInstance checklist = new ChecklistInstance();
        checklist.setTenant(tenant(tenantId));

        ChecklistItemInstance item = new ChecklistItemInstance();
        item.setId(id);
        item.setChecklist(checklist);
        return item;
    }
}

