package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.CreateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.DeviationDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.dto.UpdateDeviationRequest;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.mapper.DeviationMapper;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository.DeviationRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.entity.BaseComplianceLog;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DeviationServiceTest {

	@Mock
	private DeviationRepository deviationRepo;

	@Mock
	private TenantRepository tenantRepo;

	@Mock
	private UserRepository userRepository;

	@Mock
	private DeviationMapper mapper;

	@InjectMocks
	private DeviationService deviationService;

	@BeforeEach
	void setUpTenantContext() {
		TenantContext.setCurrentOrg(1L);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("reporter@test.no", "N/A"));
	}

	@AfterEach
	void clearTenantContext() {
		TenantContext.clear();
		SecurityContextHolder.clearContext();
	}

	@Test
	void testCreateSuccess() {
		CreateDeviationRequest request = new CreateDeviationRequest();
		request.setModule(ComplianceModule.IK_FOOD);
		request.setTitle("Test deviation");
		request.setReportedDate(LocalDate.of(2026, 4, 9));
		request.setDiscoveredBy("Alice");
		request.setReportedTo("Manager");
		request.setAssignedTo("Chef");
		request.setIssueDescription("Description");
		request.setImmediateAction("Action");
		request.setRootCause("Cause");
		request.setCorrectiveAction("Correction");
		request.setCompletionNotes("Done");
		request.setSeverity(DeviationSeverity.CRITICAL);
		request.setCategory(DeviationCategory.HYGIENE);
		request.setStatus(DeviationStatus.OPEN);

		Tenant tenant = new Tenant();
		tenant.setId(1L);

		User reporter = new User();
		reporter.setId(55L);
		reporter.setTenant(tenant);

		DeviationDTO mappedDto = new DeviationDTO();
		mappedDto.setTitle("Test deviation");

		when(tenantRepo.findById(1L)).thenReturn(Optional.of(tenant));
		when(userRepository.findByEmail("reporter@test.no")).thenReturn(Optional.of(reporter));
		when(deviationRepo.save(any(Deviation.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(mapper.toDTO(any(Deviation.class))).thenReturn(mappedDto);

		DeviationDTO result = deviationService.create(request);

		assertNotNull(result);
		assertEquals("Test deviation", result.getTitle());

		ArgumentCaptor<Deviation> captor = ArgumentCaptor.forClass(Deviation.class);
		verify(deviationRepo).save(captor.capture());
		Deviation saved = captor.getValue();

		assertEquals(tenant, saved.getTenant());
		assertEquals(ComplianceModule.IK_FOOD, saved.getModule());
		assertEquals("Test deviation", saved.getTitle());
		assertEquals(LocalDate.of(2026, 4, 9), saved.getReportedDate());
		assertEquals("Alice", saved.getDiscoveredBy());
		assertEquals("Manager", saved.getReportedTo());
		assertEquals("Chef", saved.getAssignedTo());
		assertEquals("Description", saved.getIssueDescription());
		assertEquals("Action", saved.getImmediateAction());
		assertEquals("Cause", saved.getRootCause());
		assertEquals("Correction", saved.getCorrectiveAction());
		assertEquals("Done", saved.getCompletionNotes());
		assertEquals(DeviationSeverity.CRITICAL, saved.getSeverity());
		assertEquals(DeviationCategory.HYGIENE, saved.getCategory());
		assertEquals(DeviationStatus.OPEN, saved.getStatus());
		assertEquals(reporter, saved.getCreatedBy());
		assertNull(saved.getLogId());
		assertNotNull(saved.getCreatedAt());
		assertNull(saved.getResolvedAt());
	}

	@Test
	void testCreateSetsLogLinkAndCreatedBy() {
		CreateDeviationRequest request = new CreateDeviationRequest();
		request.setModule(ComplianceModule.IK_FOOD);
		request.setTitle("Linked deviation");
		request.setIssueDescription("Description");
		request.setSeverity(DeviationSeverity.HIGH);
		request.setCategory(DeviationCategory.TEMPERATURE);
		request.setStatus(DeviationStatus.OPEN);
		request.setLogId(34L);

		Tenant tenant = new Tenant();
		tenant.setId(1L);

		User reporter = new User();
		reporter.setId(55L);
		reporter.setTenant(tenant);

		when(tenantRepo.findById(1L)).thenReturn(Optional.of(tenant));
		when(userRepository.findByEmail("reporter@test.no")).thenReturn(Optional.of(reporter));
		when(deviationRepo.save(any(Deviation.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(mapper.toDTO(any(Deviation.class))).thenReturn(new DeviationDTO());

		deviationService.create(request);

		ArgumentCaptor<Deviation> captor = ArgumentCaptor.forClass(Deviation.class);
		verify(deviationRepo).save(captor.capture());
		Deviation saved = captor.getValue();

		assertEquals(reporter, saved.getCreatedBy());
		assertEquals(34L, saved.getLogId());
	}

	@Test
	void testCreateThrowsWhenTenantNotFound() {
		CreateDeviationRequest request = new CreateDeviationRequest();

		when(tenantRepo.findById(1L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> deviationService.create(request));

		assertEquals("Tenant not found", exception.getMessage());
	}

	@Test
	void testGetForCurrentTenantReturnsMappedList() {
		Long tenantId = 1L;

		Deviation deviation1 = new Deviation();
		Deviation deviation2 = new Deviation();

		DeviationDTO dto1 = new DeviationDTO();
		dto1.setTitle("Deviation 1");
		DeviationDTO dto2 = new DeviationDTO();
		dto2.setTitle("Deviation 2");

		when(deviationRepo.findByTenantId(tenantId)).thenReturn(List.of(deviation1, deviation2));
		when(mapper.toDTO(any(Deviation.class))).thenReturn(dto1, dto2);

		List<DeviationDTO> result = deviationService.getForCurrentTenant();

		assertEquals(2, result.size());
		assertEquals("Deviation 1", result.get(0).getTitle());
		assertEquals("Deviation 2", result.get(1).getTitle());
		verify(deviationRepo).findByTenantId(tenantId);
	}

	@Test
	void testUpdateResolvedSetsStatusAndResolvedAt() {
		Long deviationId = 10L;

		Tenant tenant = new Tenant();
		tenant.setId(1L);

		Deviation existing = new Deviation();
		existing.setTenant(tenant);
		existing.setStatus(DeviationStatus.OPEN);

		UpdateDeviationRequest request = new UpdateDeviationRequest();
		request.setStatus(DeviationStatus.RESOLVED);

		DeviationDTO mappedDto = new DeviationDTO();
		mappedDto.setStatus(DeviationStatus.RESOLVED.name());

		when(deviationRepo.findById(deviationId)).thenReturn(Optional.of(existing));
		when(deviationRepo.save(any(Deviation.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(mapper.toDTO(any(Deviation.class))).thenReturn(mappedDto);

		DeviationDTO result = deviationService.update(deviationId, request);

		assertNotNull(result);
		assertEquals(DeviationStatus.RESOLVED.name(), result.getStatus());

		ArgumentCaptor<Deviation> captor = ArgumentCaptor.forClass(Deviation.class);
		verify(deviationRepo).save(captor.capture());
		Deviation saved = captor.getValue();

		assertEquals(DeviationStatus.RESOLVED, saved.getStatus());
		assertNotNull(saved.getResolvedAt());
	}

	@Test
	void testUpdateThrowsWhenDeviationBelongsToAnotherTenant() {
		Long deviationId = 11L;

		Tenant otherTenant = new Tenant();
		otherTenant.setId(2L);

		Deviation existing = new Deviation();
		existing.setTenant(otherTenant);
		existing.setStatus(DeviationStatus.OPEN);

		UpdateDeviationRequest request = new UpdateDeviationRequest();
		request.setStatus(DeviationStatus.RESOLVED);

		when(deviationRepo.findById(deviationId)).thenReturn(Optional.of(existing));

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> deviationService.update(deviationId, request));

		assertEquals("Deviation does not belong to current organization", exception.getMessage());
	}

	@Test
	void testUpdateThrowsWhenDeviationNotFound() {
		Long deviationId = 99L;
		UpdateDeviationRequest request = new UpdateDeviationRequest();
		request.setStatus(DeviationStatus.OPEN);

		when(deviationRepo.findById(deviationId)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> deviationService.update(deviationId, request));

		assertEquals("Deviation not found", exception.getMessage());
	}

	@Test
	void testCreateFromLogBuildsExpectedDeviation() {
		Tenant tenant = new Tenant();
		tenant.setId(2L);

		User recordedBy = new User();
		recordedBy.setId(44L);

		BaseComplianceLog log = new BaseComplianceLog() {
		};
		log.setId(321L);
		log.setTenant(tenant);
		log.setRecordedBy(recordedBy);
		log.setModule(ComplianceModule.IK_ALCOHOL);
		log.setTitle("Missing alcohol report");
		log.setDescription(null);

		when(deviationRepo.save(any(Deviation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		deviationService.createFromLog(log);

		ArgumentCaptor<Deviation> captor = ArgumentCaptor.forClass(Deviation.class);
		verify(deviationRepo).save(captor.capture());
		Deviation saved = captor.getValue();

		assertEquals(tenant, saved.getTenant());
		assertEquals(ComplianceModule.IK_ALCOHOL, saved.getModule());
		assertEquals("Critical log detected", saved.getTitle());
		assertEquals("Missing alcohol report", saved.getIssueDescription());
		assertNotNull(saved.getReportedDate());
		assertEquals(DeviationSeverity.CRITICAL, saved.getSeverity());
		assertEquals(DeviationCategory.ALCOHOL, saved.getCategory());
		assertEquals(DeviationStatus.OPEN, saved.getStatus());
		assertEquals(recordedBy, saved.getCreatedBy());
		assertEquals(321L, saved.getLogId());
		assertNotNull(saved.getCreatedAt());
	}

}
