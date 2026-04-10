package edu.ntnu.idi.idatt2105.backend.ikalcohol.log.mapper;

import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.mapper.BaseComplianceLogMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.dto.AlcoholLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;

/**
 * Mapper for converting between {@link AlcoholComplianceLog} entities,
 * {@link AlcoholLogDTO} data objects and {@link AlcoholLogCreateRequest}
 * requests.
 */
@Component
public class AlcoholLogMapper extends BaseComplianceLogMapper<AlcoholComplianceLog, AlcoholLogDTO> {

    /**
     * Converts an alcohol compliance log entity to its DTO representation.
     *
     * @param entity the entity to convert; returns {@code null} if
     *               {@code entity} is {@code null}
     * @return the corresponding {@link AlcoholLogDTO}
     */
    public AlcoholLogDTO toDTO(AlcoholComplianceLog entity) {
        if (entity == null) {
            return null;
        }

        AlcoholLogDTO dto = new AlcoholLogDTO();
        toBaseDTO(entity, dto);
        dto.setLogType(entity.getLogType());
        dto.setIdChecked(entity.getIdChecked());
        dto.setServiceRefused(entity.getServiceRefused());
        dto.setEstimatedAge(entity.getEstimatedAge());
        return dto;
    }

    /**
     * Creates an {@link AlcoholComplianceLog} entity from a creation request.
     *
     * @param request    the creation request
     * @param tenant     the tenant to associate the log with
     * @param recordedBy the user recording the log
     * @param status     the resolved log status
     * @return a new, unpersisted {@link AlcoholComplianceLog} entity
     */
    public AlcoholComplianceLog toEntity(AlcoholLogCreateRequest request, Tenant tenant, User recordedBy,
            LogStatus status) {
        AlcoholComplianceLog entity = new AlcoholComplianceLog();
        entity.setTenant(tenant);
        entity.setRecordedBy(recordedBy);
        entity.setModule(ComplianceModule.IK_ALCOHOL);
        entity.setTitle(request.getTitle());
        entity.setDescription(resolveDescription(request));
        entity.setStatus(status);
        entity.setLogType(request.getLogType());
        entity.setIdChecked(request.getIdChecked());
        entity.setServiceRefused(request.getServiceRefused());
        entity.setEstimatedAge(request.getEstimatedAge());
        return entity;
    }

    private String resolveDescription(AlcoholLogCreateRequest request) {
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            return request.getDescription();
        }

        if (request.getNote() != null && !request.getNote().isBlank()) {
            return request.getNote();
        }

        return null;
    }
}
