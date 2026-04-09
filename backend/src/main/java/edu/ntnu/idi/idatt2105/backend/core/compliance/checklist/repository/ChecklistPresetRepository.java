package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistPreset;

/**
 * Repository for global checklist preset items.
 *
 * <p>Presets are ordered by {@code tab}, then {@code sortOrder} so the frontend
 * receives items in the correct display sequence without needing client-side sorting.
 */
public interface ChecklistPresetRepository extends JpaRepository<ChecklistPreset, Long> {

    /**
     * Returns all presets ordered for display: grouped by tab (alphabetically) then by
     * sort order within each group.
     */
    List<ChecklistPreset> findAllByOrderByTabAscSortOrderAsc();
}
