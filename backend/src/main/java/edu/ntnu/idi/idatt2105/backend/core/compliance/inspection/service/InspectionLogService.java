package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionExportFilter;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionStatsDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InspectionLogService {

    private final DeviationRepository deviationRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final AlcoholLogRepository alcoholLogRepository;

    public List<InspectionLogDTO> getInspectionLogs() {
        Long tenantId = TenantContext.getCurrentOrg();

        List<InspectionLogDTO> logs = new ArrayList<>();

        for (Deviation d : deviationRepository.findByTenantId(tenantId)) {
            logs.add(mapDeviation(d));
        }

        for (TemperatureComplianceLog log : temperatureLogRepository.findAllByTenantId(tenantId)) {
            logs.add(mapTemperatureLog(log));
        }

        for (AlcoholComplianceLog log : alcoholLogRepository.findAllByTenantId(tenantId)) {
            logs.add(mapAlcoholLog(log));
        }

        return logs.stream()
                .sorted(Comparator.comparing(InspectionLogDTO::getTimestamp).reversed())
                .toList();
    }

    public List<InspectionLogDTO> getFilteredLogs(InspectionExportFilter filter) {
        Long tenantId = TenantContext.getCurrentOrg();

        Set<String> types = normalizedSet(filter.getTypes());
        Set<String> severities = normalizedSet(filter.getSeverities());
        Set<String> statuses = normalizedSet(filter.getStatuses());

        List<InspectionLogDTO> logs = new ArrayList<>();

        if (types.isEmpty() || types.contains("DEVIATION")) {
            deviationRepository.findByTenantId(tenantId).stream()
                    .filter(d -> severities.isEmpty() || severities.contains(d.getSeverity().name()))
                    .filter(d -> statuses.isEmpty() || statuses.contains(d.getStatus().name()))
                    .filter(d -> isWithinDateRange(d.getCreatedAt().toLocalDate(), filter))
                    .map(this::mapDeviation)
                    .forEach(logs::add);
        }

        if (types.isEmpty() || types.contains("TEMPERATURE")) {
            temperatureLogRepository.findAllByTenantId(tenantId).stream()
                    .filter(l -> statuses.isEmpty() || statuses.contains(l.getStatus().name()))
                    .filter(l -> isWithinDateRange(l.getRecordedAt().toLocalDate(), filter))
                    .map(this::mapTemperatureLog)
                    .forEach(logs::add);
        }

        if (types.isEmpty() || types.contains("ALCOHOL")) {
            alcoholLogRepository.findAllByTenantId(tenantId).stream()
                    .filter(l -> statuses.isEmpty() || statuses.contains(l.getStatus().name()))
                    .filter(l -> isWithinDateRange(l.getRecordedAt().toLocalDate(), filter))
                    .map(this::mapAlcoholLog)
                    .forEach(logs::add);
        }

        return logs.stream()
                .sorted(Comparator.comparing(InspectionLogDTO::getTimestamp).reversed())
                .toList();
    }

    public InspectionStatsDTO getStats() {
        Long tenantId = TenantContext.getCurrentOrg();

        List<Deviation> deviations = deviationRepository.findByTenantId(tenantId);
        List<TemperatureComplianceLog> tempLogs = temperatureLogRepository.findAllByTenantId(tenantId);
        List<AlcoholComplianceLog> alcoholLogs = alcoholLogRepository.findAllByTenantId(tenantId);

        return InspectionStatsDTO.builder()
                // Deviations
                .deviationTotal(deviations.size())
                .deviationOpen(countDeviationByStatus(deviations, DeviationStatus.OPEN))
                .deviationInProgress(countDeviationByStatus(deviations, DeviationStatus.IN_PROGRESS))
                .deviationResolved(countDeviationByStatus(deviations, DeviationStatus.RESOLVED))
                .deviationCritical(countDeviationBySeverity(deviations, DeviationSeverity.CRITICAL))
                .deviationHigh(countDeviationBySeverity(deviations, DeviationSeverity.HIGH))
                .deviationMedium(countDeviationBySeverity(deviations, DeviationSeverity.MEDIUM))
                .deviationLow(countDeviationBySeverity(deviations, DeviationSeverity.LOW))
                // Temperature
                .temperatureTotal(tempLogs.size())
                .temperatureOk(countLogByStatus(tempLogs, LogStatus.OK))
                .temperatureWarning(countLogByStatus(tempLogs, LogStatus.WARNING))
                .temperatureCritical(countLogByStatus(tempLogs, LogStatus.CRITICAL))
                // Alcohol
                .alcoholTotal(alcoholLogs.size())
                .alcoholOk(countLogByStatus(alcoholLogs, LogStatus.OK))
                .alcoholWarning(countLogByStatus(alcoholLogs, LogStatus.WARNING))
                .alcoholCritical(countLogByStatus(alcoholLogs, LogStatus.CRITICAL))
                .build();
    }

    private InspectionLogDTO mapDeviation(Deviation d) {
        InspectionLogDTO dto = new InspectionLogDTO();
        dto.setType("DEVIATION");
        dto.setReferenceId(d.getId());
        dto.setTitle(d.getTitle());
        dto.setDescription(d.getIssueDescription());
        dto.setStatus(d.getStatus().name());
        dto.setSeverity(d.getSeverity() != null ? d.getSeverity().name() : null);
        dto.setModule(d.getModule().name());
        dto.setTimestamp(d.getCreatedAt().toString());
        dto.setActor(resolveActor(d.getCreatedBy()));
        return dto;
    }

    private InspectionLogDTO mapTemperatureLog(TemperatureComplianceLog log) {
        InspectionLogDTO dto = new InspectionLogDTO();
        dto.setType("TEMPERATURE");
        dto.setReferenceId(log.getId());
        dto.setTitle(log.getTitle());
        dto.setDescription(log.getDescription());
        dto.setStatus(log.getStatus().name());
        dto.setModule(log.getModule().name());
        dto.setTimestamp(log.getRecordedAt().toString());
        dto.setActor(resolveActor(log.getRecordedBy()));
        return dto;
    }

    private InspectionLogDTO mapAlcoholLog(AlcoholComplianceLog log) {
        InspectionLogDTO dto = new InspectionLogDTO();
        dto.setType("ALCOHOL");
        dto.setReferenceId(log.getId());
        dto.setTitle(log.getTitle());
        dto.setDescription(log.getDescription());
        dto.setStatus(log.getStatus().name());
        dto.setModule(log.getModule().name());
        dto.setTimestamp(log.getRecordedAt().toString());
        dto.setActor(resolveActor(log.getRecordedBy()));
        return dto;
    }

    private String resolveActor(User user) {
        if (user == null)
            return "System";
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    private boolean isWithinDateRange(LocalDate date, InspectionExportFilter filter) {
        return (filter.getDateFrom() == null || !date.isBefore(filter.getDateFrom()))
                && (filter.getDateTo() == null || !date.isAfter(filter.getDateTo()));
    }

    private Set<String> normalizedSet(List<String> list) {
        if (list == null || list.isEmpty())
            return Set.of();
        return list.stream().map(String::toUpperCase).collect(Collectors.toSet());
    }

    private long countDeviationByStatus(List<Deviation> list, DeviationStatus status) {
        return list.stream().filter(d -> status == d.getStatus()).count();
    }

    private long countDeviationBySeverity(List<Deviation> list, DeviationSeverity severity) {
        return list.stream().filter(d -> severity == d.getSeverity()).count();
    }

    private <T extends edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog> long countLogByStatus(
            List<T> list, LogStatus status) {
        return list.stream().filter(l -> status == l.getStatus()).count();
    }
}
