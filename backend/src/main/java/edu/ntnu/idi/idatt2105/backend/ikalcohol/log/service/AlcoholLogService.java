package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing alcohol compliance logs in the IK Alcohol module.
 * <p>
 * Extends {@link BaseComplianceLogService} with alcohol-specific creation
 * logic including automatic status resolution for service refusals and
 * incidents. All operations are tenant-scoped.
 */
@Service
@Transactional
@Slf4j
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

    /**
     * Creates a new alcohol compliance log entry for the current tenant.
     * The log status is derived from the request type and refusal flag.
     *
     * @param request the log details including type, status and refusal fields
     * @return the persisted {@link AlcoholLogDTO}
     */
    public AlcoholLogDTO createLog(AlcoholLogCreateRequest request) {
        Long tenantId = TenantContext.getCurrentOrg();
        log.info("Creating alcohol compliance log for tenantId={} type={}", tenantId, request.getLogType());
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User recordedBy = resolveAuthenticatedUser(tenantId);
        LogStatus status = resolveStatus(request);
        AlcoholComplianceLog alcoholLog = mapper.toEntity(request, tenant, recordedBy, status);
        AlcoholComplianceLog savedLog = repository.save(alcoholLog);

        log.info("Created alcohol log id={} status={} deviationCreated=false", savedLog.getId(), status);

        return mapper.toDTO(savedLog);
    }

    /**
     * Convenience method to record a service refusal. Sets the
     * {@code serviceRefused} flag and delegates to {@link #createLog}.
     *
     * @param request the log details for the refusal event
     * @return the persisted {@link AlcoholLogDTO}
     */
    public AlcoholLogDTO recordRefusal(AlcoholLogCreateRequest request) {
        log.info("Recording alcohol service refusal for current tenant");
        request.setServiceRefused(true);
        return createLog(request);
    }

    /**
     * Returns all alcohol compliance logs for the current tenant as DTOs,
     * with actor names resolved from the user repository if needed.
     *
     * @return a list of {@link AlcoholLogDTO} objects
     */
    @Transactional(readOnly = true)
    public List<AlcoholLogDTO> getAllForCurrentOrgAsDTO() {
        Long tenantId = TenantContext.getCurrentOrg();
        List<AlcoholLogDTO> logs = repository.findAllByTenantId(tenantId).stream().map(this::toEnrichedDTO).toList();
        log.debug("Fetched {} alcohol logs for current tenant", logs.size());
        return logs;
    }

    /**
     * Retrieves a single alcohol compliance log as a DTO, with actor name
     * resolved if missing.
     *
     * @param id the log identifier
     * @return the {@link AlcoholLogDTO} for the requested entry
     */
    @Transactional(readOnly = true)
    public AlcoholLogDTO getByIdAsDTO(Long id) {
        log.debug("Fetching alcohol log id={}", id);
        return toEnrichedDTO(getAuthorizedLogById(id));
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
            String fullName = ((user.getFirstName() != null ? user.getFirstName().trim() : "") + " "
                    + (user.getLastName() != null ? user.getLastName().trim() : "")).trim();
            dto.setRecordedByName(fullName.isBlank() ? null : fullName);
        });

        return dto;
    }

    private User resolveAuthenticatedUser(Long tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("Missing authentication principal while creating alcohol log for tenantId={}", tenantId);
            throw new UnauthorizedException("Authenticated user is required");
        }

        Object principal = authentication.getPrincipal();
        String email = principal instanceof String string ? string : principal.toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));

        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("Authenticated user id={} does not belong to tenantId={}", user.getId(), tenantId);
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
            log.warn("Rejecting alcohol log with unsupported status={}", requestedStatus);
            throw new ValidationException("Alcohol logs only support OK, WARNING, or CRITICAL status");
        }

        if (isDeviationWorthy(request)) {
            return requestedStatus == LogStatus.CRITICAL ? LogStatus.CRITICAL : LogStatus.WARNING;
        }

        return requestedStatus != null ? requestedStatus : LogStatus.OK;
    }

    private AlcoholComplianceLog getAuthorizedLogById(Long id) {
        AlcoholComplianceLog entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance log not found"));

        Long currentOrgId = TenantContext.getCurrentOrg();
        if (entity.getTenant() == null || entity.getTenant().getId() == null
                || !entity.getTenant().getId().equals(currentOrgId)) {
            log.warn("Access denied to alcohol log id={} for tenantId={}", id, currentOrgId);
            throw new UnauthorizedException("You do not have access to this compliance log");
        }

        return entity;
    }

    private boolean isDeviationWorthy(AlcoholLogCreateRequest request) {
        return Boolean.TRUE.equals(request.getServiceRefused())
                || request.getLogType() == AlcoholLogType.SERVICE_REFUSAL
                || request.getLogType() == AlcoholLogType.INCIDENT;
    }
}
