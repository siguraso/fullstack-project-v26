package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistItemTemplate;

public interface ChecklistItemTemplateRepository extends JpaRepository<ChecklistItemTemplate, Long> {
    ChecklistItemTemplate findByChecklistTemplate_Id(Long id);
}
