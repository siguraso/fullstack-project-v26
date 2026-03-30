package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CompleteChecklistItemRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.CreateChecklistTemplateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistItemTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistItemTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistTemplateRepository templateRepo;
    private final ChecklistItemTemplateRepository itemTemplateRepo;
    private final ChecklistInstanceRepository instanceRepo;
    private final ChecklistItemInstanceRepository itemInstanceRepo;
    private final TenantRepository tenantRepo;
    private final DeviationService deviationService;
    private final ChecklistInstanceMapper mapper;

    public ChecklistTemplate createTemplate(CreateChecklistTemplateRequest request) {
        Tenant tenant = tenantRepo.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        ChecklistTemplate template = new ChecklistTemplate();
        template.setTenant(tenant);
        ;
        template.setName(request.getName());
        template.setFrequency(request.getFrequency());
        template.setModule(request.getModule());

        ChecklistTemplate savedTemplate = templateRepo.save(template);

        int order = 0;
        for (String description : request.getItems()) {
            ChecklistItemTemplate item = new ChecklistItemTemplate();
            item.setChecklistTemplate(savedTemplate);
            item.setDescription(description);
            item.setSortOrder(order++);
            itemTemplateRepo.save(item);
        }

        return savedTemplate;
    }

    public ChecklistInstance generateInstance(Long templateId) {

        ChecklistTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        ChecklistInstance instance = new ChecklistInstance();
        instance.setTemplate(template);
        instance.setDate(LocalDate.now());
        instance.setStatus(ChecklistStatus.PENDING);
        instance.setTenant(template.getTenant());

        ChecklistInstance savedInstance = instanceRepo.save(instance);

        List<ChecklistItemTemplate> templates = itemTemplateRepo.findByChecklistId(templateId);

        for (ChecklistItemTemplate t : templates) {
            ChecklistItemInstance item = new ChecklistItemInstance();
            item.setChecklist(savedInstance);
            item.setTemplateItem(t);
            item.setCompleted(false);
            itemInstanceRepo.save(item);
        }

        return savedInstance;
    }

    public List<ChecklistInstanceDTO> getTodayChecklist(Long tenantId) {

        List<ChecklistInstance> instances = instanceRepo.findByTenantIdAndDate(tenantId, LocalDate.now());

        return instances.stream().map(mapper::toDto).toList();
    }

    public void completeItem(Long itemId, CompleteChecklistItemRequest request) {

        ChecklistItemInstance item = itemInstanceRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));

        item.setCompleted(request.isCompleted());
        item.setCompletedAt(LocalDate.now());
        item.setComment(request.getComment());

        itemInstanceRepo.save(item);

        updateCheckListStatus(item.getChecklist().getId());
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
    }
}
