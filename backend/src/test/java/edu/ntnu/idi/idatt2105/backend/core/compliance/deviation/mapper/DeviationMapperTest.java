package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;

class DeviationMapperTest {

  Deviation entity;

  @BeforeEach
  void setUp() {
    entity = new Deviation();
    entity.setId(1L);
    entity.setTitle("Giant rat infestation");
    entity.setReportedDate(LocalDate.of(2024, 1, 1));
    entity.setDiscoveredBy("John");
    entity.setReportedTo("Manager");
    entity.setAssignedTo("Kitchen lead");
    entity.setIssueDescription("There are giant rats in the storage room.");
    entity.setImmediateAction("Set traps and isolate area.");
    entity.setRootCause("Door left open overnight.");
    entity.setCorrectiveAction("Install automatic door closer.");
    entity.setCompletionNotes("Monitoring ongoing.");
    entity.setStatus(DeviationStatus.IN_PROGRESS);
    entity.setSeverity(DeviationSeverity.CRITICAL);
    entity.setCategory(DeviationCategory.HYGIENE);
    entity.setModule(ComplianceModule.IK_ALCOHOL);
    entity.setLogId(20L);
    entity.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    entity.setResolvedAt(LocalDateTime.of(2024, 1, 2, 12, 0));
  }

  @Test
  void testToDTO() {  
    DeviationMapper mapper = new DeviationMapper();

    DeviationDTO dto = mapper.toDTO(entity);

    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals("Giant rat infestation", dto.getTitle());
    assertEquals("2024-01-01", dto.getReportedDate());
    assertEquals("John", dto.getDiscoveredBy());
    assertEquals("Manager", dto.getReportedTo());
    assertEquals("Kitchen lead", dto.getAssignedTo());
    assertEquals("There are giant rats in the storage room.", dto.getIssueDescription());
    assertEquals("Set traps and isolate area.", dto.getImmediateAction());
    assertEquals("Door left open overnight.", dto.getRootCause());
    assertEquals("Install automatic door closer.", dto.getCorrectiveAction());
    assertEquals("Monitoring ongoing.", dto.getCompletionNotes());
    assertEquals(DeviationStatus.IN_PROGRESS.name(), dto.getStatus());
    assertEquals(DeviationSeverity.CRITICAL.name(), dto.getSeverity());
    assertEquals(DeviationCategory.HYGIENE.name(), dto.getCategory());
    assertEquals(ComplianceModule.IK_ALCOHOL, dto.getModule());
    assertEquals(20L, dto.getLogId());
    assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0).toString(), dto.getCreatedAt());
    assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0).toString(), dto.getResolvedAt());
  }

  @Test
  void testToDTONullResolvedAt() {
    entity.setResolvedAt(null);

    DeviationMapper mapper = new DeviationMapper();

    DeviationDTO dto = mapper.toDTO(entity);

    assertNotNull(dto);
    assertNull(dto.getResolvedAt());
  }
}