package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance.ChecklistItemInstance;

public interface ChecklistItemInstanceRepository extends JpaRepository<ChecklistItemInstance, Long> {
    List<ChecklistItemInstance> findByChecklistId(Long checklistId);
}
