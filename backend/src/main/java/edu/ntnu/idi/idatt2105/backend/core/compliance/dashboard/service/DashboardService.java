package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.service;

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
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.ChecklistTodaySummaryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.DashboardActivityDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.DashboardOverviewDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper.DeviationMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int ACTIVITY_LIMIT = 20;
    private static final int SOURCE_FETCH_LIMIT = 20;

    private final ChecklistInstanceRepository checklistInstanceRepository;
    private final ChecklistInstanceMapper checklistInstanceMapper;
    private final DeviationRepository deviationRepository;
    private final DeviationMapper deviationMapper;
    private final TemperatureLogRepository temperatureLogRepository;
    private final AlcoholLogRepository alcoholLogRepository;

    public DashboardOverviewDTO getOverview() {
        Long tenantId = TenantContext.getCurrentOrg();

        List<ChecklistInstance> todayChecklists = checklistInstanceRepository
                .findByTenantIdAndDate(tenantId, LocalDate.now());
        List<DeviationStatus> activeStatuses = List.of(DeviationStatus.OPEN, DeviationStatus.IN_PROGRESS);

        List<Deviation> activeDeviationEntities = deviationRepository
                .findByTenantIdAndStatusInOrderByCreatedAtDesc(tenantId, activeStatuses);
        List<Deviation> criticalDeviationEntities = deviationRepository
                .findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
                        tenantId,
                        activeStatuses,
                        DeviationSeverity.CRITICAL);

        List<TemperatureComplianceLog> recentTemperatureLogs = temperatureLogRepository
                .findAllByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();
        List<AlcoholComplianceLog> recentAlcoholLogs = alcoholLogRepository
                .findAllByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "recordedAt")))
                .getContent();
        List<Deviation> recentDeviationEntities = deviationRepository
                .findByTenantId(
                        tenantId,
                        PageRequest.of(0, SOURCE_FETCH_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        overview.setChecklistsToday(buildChecklistTodaySummary(todayChecklists));
        overview.setPendingChecklists(buildPendingChecklists(todayChecklists));
        overview.setActiveDeviations(activeDeviationEntities.stream().map(deviationMapper::toDTO).toList());
        overview.setCriticalAlerts(criticalDeviationEntities.stream().map(deviationMapper::toDTO).toList());
        overview.setTeamActivity(buildTeamActivity(recentTemperatureLogs, recentAlcoholLogs, recentDeviationEntities));

        return overview;
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

    private List<DashboardActivityDTO> buildTeamActivity(
            List<TemperatureComplianceLog> temperatureLogs,
            List<AlcoholComplianceLog> alcoholLogs,
            List<Deviation> deviations) {
        List<ActivityItem> activity = new ArrayList<>();

        for (TemperatureComplianceLog log : temperatureLogs) {
            DashboardActivityDTO dto = new DashboardActivityDTO();
            dto.setType("TEMPERATURE_LOG");
            dto.setTitle(log.getTitle());
            dto.setDescription("Logged " + log.getTemperatureCelsius() + " C for zone " + log.getTemperatureZone().getName());
            dto.setActor(resolveActorName(log.getRecordedBy()));
            dto.setOccurredAt(log.getRecordedAt().toString());
            dto.setReferenceId(log.getId());
            activity.add(new ActivityItem(log.getRecordedAt(), dto));
        }

        for (AlcoholComplianceLog log : alcoholLogs) {
            DashboardActivityDTO dto = new DashboardActivityDTO();
            dto.setType("ALCOHOL_LOG");
            dto.setTitle(log.getTitle());
            dto.setDescription(log.getDescription());
            dto.setActor(resolveActorName(log.getRecordedBy()));
            dto.setOccurredAt(log.getRecordedAt().toString());
            dto.setReferenceId(log.getId());
            activity.add(new ActivityItem(log.getRecordedAt(), dto));
        }

        for (Deviation deviation : deviations) {
            DashboardActivityDTO dto = new DashboardActivityDTO();
            dto.setType("DEVIATION_CREATED");
            dto.setTitle(deviation.getTitle());
            dto.setDescription(deviation.getDescription());
            dto.setActor(resolveActorName(deviation.getCreatedBy()));
            dto.setOccurredAt(deviation.getCreatedAt().toString());
            dto.setReferenceId(deviation.getId());
            activity.add(new ActivityItem(deviation.getCreatedAt(), dto));
        }

        return activity.stream()
                .sorted(Comparator.comparing(ActivityItem::occurredAt).reversed())
                .limit(ACTIVITY_LIMIT)
                .map(ActivityItem::dto)
                .toList();
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

    private record ActivityItem(LocalDateTime occurredAt, DashboardActivityDTO dto) {
    }

}







