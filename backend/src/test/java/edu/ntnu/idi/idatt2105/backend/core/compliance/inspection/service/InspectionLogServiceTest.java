package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto.InspectionLogDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.LogStatus;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.entity.AlcoholComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikalcohol.log.repository.AlcoholLogRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.entity.TemperatureComplianceLog;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.repository.TemperatureLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InspectionLogServiceTest {

  @Mock
  private DeviationRepository deviationRepository;

  @Mock
  private TemperatureLogRepository temperatureLogRepository;

  @Mock
  private AlcoholLogRepository alcoholLogRepository;

  @InjectMocks
  private InspectionLogService inspectionLogService;

  @BeforeEach
  void setUpTenantContext() {
    TenantContext.setCurrentOrg(9L);
  }

  @AfterEach
  void clearTenantContext() {
    TenantContext.clear();
  }

  @Test
  void getInspectionLogsMergesAllSourcesAndSortsDescendingByTimestamp() {
    Deviation deviation = new Deviation();
    deviation.setId(10L);
    deviation.setTitle("Missing fridge checklist");
    deviation.setIssueDescription("Temperature check skipped");
    deviation.setStatus(DeviationStatus.OPEN);
    deviation.setSeverity(DeviationSeverity.HIGH);
    deviation.setModule(ComplianceModule.IK_FOOD);
    deviation.setCreatedAt(LocalDateTime.of(2026, 4, 9, 8, 0));
    deviation.setCreatedBy(buildUser("Ada", "Lovelace"));

    TemperatureComplianceLog temperatureLog = new TemperatureComplianceLog();
    temperatureLog.setId(20L);
    temperatureLog.setTitle("Cold room reading");
    temperatureLog.setDescription("Logged 5 C");
    temperatureLog.setStatus(LogStatus.WARNING);
    temperatureLog.setModule(ComplianceModule.IK_FOOD);
    temperatureLog.setRecordedAt(LocalDateTime.of(2026, 4, 9, 9, 0));
    temperatureLog.setRecordedBy(buildUser("Grace", "Hopper"));

    AlcoholComplianceLog alcoholLog = new AlcoholComplianceLog();
    alcoholLog.setId(30L);
    alcoholLog.setTitle("ID check event");
    alcoholLog.setDescription("Minor warning");
    alcoholLog.setStatus(LogStatus.OK);
    alcoholLog.setModule(ComplianceModule.IK_ALCOHOL);
    alcoholLog.setRecordedAt(LocalDateTime.of(2026, 4, 9, 7, 30));
    alcoholLog.setRecordedBy(buildUser("Linus", "Torvalds"));

    when(deviationRepository.findByTenantId(9L)).thenReturn(List.of(deviation));
    when(temperatureLogRepository.findAllByTenantId(9L)).thenReturn(List.of(temperatureLog));
    when(alcoholLogRepository.findAllByTenantId(9L)).thenReturn(List.of(alcoholLog));

    List<InspectionLogDTO> result = inspectionLogService.getInspectionLogs();

    assertEquals(3, result.size());
    assertEquals("TEMPERATURE", result.get(0).getType());
    assertEquals("DEVIATION", result.get(1).getType());
    assertEquals("ALCOHOL", result.get(2).getType());
    assertEquals("Grace Hopper", result.get(0).getActor());
  }

  @Test
  void getInspectionLogsUsesFallbackDescriptionAndSystemActor() {
    Deviation deviation = new Deviation();
    deviation.setId(100L);
    deviation.setTitle("Unclassified deviation");
    deviation.setIssueDescription("   ");
    deviation.setImmediateAction(" ");
    deviation.setStatus(DeviationStatus.IN_PROGRESS);
    deviation.setSeverity(DeviationSeverity.MEDIUM);
    deviation.setModule(ComplianceModule.IK_FOOD);
    deviation.setCreatedAt(LocalDateTime.of(2026, 4, 9, 10, 15));
    deviation.setCreatedBy(null);

    when(deviationRepository.findByTenantId(9L)).thenReturn(List.of(deviation));
    when(temperatureLogRepository.findAllByTenantId(9L)).thenReturn(List.of());
    when(alcoholLogRepository.findAllByTenantId(9L)).thenReturn(List.of());

    List<InspectionLogDTO> result = inspectionLogService.getInspectionLogs();

    assertEquals(1, result.size());
    assertEquals("Deviation recorded", result.get(0).getDescription());
    assertEquals("System", result.get(0).getActor());
  }

  private User buildUser(String firstName, String lastName) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    return user;
  }
}

