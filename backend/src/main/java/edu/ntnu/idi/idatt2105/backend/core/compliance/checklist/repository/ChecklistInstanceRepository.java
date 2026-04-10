package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistInstance;

public interface ChecklistInstanceRepository extends JpaRepository<ChecklistInstance, Long> {

    @Query("""
                SELECT DISTINCT c FROM ChecklistInstance c
                LEFT JOIN FETCH c.items i
                LEFT JOIN FETCH i.templateItem
                WHERE c.tenant.id = :tenantId AND c.date = :date
            """)
    List<ChecklistInstance> findTodayWithItems(Long tenantId, LocalDate date);

    List<ChecklistInstance> findByTenantIdAndDate(Long tenantId, LocalDate date);

    @Query("""
                SELECT c FROM ChecklistInstance c
                JOIN FETCH c.items i
                JOIN FETCH i.templateItem
                WHERE c.id = :id
            """)
    Optional<ChecklistInstance> findByIdWithItems(Long id);

    void deleteByTemplate_id(Long templateId);
}
