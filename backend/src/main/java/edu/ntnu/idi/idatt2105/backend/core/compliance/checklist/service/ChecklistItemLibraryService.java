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
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing the reusable checklist item library for the current
 * tenant.
 * <p>
 * Library items act as reusable definitions that can be referenced by
 * checklist templates. Items in use by at least one template cannot be deleted.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemLibraryService {

    private final ChecklistItemLibraryRepository repo;
    private final TenantRepository tenantRepo;
    private final ChecklistItemLibraryMapper mapper;
    private final ChecklistItemTemplateRepository itemTemplateRepo;

    /**
     * Creates a new checklist item definition in the library for the current
     * tenant.
     *
     * @param request DTO with title, description, category and priority
     * @return the persisted {@link ChecklistItemLibraryDTO}
     */
    public ChecklistItemLibraryDTO create(ChecklistItemLibraryDTO request) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Creating checklist library item for tenantId={} category={}", tenantId, request.getCategory());
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        ChecklistItemLibrary item = new ChecklistItemLibrary();
        item.setTenant(tenant);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setPriority(request.getPriority());

        ChecklistItemLibrary savedItem = repo.save(item);
        log.info("Created checklist library item id={} for tenantId={}", savedItem.getId(), tenantId);
        return mapper.toDto(savedItem);
    }

    /**
     * Returns all checklist item definitions in the library for the current
     * tenant.
     *
     * @return a list of {@link ChecklistItemLibraryDTO} objects
     */
    public List<ChecklistItemLibraryDTO> getAll() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<ChecklistItemLibraryDTO> items = repo.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDto)
                .toList();
        log.debug("Fetched {} checklist library items for tenantId={}", items.size(), tenantId);
        return items;
    }

    /**
     * Updates an existing checklist item definition in the library.
     *
     * @param id      identifier of the item to update
     * @param request DTO with updated title, description, category and priority
     * @return the updated {@link ChecklistItemLibraryDTO}
     */
    public ChecklistItemLibraryDTO update(Long id, ChecklistItemLibraryDTO request) {
        log.info("Updating checklist library item id={}", id);
        ChecklistItemLibrary item = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        ensureItemOwnedByCurrentTenant(item);

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setPriority(request.getPriority());

        ChecklistItemLibrary savedItem = repo.save(item);
        log.info("Updated checklist library item id={}", savedItem.getId());
        return mapper.toDto(savedItem);
    }

    /**
     * Deletes a checklist item definition from the library.
     *
     * @param id identifier of the item to delete
     */
    public void delete(Long id) {
        log.info("Deleting checklist library item id={}", id);
        ChecklistItemLibrary item = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        ensureItemOwnedByCurrentTenant(item);

        repo.deleteById(id);
        log.info("Deleted checklist library item id={}", id);
    }

    /**
     * Checks whether a library item is currently referenced by any checklist
     * template.
     *
     * @param id identifier of the library item to check
     * @return {@code true} if the item is in use by at least one template
     */
    public boolean isItemInUse(Long id) {
        boolean inUse = itemTemplateRepo.existsByLibraryItem_Id(id);
        log.debug("Checklist library item id={} inUse={}", id, inUse);
        return inUse;
    }

    private void ensureItemOwnedByCurrentTenant(ChecklistItemLibrary item) {
        Long tenantId = TenantContext.getCurrentOrg();
        if (item.getTenant() == null || item.getTenant().getId() == null || !item.getTenant().getId().equals(tenantId)) {
            log.warn("Access denied to checklist library item id={} for tenantId={}", item.getId(), tenantId);
            throw new UnauthorizedException("Checklist library item does not belong to current organization");
        }
    }
}