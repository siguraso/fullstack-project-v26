package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.service;

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
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.mapper.TemperatureLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository.TemperatureZoneRepository;

@Service
@Transactional
public class TemperatureLogService extends BaseComplianceLogService<TemperatureComplianceLog> {

    private final TemperatureLogRepository repository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final TemperatureZoneRepository temperatureZoneRepository;
    private final TemperatureLogMapper mapper;
    private final DeviationService deviationService;

    public TemperatureLogService(
            TemperatureLogRepository repository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            TemperatureZoneRepository temperatureZoneRepository,
            TemperatureLogMapper mapper,
            DeviationService deviationService) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.temperatureZoneRepository = temperatureZoneRepository;
        this.mapper = mapper;
        this.deviationService = deviationService;
    }

    @Override
    protected BaseComplianceLogRepository<TemperatureComplianceLog> getRepository() {
        return repository;
    }

    public TemperatureLogDTO createLog(TemperatureLogCreateRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = resolveAuthenticatedUser(tenantId);

        TemperatureZone zone = temperatureZoneRepository.findById(request.getTemperatureZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Temperature zone not found"));

        if (!zone.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Temperature zone does not belong to current organization");
        }

        LogStatus status = resolveStatus(request.getTemperatureCelsius(), zone);
        TemperatureComplianceLog log = mapper.toEntity(request, tenant, recordedBy, zone, status);
        TemperatureComplianceLog savedLog = repository.save(log);
        boolean deviationCreated = false;

        if (status == LogStatus.WARNING) {
            deviationService.createFromLog(savedLog);
            deviationCreated = true;
        }

        TemperatureLogDTO dto = mapper.toDTO(savedLog);
        dto.setDeviationCreated(deviationCreated);
        return dto;
    }

    private LogStatus resolveStatus(Double measuredTemperature, TemperatureZone zone) {
        if (measuredTemperature < zone.getLowerLimitCelsius() || measuredTemperature > zone.getUpperLimitCelsius()) {
            return LogStatus.WARNING;
        }
        return LogStatus.OK;
    }

    private User resolveAuthenticatedUser(Long tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Authenticated user is required");
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("Authenticated user was not found");
        }

        String email = principal instanceof String string ? string : principal.toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Authenticated user does not belong to current organization");
        }

        return user;
    }
}
