package edu.ntnu.idi.idatt2105.backend.core.document.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt2105.backend.core.document.entity.Document;
import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DocumentRepositoryTest {

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  void searchDocumentsFiltersByTenantAreaTextAndTags() {
    Tenant tenant1 = persistTenant("Docs Tenant A", "920000001");
    Tenant tenant2 = persistTenant("Docs Tenant B", "920000002");

    User uploader1 = persistUser(tenant1, "tenantA@example.com", "tenantA-user");
    User uploader2 = persistUser(tenant2, "tenantB@example.com", "tenantB-user");

    Document matching = persistDocument(
        tenant1,
        uploader1,
        DocumentArea.IK_FOOD,
        "Fridge Manual",
        "Cleaning and monitoring",
        List.of("haccp", "kitchen"),
        LocalDateTime.now().minusHours(1));

    persistDocument(
        tenant1,
        uploader1,
        DocumentArea.GENERAL,
        "Office Guide",
        "Unrelated",
        List.of("office"),
        LocalDateTime.now().minusMinutes(20));

    persistDocument(
        tenant2,
        uploader2,
        DocumentArea.IK_FOOD,
        "Fridge Manual",
        "Other tenant",
        List.of("haccp"),
        LocalDateTime.now());

    List<Document> result = documentRepository.searchDocuments(
        tenant1.getId(),
        DocumentArea.IK_FOOD,
        "%manual%",
        false,
        List.of("haccp"));

    assertEquals(1, result.size());
    assertEquals(matching.getId(), result.getFirst().getId());
  }

  @Test
  void findByIdAndTenantIdReturnsOnlyMatchingTenantDocument() {
    Tenant tenant1 = persistTenant("Docs Tenant C", "920000003");
    Tenant tenant2 = persistTenant("Docs Tenant D", "920000004");

    User uploader1 = persistUser(tenant1, "tenantC@example.com", "tenantC-user");
    User uploader2 = persistUser(tenant2, "tenantD@example.com", "tenantD-user");

    Document doc = persistDocument(
        tenant1,
        uploader1,
        DocumentArea.GENERAL,
        "Tenant scoped doc",
        "Description",
        List.of("policy"),
        LocalDateTime.now());

    persistDocument(
        tenant2,
        uploader2,
        DocumentArea.GENERAL,
        "Other tenant doc",
        "Description",
        List.of("policy"),
        LocalDateTime.now().minusMinutes(1));

    assertTrue(documentRepository.findByIdAndTenantId(doc.getId(), tenant1.getId()).isPresent());
    assertTrue(documentRepository.findByIdAndTenantId(doc.getId(), tenant2.getId()).isEmpty());
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

  private User persistUser(Tenant tenant, String email, String username) {
    User user = new User();
    user.setTenant(tenant);
    user.setFirstName("Test");
    user.setLastName("User");
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword("encoded");
    user.setRole(UserRole.ADMIN);
    user.setActive(true);

    entityManager.persist(user);
    entityManager.flush();
    return user;
  }

  private Document persistDocument(
      Tenant tenant,
      User uploadedBy,
      DocumentArea area,
      String title,
      String description,
      List<String> tags,
      LocalDateTime updatedAt) {

    Document document = new Document();
    document.setTenant(tenant);
    document.setUploadedBy(uploadedBy);
    document.setArea(area);
    document.setTitle(title);
    document.setDescription(description);
    document.setOriginalFilename(title.replace(" ", "_") + ".pdf");
    document.setMimeType("application/pdf");
    document.setSizeBytes(1024L);
    document.setTags(new LinkedHashSet<>(tags));
    document.setCreatedAt(updatedAt.minusMinutes(10));
    document.setUpdatedAt(updatedAt);

    entityManager.persist(document);
    entityManager.flush();
    return document;
  }
}

