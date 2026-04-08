package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service.DeviationService;
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
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.enums.AlcoholLogType;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper.AlcoholLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;

@Service
@Transactional
public class AlcoholLogService extends BaseComplianceLogService<AlcoholComplianceLog> {

    private final AlcoholLogRepository repository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final AlcoholLogMapper mapper;
    private final DeviationService deviationService;

    public AlcoholLogService(
            AlcoholLogRepository repository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            AlcoholLogMapper mapper,
            DeviationService deviationService) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.deviationService = deviationService;
    }

    @Override
    protected BaseComplianceLogRepository<AlcoholComplianceLog> getRepository() {
        return repository;
    }

    public AlcoholLogDTO createLog(AlcoholLogCreateRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = resolveAuthenticatedUser(tenantId);
        LogStatus status = resolveStatus(request);
        AlcoholComplianceLog log = mapper.toEntity(request, tenant, recordedBy, status);
        AlcoholComplianceLog savedLog = repository.save(log);

        if (status == LogStatus.WARNING || status == LogStatus.CRITICAL) {
            deviationService.createFromLog(savedLog);
        }

        return mapper.toDTO(savedLog);
    }

    public AlcoholLogDTO recordRefusal(AlcoholLogCreateRequest request) {
        request.setServiceRefused(true);
        return createLog(request);
    }

    @Transactional(readOnly = true)
    public List<AlcoholLogDTO> getAllForCurrentOrgAsDTO() {
        return getAllForCurrentOrg().stream().map(this::toEnrichedDTO).toList();
    }

    @Transactional(readOnly = true)
    public AlcoholLogDTO getByIdAsDTO(Long id) {
        return toEnrichedDTO(getById(id));
    }

    private AlcoholLogDTO toEnrichedDTO(AlcoholComplianceLog entity) {
        AlcoholLogDTO dto = mapper.toDTO(entity);

        if (dto.getRecordedByName() != null && !dto.getRecordedByName().isBlank()) {
            return dto;
        }

        if (dto.getRecordedById() == null) {
            return dto;
        }

        userRepository.findById(dto.getRecordedById()).ifPresent(user -> {
            String fullName = String.format("%s %s",
                    user.getFirstName() != null ? user.getFirstName().trim() : "",
                    user.getLastName() != null ? user.getLastName().trim() : "").trim();

            String displayName = fullName;
            dto.setRecordedByName(displayName == null || displayName.isBlank() ? null : displayName);
        });

        return dto;
    }

    private User resolveAuthenticatedUser(Long tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("Authenticated user is required");
        }

        Object principal = authentication.getPrincipal();
        String email = principal instanceof String string ? string : principal.toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Authenticated user does not belong to current organization");
        }

        return user;
    }

    private LogStatus resolveStatus(AlcoholLogCreateRequest request) {
        LogStatus requestedStatus = request.getStatus();
        if (requestedStatus != null
                && requestedStatus != LogStatus.OK
                && requestedStatus != LogStatus.WARNING
                && requestedStatus != LogStatus.CRITICAL) {
            throw new ValidationException("Alcohol logs only support OK, WARNING, or CRITICAL status");
        }

        if (isDeviationWorthy(request)) {
            return requestedStatus == LogStatus.CRITICAL ? LogStatus.CRITICAL : LogStatus.WARNING;
        }

        return requestedStatus != null ? requestedStatus : LogStatus.OK;
    }

    private boolean isDeviationWorthy(AlcoholLogCreateRequest request) {
        return Boolean.TRUE.equals(request.getServiceRefused())
                || request.getLogType() == AlcoholLogType.SERVICE_REFUSAL
                || request.getLogType() == AlcoholLogType.INCIDENT;
    }
}
