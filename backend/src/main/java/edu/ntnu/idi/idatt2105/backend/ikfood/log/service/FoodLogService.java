package edu.ntnu.idi.idatt2105.backend.ikfood.log.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.dto.FoodLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.entity.FoodComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.enums.FoodLogType;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.mapper.FoodLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.log.repository.FoodLogRepository;

@Service
@Transactional
public class FoodLogService extends BaseComplianceLogService<FoodComplianceLog> {

    private final FoodLogRepository repository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final FoodLogMapper mapper;

    public FoodLogService(
            FoodLogRepository repository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            FoodLogMapper mapper) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    protected BaseComplianceLogRepository<FoodComplianceLog> getRepository() {
        return repository;
    }

    public FoodLogDTO createLog(FoodLogCreateRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = userRepository.findById(request.getRecordedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!recordedBy.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("User does not belong to current organization");
        }

        FoodComplianceLog log = mapper.toEntity(request, tenant, recordedBy);
        return mapper.toDTO(repository.save(log));
    }

    public FoodLogDTO createFromTemperatureDeviation(Long temperatureLogId, String title) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = resolveAuthenticatedUser(tenantId);

        FoodComplianceLog log = new FoodComplianceLog();
        log.setTenant(tenant);
        log.setRecordedBy(recordedBy);
        log.setTitle(title);
        log.setDescription("Automatically created from temperature deviation");
        log.setStatus(LogStatus.OPEN);
        log.setLogType(FoodLogType.TEMPERATURE_CHECK);
        log.setTemperatureLogId(temperatureLogId);

        return mapper.toDTO(repository.save(log));
    }

    private User resolveAuthenticatedUser(Long tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Authenticated user is required");
        }

        String email = authentication.getPrincipal().toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Authenticated user does not belong to current organization");
        }

        return user;
    }
}

