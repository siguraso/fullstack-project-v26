package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;

public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, Long> {
}
