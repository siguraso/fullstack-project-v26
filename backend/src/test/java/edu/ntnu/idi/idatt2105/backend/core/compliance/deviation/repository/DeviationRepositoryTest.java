package edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.entity.Deviation;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationCategory;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationSeverity;
import edu.ntnu.idi.idatt2105.backend.core.compliance.deviation.enums.DeviationStatus;
import edu.ntnu.idi.idatt2105.backend.core.compliance.log.enums.ComplianceModule;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class DeviationRepositoryTest {

    @Autowired
    private DeviationRepository deviationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByTenantIdReturnsOnlyMatchingTenant() {
        Tenant tenant1 = persistTenant("RepoTenant1", "990000001");
        Tenant tenant2 = persistTenant("RepoTenant2", "990000002");

        persistDeviation(tenant1, DeviationStatus.OPEN, DeviationSeverity.MEDIUM, LocalDateTime.now().minusHours(2));
        persistDeviation(tenant1, DeviationStatus.RESOLVED, DeviationSeverity.CRITICAL, LocalDateTime.now().minusHours(1));
        persistDeviation(tenant2, DeviationStatus.OPEN, DeviationSeverity.LOW, LocalDateTime.now());

        List<Deviation> result = deviationRepository.findByTenantId(tenant1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d.getTenant().getId().equals(tenant1.getId())));
    }

    @Test
    void findByTenantIdAndStatusInOrderByCreatedAtDescSortsAndFilters() {
        Tenant tenant = persistTenant("RepoTenant3", "990000003");

        Deviation newest = persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.CRITICAL,
                LocalDateTime.now().minusMinutes(10));
        Deviation oldest = persistDeviation(tenant, DeviationStatus.RESOLVED, DeviationSeverity.MEDIUM,
                LocalDateTime.now().minusHours(2));
        persistDeviation(tenant, DeviationStatus.IN_PROGRESS, DeviationSeverity.LOW, LocalDateTime.now().minusMinutes(30));

        List<Deviation> result = deviationRepository.findByTenantIdAndStatusInOrderByCreatedAtDesc(
                tenant.getId(),
                List.of(DeviationStatus.OPEN, DeviationStatus.RESOLVED));

        assertEquals(2, result.size());
        assertEquals(newest.getId(), result.get(0).getId());
        assertEquals(oldest.getId(), result.get(1).getId());
    }

    @Test
    void findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDescFiltersBySeverity() {
        Tenant tenant = persistTenant("RepoTenant4", "990000004");

        Deviation newestCritical = persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.CRITICAL,
                LocalDateTime.now().minusMinutes(15));
        Deviation oldestCritical = persistDeviation(tenant, DeviationStatus.RESOLVED, DeviationSeverity.CRITICAL,
                LocalDateTime.now().minusHours(1));
        persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.MEDIUM, LocalDateTime.now().minusMinutes(5));

        List<Deviation> result = deviationRepository.findByTenantIdAndStatusInAndSeverityOrderByCreatedAtDesc(
                tenant.getId(),
                List.of(DeviationStatus.OPEN, DeviationStatus.RESOLVED),
                DeviationSeverity.CRITICAL);

        assertEquals(2, result.size());
        assertEquals(newestCritical.getId(), result.get(0).getId());
        assertEquals(oldestCritical.getId(), result.get(1).getId());
        assertTrue(result.stream().allMatch(d -> d.getSeverity() == DeviationSeverity.CRITICAL));
    }

    @Test
    void findByTenantIdWithPageableReturnsRequestedPage() {
        Tenant tenant = persistTenant("RepoTenant5", "990000005");

        persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.LOW, LocalDateTime.now().minusDays(3));
        persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.MEDIUM, LocalDateTime.now().minusDays(2));
        persistDeviation(tenant, DeviationStatus.OPEN, DeviationSeverity.HIGH, LocalDateTime.now().minusDays(1));

        Pageable pageable = PageRequest.of(0, 2);
        Page<Deviation> page = deviationRepository.findByTenantId(tenant.getId(), pageable);

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
    }

    private Tenant persistTenant(String name, String orgNumber) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setOrgNumber(orgNumber);
        tenant.setCountry("Norway");
        tenant.setActive(true);

        entityManager.persist(tenant);
        entityManager.flush();
        return tenant;
    }

    private Deviation persistDeviation(
            Tenant tenant,
            DeviationStatus status,
            DeviationSeverity severity,
            LocalDateTime createdAt) {

        Deviation deviation = new Deviation();
        deviation.setTenant(tenant);
        deviation.setModule(ComplianceModule.IK_FOOD);
        deviation.setTitle("Deviation " + createdAt.toString());
        deviation.setDescription("Test deviation");
        deviation.setStatus(status);
        deviation.setSeverity(severity);
        deviation.setCategory(DeviationCategory.HYGIENE);
        deviation.setCreatedAt(createdAt);

        entityManager.persist(deviation);
        entityManager.flush();
        return deviation;
    }
}
