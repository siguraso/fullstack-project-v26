package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper;

import java.time.LocalDateTime;

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
    entity.setDescription("There are giant rats in the storage room.");
    entity.setStatus(DeviationStatus.IN_PROGRESS);
    entity.setSeverity(DeviationSeverity.CRITICAL);
    entity.setCategory(DeviationCategory.HYGIENE);
    entity.setModule(ComplianceModule.IK_ALCOHOL);
    entity.setChecklistItemId(10L);
    entity.setLogId(20L);
    entity.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    entity.setResolvedAt(LocalDateTime.of(2024, 1, 2, 12, 0));
  }

  @Test
  void testToDTO() {  
    DeviationMapper mapper = new DeviationMapper();

    DeviationDTO dto = mapper.toDTO(entity);

    assertNotNull(dto);
    assertEquals(dto.getId(), 1L);
    assertEquals(dto.getTitle(), "Giant rat infestation");
    assertEquals(dto.getDescription(), "There are giant rats in the storage room.");
    assertEquals(dto.getStatus(), DeviationStatus.IN_PROGRESS.name());
    assertEquals(dto.getSeverity(), DeviationSeverity.CRITICAL.name());
    assertEquals(dto.getCategory(), DeviationCategory.HYGIENE.name());
    assertEquals(dto.getModule(), ComplianceModule.IK_ALCOHOL);
    assertEquals(dto.getChecklistItemId(), 10L);
    assertEquals(dto.getLogId(), 20L);
    assertEquals(dto.getCreatedAt(), LocalDateTime.of(2024, 1, 1, 12, 0).toString());
    assertEquals(dto.getResolvedAt(), LocalDateTime.of(2024, 1, 2, 12, 0).toString());
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