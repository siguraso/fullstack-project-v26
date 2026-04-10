package edu.ntnu.idi.idatt2105.backend.core.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper.DeviationMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.dashboard.dto.ChecklistTodaySummaryDTO;
import edu.ntnu.idi.idatt2105.backend.core.dashboard.dto.DashboardActivityDTO;
import edu.ntnu.idi.idatt2105.backend.core.dashboard.dto.DashboardOverviewDTO;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service that assembles the compliance dashboard overview for the current
 * tenant.
 * <p>
 * Aggregates data from checklists, deviations, temperature logs and alcohol
 * logs into a single {@link DashboardOverviewDTO} suitable for the dashboard
 * landing page.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    private static final int ACTIVITY_LIMIT = 20;
    private static final int SOURCE_FETCH_LIMIT = 20;

    private final ChecklistInstanceRepository checklistInstanceRepository;
    private final ChecklistInstanceMapper checklistInstanceMapper;
    private final DeviationRepository deviationRepository;
    private final DeviationMapper deviationMapper;
    private final TemperatureLogRepository temperatureLogRepository;
    private final AlcoholLogRepository alcoholLogRepository;

    /**
     * Builds a full dashboard overview for the current tenant including
     * today's checklist summary, pending checklists, active deviations,
     * critical alerts, and the most recent team activity across all
     * compliance modules.
     *
     * @return a populated {@link DashboardOverviewDTO}
     */
    public DashboardOverviewDTO getOverview() {
        Long tenantId = TenantContext.getCurrentOrg();

        List<ChecklistInstance> todayChecklists = getTodayChecklists(tenantId);
        List<Deviation> activeDeviations = getActiveDeviations(tenantId);
        List<Deviation> criticalDeviations = getCriticalDeviations(tenantId);

        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        overview.setChecklistsToday(buildChecklistTodaySummary(todayChecklists));
        overview.setPendingChecklists(buildPendingChecklists(todayChecklists));
        overview.setActiveDeviations(mapDeviations(activeDeviations));
        overview.setCriticalAlerts(mapDeviations(criticalDeviations));
        overview.setTeamActivity(buildTeamActivity(tenantId));

        return overview;
    }

    private List<ChecklistInstance> getTodayChecklists(Long tenantId) {
        return checklistInstanceRepository.findTodayWithItems(tenantId, LocalDate.now());
    }

    private List<Deviation> getActiveDeviations(Long tenantId) {
        return deviationRepository.findByTenantIdAndStatusInOrderByCreatedAtDesc(
                tenantId,
                List.of(DeviationStatus.OPEN, DeviationStatus.IN_PROGRESS));
    }

    private List<Deviation> getCriticalDeviations(Long tenantId) {
        return deviationRepository.findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
                tenantId,
                List.of(DeviationStatus.OPEN, DeviationStatus.IN_PROGRESS),
                DeviationSeverity.CRITICAL);
    }

    private List<edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO> mapDeviations(
            List<Deviation> deviations) {
        return deviations.stream().map(deviationMapper::toDTO).toList();
    }

    private ChecklistTodaySummaryDTO buildChecklistTodaySummary(List<ChecklistInstance> checklists) {
        int totalChecklists = checklists.size();
        int completedChecklists = (int) checklists.stream()
                .filter(checklist -> checklist.getStatus() == ChecklistStatus.COMPLETED)
                .count();
        int totalItems = checklists.stream()
                .map(ChecklistInstance::getItems)
                .mapToInt(List::size)
                .sum();
        int completedItems = (int) checklists.stream()
                .flatMap(checklist -> checklist.getItems().stream())
                .filter(ChecklistItemInstance::isCompleted)
                .count();

        ChecklistTodaySummaryDTO summary = new ChecklistTodaySummaryDTO();
        summary.setTotalChecklists(totalChecklists);
        summary.setCompletedChecklists(completedChecklists);
        summary.setTotalItems(totalItems);
        summary.setCompletedItems(completedItems);

        return summary;
    }

    private List<ChecklistInstanceDTO> buildPendingChecklists(List<ChecklistInstance> checklists) {
        return checklists.stream()
                .filter(checklist -> checklist.getStatus() != ChecklistStatus.COMPLETED)
                .map(checklistInstanceMapper::toDto)
                .toList();
    }

    private List<DashboardActivityDTO> buildTeamActivity(Long tenantId) {

        List<ActivityItem> activity = new ArrayList<>();

        activity.addAll(buildTemperatureActivities(tenantId));
        activity.addAll(buildAlcoholActivities(tenantId));
        activity.addAll(buildDeviationActivities(tenantId));

        return activity.stream()
                .sorted(Comparator.comparing(ActivityItem::occurredAt).reversed())
                .limit(ACTIVITY_LIMIT)
                .map(ActivityItem::dto)
                .toList();
    }

    private List<ActivityItem> buildTemperatureActivities(Long tenantId) {
        List<TemperatureComplianceLog> logs = temperatureLogRepository
                .findAllByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();

        return logs.stream()
                .map(log -> new ActivityItem(
                        log.getRecordedAt(),
                        createTemperatureActivity(log)))
                .toList();
    }

    private List<ActivityItem> buildAlcoholActivities(Long tenantId) {
        List<AlcoholComplianceLog> logs = alcoholLogRepository
                .findAllByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();

        return logs.stream()
                .map(log -> new ActivityItem(
                        log.getRecordedAt(),
                        createAlcoholActivity(log)))
                .toList();
    }

    private List<ActivityItem> buildDeviationActivities(Long tenantId) {
        List<Deviation> deviations = deviationRepository
                .findByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        return deviations.stream()
                .map(dev -> new ActivityItem(
                        dev.getCreatedAt(),
                        createDeviationActivity(dev)))
                .toList();
    }

    private DashboardActivityDTO createTemperatureActivity(TemperatureComplianceLog log) {
        DashboardActivityDTO dto = new DashboardActivityDTO();
        dto.setType("TEMPERATURE_LOG");
        dto.setTitle(log.getTitle());
        dto.setDescription("Logged " + log.getTemperatureCelsius() + " C for zone "
                + log.getTemperatureZone().getName());
        dto.setActor(resolveActorName(log.getRecordedBy()));
        dto.setOccurredAt(log.getRecordedAt().toString());
        dto.setReferenceId(log.getId());
        return dto;
    }

    private DashboardActivityDTO createAlcoholActivity(AlcoholComplianceLog log) {
        DashboardActivityDTO dto = new DashboardActivityDTO();
        dto.setType("ALCOHOL_LOG");
        dto.setTitle(log.getTitle());
        dto.setDescription(log.getDescription());
        dto.setActor(resolveActorName(log.getRecordedBy()));
        dto.setOccurredAt(log.getRecordedAt().toString());
        dto.setReferenceId(log.getId());
        return dto;
    }

    private DashboardActivityDTO createDeviationActivity(Deviation deviation) {
        DashboardActivityDTO dto = new DashboardActivityDTO();
        dto.setType("DEVIATION_CREATED");
        dto.setTitle(deviation.getTitle());
        dto.setDescription(summarizeDeviation(deviation));
        dto.setActor(resolveActorName(deviation.getCreatedBy()));
        dto.setOccurredAt(deviation.getCreatedAt().toString());
        dto.setReferenceId(deviation.getId());
        return dto;
    }

    private String resolveActorName(User user) {
        if (user == null) {
            return "System";
        }

        String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }

        return "System";
    }

    private String summarizeDeviation(Deviation deviation) {
        if (deviation.getIssueDescription() != null && !deviation.getIssueDescription().isBlank()) {
            return deviation.getIssueDescription();
        }

        if (deviation.getImmediateAction() != null && !deviation.getImmediateAction().isBlank()) {
            return deviation.getImmediateAction();
        }

        return "Deviation recorded";
    }

    private record ActivityItem(LocalDateTime occurredAt, DashboardActivityDTO dto) {
    }

}
