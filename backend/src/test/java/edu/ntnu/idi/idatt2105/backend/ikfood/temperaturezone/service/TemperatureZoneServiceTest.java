package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.CreateTemperatureZoneRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.dto.TemperatureZoneDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.entity.TemperatureZone;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.mapper.TemperatureZoneMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturezone.repository.TemperatureZoneRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemperatureZoneServiceTest {

  @Mock
  private TemperatureZoneRepository temperatureZoneRepository;

  @Mock
  private TenantRepository tenantRepository;

  @Mock
  private TemperatureZoneMapper temperatureZoneMapper;

  @InjectMocks
  private TemperatureZoneService temperatureZoneService;

  @BeforeEach
  void setUpTenantContext() {
    TenantContext.setCurrentOrg(1L);
  }

  @AfterEach
  void clearTenantContext() {
    TenantContext.clear();
  }

  @Test
  void createTrimsNameAndSavesActiveZone() {
    Tenant tenant = new Tenant();
    tenant.setId(1L);

    CreateTemperatureZoneRequest request = new CreateTemperatureZoneRequest();
    request.setName("  Chiller  ");
    request.setLowerLimitCelsius(2.0);
    request.setUpperLimitCelsius(6.0);

    TemperatureZoneDTO mappedDto = new TemperatureZoneDTO();
    mappedDto.setName("Chiller");
    mappedDto.setActive(true);

    when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
    when(temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(1L, "Chiller")).thenReturn(false);
    when(temperatureZoneRepository.save(any(TemperatureZone.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(temperatureZoneMapper.toDTO(any(TemperatureZone.class))).thenReturn(mappedDto);

    TemperatureZoneDTO result = temperatureZoneService.create(request);

    assertEquals("Chiller", result.getName());
    assertTrue(result.isActive());

    ArgumentCaptor<TemperatureZone> captor = ArgumentCaptor.forClass(TemperatureZone.class);
    verify(temperatureZoneRepository).save(captor.capture());
    TemperatureZone saved = captor.getValue();
    assertEquals("Chiller", saved.getName());
    assertEquals(2.0, saved.getLowerLimitCelsius());
    assertEquals(6.0, saved.getUpperLimitCelsius());
    assertTrue(saved.isActive());
    assertEquals(tenant, saved.getTenant());
  }

  @Test
  void createThrowsValidationExceptionWhenNameAlreadyExistsForTenant() {
    Tenant tenant = new Tenant();
    tenant.setId(1L);

    CreateTemperatureZoneRequest request = new CreateTemperatureZoneRequest();
    request.setName("Freezer");
    request.setLowerLimitCelsius(-20.0);
    request.setUpperLimitCelsius(-5.0);

    when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
    when(temperatureZoneRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(1L, "Freezer")).thenReturn(true);

    ValidationException exception = assertThrows(ValidationException.class, () -> temperatureZoneService.create(request));

    assertEquals("Temperature zone already exists: Freezer", exception.getMessage());
  }

  @Test
  void updateThrowsValidationExceptionWhenLimitsAreInvalid() {
    CreateTemperatureZoneRequest request = new CreateTemperatureZoneRequest();
    request.setName("Warm shelf");
    request.setLowerLimitCelsius(10.0);
    request.setUpperLimitCelsius(10.0);

    ValidationException exception = assertThrows(ValidationException.class, () -> temperatureZoneService.update(44L, request));

    assertEquals("Lower temperature limit must be less than upper limit", exception.getMessage());
  }

  @Test
  void updateThrowsResourceNotFoundWhenZoneIsMissingForTenant() {
    CreateTemperatureZoneRequest request = new CreateTemperatureZoneRequest();
    request.setName("Cold room");
    request.setLowerLimitCelsius(0.0);
    request.setUpperLimitCelsius(4.0);

    when(temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(99L, 1L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> temperatureZoneService.update(99L, request));

    assertEquals("Temperature zone not found", exception.getMessage());
  }

  @Test
  void deleteSoftDeletesExistingZone() {
    TemperatureZone zone = new TemperatureZone();
    zone.setId(5L);
    zone.setActive(true);

    when(temperatureZoneRepository.findByIdAndTenantIdAndActiveTrue(5L, 1L)).thenReturn(Optional.of(zone));

    temperatureZoneService.delete(5L);

    assertTrue(!zone.isActive());
    verify(temperatureZoneRepository).save(zone);
  }
}

