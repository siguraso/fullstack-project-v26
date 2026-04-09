package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistItemLibraryMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemLibraryRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;

@ExtendWith(MockitoExtension.class)
class ChecklistItemLibraryServiceTest {

    @Mock
    private ChecklistItemLibraryRepository repo;

    @Mock
    private TenantRepository tenantRepo;

    @Mock
    private ChecklistItemLibraryMapper mapper;

    @Mock
    private ChecklistItemTemplateRepository itemTemplateRepo;

    @InjectMocks
    private ChecklistItemLibraryService service;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentOrg(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void updateRejectsForeignTenantItem() {
        ChecklistItemLibrary item = libraryItem(10L, 2L);
        when(repo.findById(10L)).thenReturn(Optional.of(item));

        ChecklistItemLibraryDTO request = new ChecklistItemLibraryDTO();
        request.setTitle("Updated");
        request.setDescription("Updated description");
        request.setCategory("Food");
        request.setPriority("High");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.update(10L, request));

        assertEquals("Checklist library item does not belong to current organization", exception.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    void deleteRejectsForeignTenantItem() {
        ChecklistItemLibrary item = libraryItem(11L, 2L);
        when(repo.findById(11L)).thenReturn(Optional.of(item));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.delete(11L));

        assertEquals("Checklist library item does not belong to current organization", exception.getMessage());
        verify(repo, never()).deleteById(anyLong());
    }

    private ChecklistItemLibrary libraryItem(Long id, Long tenantId) {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);

        ChecklistItemLibrary item = new ChecklistItemLibrary();
        item.setId(id);
        item.setTenant(tenant);
        item.setTitle("Title");
        item.setDescription("Description");
        item.setCategory("Category");
        item.setPriority("Priority");
        return item;
    }
}

