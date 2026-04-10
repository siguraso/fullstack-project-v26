package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistTemplateDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistItemTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistTemplateMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemLibraryRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistService {

    private final ChecklistTemplateRepository templateRepo;
    private final ChecklistItemTemplateRepository itemTemplateRepo;
    private final ChecklistInstanceRepository instanceRepo;
    private final ChecklistItemInstanceRepository itemInstanceRepo;
    private final TenantRepository tenantRepo;
    private final ChecklistInstanceMapper instanceMapper;
    private final ChecklistTemplateMapper templateMapper;
    private final ChecklistItemLibraryRepository libraryRepo;

    public ChecklistTemplateDTO createTemplate(CreateChecklistTemplateRequest request) {

        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Creating checklist template for tenantId={} frequency={} module={}", tenantId, request.getFrequency(),
                request.getModule());
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        List<ChecklistItemLibrary> libraryItems = new java.util.ArrayList<>();
        for (Long itemId : request.getItemIds()) {
            ChecklistItemLibrary libraryItem = libraryRepo.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Library item not found"));
            ensureLibraryItemOwnedByTenant(libraryItem, tenantId);
            libraryItems.add(libraryItem);
        }

        ChecklistTemplate template = new ChecklistTemplate();
        template.setTenant(tenant);
        template.setName(request.getName());
        template.setFrequency(request.getFrequency());
        template.setModule(request.getModule());

        ChecklistTemplate savedTemplate = templateRepo.save(template);

        int order = 0;

        for (ChecklistItemLibrary libItem : libraryItems) {

            ChecklistItemTemplate item = new ChecklistItemTemplate();
            item.setChecklistTemplate(savedTemplate);
            item.setLibraryItem(libItem);
            item.setTitle(libItem.getTitle());
            item.setDescription(libItem.getDescription()); // snapshot
            item.setSortOrder(order++);

            itemTemplateRepo.save(item);
        }

        generateInstance(savedTemplate.getId());
        log.info("Created checklist template id={} with {} items for tenantId={}", savedTemplate.getId(),
                libraryItems.size(), tenantId);

        return templateMapper.toDto(savedTemplate);
    }

    public ChecklistInstanceDTO generateInstance(Long templateId) {

        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Generating checklist instance from templateId={} for tenantId={}", templateId, tenantId);
        ChecklistTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        ensureTemplateOwnedByTenant(template, tenantId);

        ChecklistInstance instance = new ChecklistInstance();
        instance.setTemplate(template);
        instance.setDate(LocalDate.now());
        instance.setStatus(ChecklistStatus.PENDING);
        instance.setTenant(template.getTenant());

        ChecklistInstance savedInstance = instanceRepo.save(instance);

        List<ChecklistItemTemplate> templates = itemTemplateRepo.findByChecklistTemplate_Id(templateId);

        for (ChecklistItemTemplate t : templates) {
            ChecklistItemInstance item = new ChecklistItemInstance();
            item.setChecklist(savedInstance);
            item.setTemplateItem(t);
            item.setCompleted(false);
            itemInstanceRepo.save(item);
        }

        log.info("Generated checklist instance id={} from templateId={} with {} items", savedInstance.getId(),
                templateId, templates.size());

        return instanceMapper.toDto(savedInstance);
    }

    public List<ChecklistInstanceDTO> getTodayChecklist() {

        Long tenantId = TenantContext.getCurrentOrg();
        List<ChecklistInstance> instances = instanceRepo.findTodayWithItems(tenantId, LocalDate.now());
        log.debug("Fetched {} checklist instances for today in tenantId={}", instances.size(), tenantId);

        return instances.stream().map(instanceMapper::toDto).toList();
    }

    public void completeItem(Long itemId, CompleteChecklistItemRequest request) {

        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Updating checklist item id={} completed={} for tenantId={}", itemId, request.getCompleted(),
                tenantId);
        ChecklistItemInstance item = itemInstanceRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));
        ensureChecklistItemOwnedByTenant(item, tenantId);

        item.setCompleted(Boolean.TRUE.equals(request.getCompleted()));
        if (Boolean.TRUE.equals(request.getCompleted())) {
            item.setCompletedAt(LocalDate.now());
        } else {
            item.setCompletedAt(null);
        }
        item.setComment(request.getComment());

        itemInstanceRepo.save(item);

        updateCheckListStatus(item.getChecklist().getId());
        log.info("Updated checklist item id={} and recalculated checklist id={}", itemId, item.getChecklist().getId());
    }

    public List<ChecklistTemplateDTO> getTemplates() {

        Long tenantId = TenantContext.getCurrentOrg();
        List<ChecklistTemplate> templates = templateRepo.findByTenant_Id(tenantId);
        log.debug("Fetched {} checklist templates for tenantId={}", templates.size(), tenantId);

        return templates.stream().map(templateMapper::toDto).toList();
    }

    public ChecklistTemplateDTO updateTemplate(Long id, CreateChecklistTemplateRequest request) {

        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Updating checklist template id={} for tenantId={}", id, tenantId);
        ChecklistTemplate template = templateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        ensureTemplateOwnedByTenant(template, tenantId);

        template.setName(request.getName());
        template.setFrequency(request.getFrequency());
        template.setModule(request.getModule());

        List<Long> requestedItemIds = request.getItemIds() != null ? request.getItemIds() : List.of();
        Set<Long> requestedItemIdSet = new HashSet<>(requestedItemIds);

        Map<Long, ChecklistItemTemplate> existingByLibraryId = new HashMap<>();
        for (ChecklistItemTemplate existing : template.getItems()) {
            if (existing.getLibraryItem() != null && existing.getLibraryItem().getId() != null) {
                existingByLibraryId.put(existing.getLibraryItem().getId(), existing);
            }
        }

        template.getItems().removeIf(existing -> {
            Long libraryItemId = existing.getLibraryItem() != null ? existing.getLibraryItem().getId() : null;

            if (libraryItemId == null || requestedItemIdSet.contains(libraryItemId)) {
                return false;
            }

            return !itemInstanceRepo.existsByTemplateItem_Id(existing.getId());
        });

        int order = 0;

        for (Long itemId : requestedItemIds) {
            ChecklistItemLibrary libItem = libraryRepo.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Library item not found"));
            ensureLibraryItemOwnedByTenant(libItem, tenantId);

            ChecklistItemTemplate item = existingByLibraryId.get(itemId);
            if (item == null) {
                item = new ChecklistItemTemplate();
                item.setChecklistTemplate(template);
                item.setLibraryItem(libItem);
                template.getItems().add(item);
            }

            item.setTitle(libItem.getTitle());
            item.setDescription(libItem.getDescription());
            item.setSortOrder(order++);
        }

        ChecklistTemplate savedTemplate = templateRepo.save(template);
        log.info("Updated checklist template id={} with {} requested items", savedTemplate.getId(),
                requestedItemIds.size());
        return templateMapper.toDto(savedTemplate);
    }

    public void deleteTemplate(Long id) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Deleting checklist template id={} for tenantId={}", id, tenantId);
        ChecklistTemplate template = templateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        ensureTemplateOwnedByTenant(template, tenantId);

        instanceRepo.deleteByTemplate_id(id);

        templateRepo.delete(template);
        log.info("Deleted checklist template id={}", id);
    }

    private void updateCheckListStatus(Long checklistId) {
        List<ChecklistItemInstance> items = itemInstanceRepo.findByChecklistId(checklistId);

        long total = items.size();
        long completed = items.stream().filter(ChecklistItemInstance::isCompleted).count();

        ChecklistInstance checklist = instanceRepo.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist not found"));

        if (completed == 0) {
            checklist.setStatus((ChecklistStatus.PENDING));
        } else if (completed < total) {
            checklist.setStatus(ChecklistStatus.IN_PROGRESS);
        } else {
            checklist.setStatus(ChecklistStatus.COMPLETED);
        }

        instanceRepo.save(checklist);
        log.debug("Checklist id={} status recalculated to {} ({}/{})", checklistId, checklist.getStatus(), completed,
                total);
    }

    public void toggleTemplate(Long id) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Toggling checklist template id={} for tenantId={}", id, tenantId);
        ChecklistTemplate template = templateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        ensureTemplateOwnedByTenant(template, tenantId);

        template.setActive(!template.isActive());

        templateRepo.save(template);
        log.info("Toggled checklist template id={} active={}", id, template.isActive());
    }

    private void ensureTemplateOwnedByTenant(ChecklistTemplate template, Long tenantId) {
        if (template.getTenant() == null || template.getTenant().getId() == null
                || !template.getTenant().getId().equals(tenantId)) {
            log.warn("Access denied to checklist template id={} for tenantId={}", template.getId(), tenantId);
            throw new UnauthorizedException("Template does not belong to current organization");
        }
    }

    private void ensureLibraryItemOwnedByTenant(ChecklistItemLibrary libraryItem, Long tenantId) {
        if (libraryItem.getTenant() == null || libraryItem.getTenant().getId() == null
                || !libraryItem.getTenant().getId().equals(tenantId)) {
            log.warn("Access denied to checklist library item id={} for tenantId={}", libraryItem.getId(), tenantId);
            throw new UnauthorizedException("Library item does not belong to current organization");
        }
    }

    private void ensureChecklistItemOwnedByTenant(ChecklistItemInstance item, Long tenantId) {
        if (item.getChecklist() == null || item.getChecklist().getTenant() == null
                || item.getChecklist().getTenant().getId() == null
                || !item.getChecklist().getTenant().getId().equals(tenantId)) {
            log.warn("Access denied to checklist item id={} for tenantId={}", item.getId(), tenantId);
            throw new UnauthorizedException("Checklist item does not belong to current organization");
        }
    }
}
