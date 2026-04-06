package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;

public interface ChecklistItemLibraryRepository extends JpaRepository<ChecklistItemLibrary, Long> {
    List<ChecklistItemLibrary> findByTenantId(Long tenantId);
}