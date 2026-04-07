package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistInstanceDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.enums.ChecklistStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.mapper.ChecklistInstanceMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.ChecklistTodaySummaryDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.DashboardOverviewDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
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
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock
    private ChecklistInstanceRepository checklistInstanceRepository;
    @Mock
    private ChecklistInstanceMapper checklistInstanceMapper;
    @Mock
    private DeviationRepository deviationRepository;
    @Mock
    private DeviationMapper deviationMapper;
    @Mock
    private TemperatureLogRepository temperatureLogRepository;
    @Mock
    private AlcoholLogRepository alcoholLogRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUpTenant() {
        TenantContext.setCurrentOrg(99L);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void testBuildSummaryPendingAndActivity() {
                Long tenantId = 99L;
        LocalDate today = LocalDate.of(2026, 4, 7);

        try (MockedStatic<LocalDate> localDate = mockStatic(LocalDate.class, org.mockito.Answers.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(today);

            ChecklistInstance completedChecklist = checklist(ChecklistStatus.COMPLETED, false, true);
            ChecklistInstance pendingChecklist = checklist(ChecklistStatus.IN_PROGRESS, true);
            List<ChecklistInstance> todayChecklists = List.of(completedChecklist, pendingChecklist);

            ChecklistInstanceDTO pendingDto = new ChecklistInstanceDTO();
            pendingDto.setId(42L);
            when(checklistInstanceMapper.toDto(eq(pendingChecklist))).thenReturn(pendingDto);
            when(checklistInstanceRepository.findByTenantIdAndDate(eq(tenantId), eq(today))).thenReturn(todayChecklists);

            Deviation activeDeviation = deviation(1L, "Open deviation", LocalDateTime.of(2026, 4, 7, 11, 0));
            Deviation criticalDeviation = deviation(2L, "Critical deviation", LocalDateTime.of(2026, 4, 7, 8, 30));
            when(deviationRepository.findByTenantIdAndStatusInOrderByCreatedAtDesc(eq(tenantId), anyList()))
                    .thenReturn(List.of(activeDeviation));
            when(deviationRepository.findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
                    eq(tenantId),
                    anyList(),
                    eq(DeviationSeverity.CRITICAL)))
                    .thenReturn(List.of(criticalDeviation));
            when(deviationRepository.findByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(activeDeviation)));

            when(deviationMapper.toDTO(any(Deviation.class))).thenAnswer(invocation -> {
                Deviation d = invocation.getArgument(0);
                DeviationDTO dto = new DeviationDTO();
                dto.setId(d.getId());
                dto.setTitle(d.getTitle());
                return dto;
            });

            TemperatureComplianceLog temperatureLog = temperatureLog(
                    10L,
                    "Fridge check",
                    4.2,
                    "Cold Room",
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    user("", "", "chefuser", "chef@example.com"));
            AlcoholComplianceLog alcoholLog = alcoholLog(
                    11L,
                    "ID control",
                    "ID was checked",
                    LocalDateTime.of(2026, 4, 7, 10, 0),
                    null);

            when(temperatureLogRepository.findAllByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(temperatureLog)));
            when(alcoholLogRepository.findAllByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(alcoholLog)));

            DashboardOverviewDTO overview = dashboardService.getOverview();

            ChecklistTodaySummaryDTO summary = overview.getChecklistsToday();
            assertNotNull(summary);
            assertEquals(2, summary.getTotalChecklists());
            assertEquals(1, summary.getCompletedChecklists());
            assertEquals(3, summary.getTotalItems());
            assertEquals(2, summary.getCompletedItems());

            assertEquals(1, overview.getPendingChecklists().size());
            assertEquals(42L, overview.getPendingChecklists().getFirst().getId());
            assertEquals(1, overview.getActiveDeviations().size());
            assertEquals(1, overview.getCriticalAlerts().size());

            assertEquals(3, overview.getTeamActivity().size());
            assertEquals("DEVIATION_CREATED", overview.getTeamActivity().get(0).getType());
            assertEquals("ALCOHOL_LOG", overview.getTeamActivity().get(1).getType());
            assertEquals("TEMPERATURE_LOG", overview.getTeamActivity().get(2).getType());
            assertEquals("Ada Lovelace", overview.getTeamActivity().get(0).getActor());
            assertEquals("System", overview.getTeamActivity().get(1).getActor());
            assertEquals("chefuser", overview.getTeamActivity().get(2).getActor());
            assertEquals(
                    "Logged 4.2 C for zone Cold Room",
                    overview.getTeamActivity().get(2).getDescription());
        }
    }

    @Test
    void testSortActivityByTimeAndLimitTo20Items() {
        Long tenantId = 99L;
        LocalDate today = LocalDate.of(2026, 4, 7);

        try (MockedStatic<LocalDate> localDate = mockStatic(LocalDate.class, org.mockito.Answers.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(today);

            when(checklistInstanceRepository.findByTenantIdAndDate(eq(tenantId), eq(today))).thenReturn(List.of());
            when(deviationRepository.findByTenantIdAndStatusInOrderByCreatedAtDesc(eq(tenantId), anyList()))
                    .thenReturn(List.of());
            when(deviationRepository.findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
                    eq(tenantId),
                    anyList(),
                    eq(DeviationSeverity.CRITICAL)))
                    .thenReturn(List.of());

        List<TemperatureComplianceLog> temperatureLogs = new ArrayList<>();
        List<AlcoholComplianceLog> alcoholLogs = new ArrayList<>();
        List<Deviation> deviations = new ArrayList<>();
        LocalDateTime base = LocalDateTime.of(2026, 4, 7, 0, 0);

        for (int i = 0; i < 15; i++) {
            temperatureLogs.add(temperatureLog(
                    100L + i,
                    "Temp " + i,
                    3.0 + i,
                    "Zone",
                    base.plusMinutes(i),
                    null));
            alcoholLogs.add(alcoholLog(
                    200L + i,
                    "Alcohol " + i,
                    "Desc",
                    base.plusMinutes(30 + i),
                    null));
            deviations.add(deviation(300L + i, "Deviation " + i, base.plusMinutes(60 + i)));
        }

            when(temperatureLogRepository.findAllByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(temperatureLogs));
            when(alcoholLogRepository.findAllByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(alcoholLogs));
            when(deviationRepository.findByTenantId(eq(tenantId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(deviations));

            DashboardOverviewDTO overview = dashboardService.getOverview();

            assertEquals(20, overview.getTeamActivity().size());

            List<String> occurredAt = overview.getTeamActivity().stream()
                    .map(activity -> activity.getOccurredAt())
                    .toList();
            for (int i = 0; i < occurredAt.size() - 1; i++) {
                LocalDateTime current = LocalDateTime.parse(occurredAt.get(i));
                LocalDateTime next = LocalDateTime.parse(occurredAt.get(i + 1));
                assertTrue(!current.isBefore(next));
            }
        }
    }

    private ChecklistInstance checklist(ChecklistStatus status, boolean... completedFlags) {
        ChecklistInstance checklist = new ChecklistInstance();
        checklist.setStatus(status);

        List<ChecklistItemInstance> items = new ArrayList<>();
        for (boolean flag : completedFlags) {
            ChecklistItemInstance item = new ChecklistItemInstance();
            item.setCompleted(flag);
            items.add(item);
        }
        checklist.setItems(items);
        return checklist;
    }

    private Deviation deviation(Long id, String title, LocalDateTime createdAt) {
        Deviation deviation = new Deviation();
        deviation.setId(id);
        deviation.setTitle(title);
        deviation.setDescription("Deviation details");
        deviation.setSeverity(DeviationSeverity.CRITICAL);
        deviation.setStatus(DeviationStatus.OPEN);
        deviation.setCreatedAt(createdAt);
        deviation.setCreatedBy(user("Ada", "Lovelace", "ada", "ada@example.com"));
        return deviation;
    }

    private TemperatureComplianceLog temperatureLog(
            Long id,
            String title,
            Double temperature,
            String zoneName,
            LocalDateTime recordedAt,
            User recordedBy) {
        TemperatureZone zone = new TemperatureZone();
        zone.setName(zoneName);

        TemperatureComplianceLog log = new TemperatureComplianceLog();
        log.setId(id);
        log.setTitle(title);
        log.setTemperatureCelsius(temperature);
        log.setTemperatureZone(zone);
        log.setRecordedAt(recordedAt);
        log.setRecordedBy(recordedBy);
        return log;
    }

    private AlcoholComplianceLog alcoholLog(Long id, String title, String description, LocalDateTime recordedAt, User by) {
        AlcoholComplianceLog log = new AlcoholComplianceLog();
        log.setId(id);
        log.setTitle(title);
        log.setDescription(description);
        log.setRecordedAt(recordedAt);
        log.setRecordedBy(by);
        return log;
    }

    private User user(String firstName, String lastName, String username, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
