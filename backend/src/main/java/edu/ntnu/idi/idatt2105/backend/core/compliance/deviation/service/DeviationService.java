package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.CreateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.UpdateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper.DeviationMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviationService {

    private final DeviationRepository deviationRepo;
    private final TenantRepository tenantRepo;
    private final DeviationMapper mapper;

    public DeviationDTO create(CreateDeviationRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Creating deviation for tenantId={} module={} severity={} status={}",
                tenantId, request.getModule(), request.getSeverity(), request.getStatus());
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Deviation deviation = new Deviation();
        deviation.setTenant(tenant);
        deviation.setModule(request.getModule());

        deviation.setTitle(request.getTitle());
        deviation.setDescription(request.getDescription());

        deviation.setSeverity(request.getSeverity());
        deviation.setCategory(request.getCategory());

        deviation.setStatus(request.getStatus());
        deviation.setCreatedAt(LocalDateTime.now());

        if (request.getStatus() == DeviationStatus.RESOLVED) {
            deviation.setResolvedAt(LocalDateTime.now());
        }

        Deviation savedDeviation = deviationRepo.save(deviation);
        log.info("Created deviation id={} for tenantId={}", savedDeviation.getId(), tenantId);
        return mapper.toDTO(savedDeviation);
    }

    public List<DeviationDTO> getForCurrentTenant() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<DeviationDTO> deviations = deviationRepo.findByTenantId(tenantId).stream().map(mapper::toDTO).toList();
        log.debug("Fetched {} deviations for tenantId={}", deviations.size(), tenantId);
        return deviations;
    }

    public DeviationDTO update(Long id, UpdateDeviationRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Updating deviation id={} for tenantId={} requestedStatus={}", id, tenantId, request.getStatus());
        Deviation deviation = deviationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deviation not found"));
        ensureDeviationOwnedByTenant(deviation, tenantId);

        if (request.getStatus() != null) {
            deviation.setStatus(request.getStatus());

            if (request.getStatus() == DeviationStatus.RESOLVED) {
                deviation.setResolvedAt(LocalDateTime.now());
            }
        }

        Deviation savedDeviation = deviationRepo.save(deviation);
        log.info("Updated deviation id={} to status={}", savedDeviation.getId(), savedDeviation.getStatus());
        return mapper.toDTO(savedDeviation);
    }

    public void createFromChecklist(ChecklistItemInstance item) {
        log.info("Creating deviation from checklist item id={} checklistId={}", item.getId(), item.getChecklist().getId());

        Deviation deviation = new Deviation();
        deviation.setTenant(item.getChecklist().getTenant());
        deviation.setModule(item.getChecklist().getTemplate().getModule());

        deviation.setTitle("Checklist item not completed");
        deviation.setDescription(item.getTemplateItem().getDescription());

        deviation.setSeverity(DeviationSeverity.MEDIUM);
        deviation.setCategory(DeviationCategory.HYGIENE);

        deviation.setStatus(DeviationStatus.OPEN);
        deviation.setChecklistItemId(item.getId());

        deviation.setCreatedAt(LocalDateTime.now());

        Deviation savedDeviation = deviationRepo.save(deviation);
        log.info("Created checklist-derived deviation id={} from itemId={}", savedDeviation.getId(), item.getId());
    }

    public void createFromLog(BaseComplianceLog complianceLog) {
        log.info("Creating deviation from compliance log id={} module={} status={}",
                complianceLog.getId(), complianceLog.getModule(), complianceLog.getStatus());

        Deviation deviation = new Deviation();
        deviation.setTenant(complianceLog.getTenant());
        deviation.setModule(complianceLog.getModule());

        deviation.setTitle("Critical log detected");
        deviation.setDescription(complianceLog.getDescription() != null ? complianceLog.getDescription() : complianceLog.getTitle());

        deviation.setSeverity(DeviationSeverity.CRITICAL);
        deviation.setCategory(resolveCategoryFromModule(complianceLog.getModule()));

        deviation.setStatus(DeviationStatus.OPEN);
        deviation.setCreatedBy(complianceLog.getRecordedBy());
        deviation.setLogId(complianceLog.getId());

        deviation.setCreatedAt(LocalDateTime.now());

        Deviation savedDeviation = deviationRepo.save(deviation);
        log.info("Created log-derived deviation id={} from complianceLogId={}", savedDeviation.getId(), complianceLog.getId());
    }

    private DeviationCategory resolveCategoryFromModule(ComplianceModule module) {
        if (module == ComplianceModule.IK_ALCOHOL) {
            return DeviationCategory.ALCOHOL;
        }
        return DeviationCategory.TEMPERATURE;
    }

    private void ensureDeviationOwnedByTenant(Deviation deviation, Long tenantId) {
        if (deviation.getTenant() == null || deviation.getTenant().getId() == null || !deviation.getTenant().getId().equals(tenantId)) {
            log.warn("Access denied to deviation id={} for tenantId={}", deviation.getId(), tenantId);
            throw new UnauthorizedException("Deviation does not belong to current organization");
        }
    }
}
