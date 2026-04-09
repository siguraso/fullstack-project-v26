package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistItemLibraryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistItemLibraryMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemLibraryRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChecklistItemLibraryService {

    private final ChecklistItemLibraryRepository repo;
    private final TenantRepository tenantRepo;
    private final ChecklistItemLibraryMapper mapper;
    private final ChecklistItemTemplateRepository itemTemplateRepo;

    public ChecklistItemLibraryDTO create(ChecklistItemLibraryDTO request) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        ChecklistItemLibrary item = new ChecklistItemLibrary();
        item.setTenant(tenant);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setPriority(request.getPriority());

        return mapper.toDto(repo.save(item));
    }

    public List<ChecklistItemLibraryDTO> getAll() {
        Long tenantId = TenantContext.getCurrentOrg();
        return repo.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public ChecklistItemLibraryDTO update(Long id, ChecklistItemLibraryDTO request) {
        ChecklistItemLibrary item = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        ensureItemOwnedByCurrentTenant(item);

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setPriority(request.getPriority());

        return mapper.toDto(repo.save(item));
    }

    public void delete(Long id) {
        ChecklistItemLibrary item = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        ensureItemOwnedByCurrentTenant(item);

        repo.deleteById(id);
    }

    public boolean isItemInUse(Long id) {
        return itemTemplateRepo.existsByLibraryItem_Id(id);
    }

    private void ensureItemOwnedByCurrentTenant(ChecklistItemLibrary item) {
        Long tenantId = TenantContext.getCurrentOrg();
        if (item.getTenant() == null || item.getTenant().getId() == null || !item.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Checklist library item does not belong to current organization");
        }
    }
}