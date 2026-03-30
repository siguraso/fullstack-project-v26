package edu.ntnu.idi.idatt2105.backend.core.compliance.log.service;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.dto.LogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.ComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.ComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.ComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceLogService {

    private final ComplianceLogRepository complianceLogRepo;
    private final TenantRepository tenantRepo;
    private final UserRepository userRepo;
    private final DeviationService deviationService;
    private final ComplianceLogMapper mapper;

    public ComplianceLog createLog(LogCreateRequest request) {

        Tenant tenant = tenantRepo.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ComplianceLog log = new ComplianceLog();
        log.setTenant(tenant);
        log.setCreatedBy(user);
        log.setModule(request.getModule());
        log.setType(request.getType());
        log.setValue(request.getValue());

        log.setStatus(determineStatus(request.getValue())); // TODO: Determine how we want to set status

        if (log.getStatus() == LogStatus.CRITICAL) {
            deviationService.createFromLog(log);
        }
        return complianceLogRepo.save(log);
    }

    // Temporary function for setting status
    private LogStatus determineStatus(String value) {
        try {
            double v = Double.parseDouble(value);
            if (v > 10)
                return LogStatus.WARNING;
            return LogStatus.OK;
        } catch (Exception e) {
            return LogStatus.OK;
        }
    }
}
