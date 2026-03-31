package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.repository.BaseComplianceLogRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.service.BaseComplianceLogService;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper.AlcoholLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;

@Service
@Transactional
public class AlcoholLogService extends BaseComplianceLogService<AlcoholComplianceLog> {

    private final AlcoholLogRepository repository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final AlcoholLogMapper mapper;

    public AlcoholLogService(
            AlcoholLogRepository repository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            AlcoholLogMapper mapper) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    protected BaseComplianceLogRepository<AlcoholComplianceLog> getRepository() {
        return repository;
    }

    public AlcoholLogDTO createLog(AlcoholLogCreateRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = userRepository.findById(request.getRecordedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!recordedBy.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("User does not belong to current organization");
        }

        AlcoholComplianceLog log = mapper.toEntity(request, tenant, recordedBy);
        return mapper.toDTO(repository.save(log));
    }

    public AlcoholLogDTO recordRefusal(AlcoholLogCreateRequest request) {
        request.setServiceRefused(true);
        request.setStatus(LogStatus.OPEN);
        return createLog(request);
    }
}

