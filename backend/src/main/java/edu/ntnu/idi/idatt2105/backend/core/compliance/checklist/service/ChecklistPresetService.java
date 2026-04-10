package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto.ChecklistPresetDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistPreset;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistPresetRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service for retrieving global checklist preset items.
 *
 * <p>
 * Presets are read-only reference data seeded from Mattilsynet IK requirements.
 * They are not tenant-scoped — all authenticated users see the same catalogue.
 */
@Service
@RequiredArgsConstructor
public class ChecklistPresetService {

    private final ChecklistPresetRepository repo;

    /**
     * Returns all presets ordered by tab and sort order.
     * The frontend groups the flat list by {@code tab} and then by
     * {@code groupLabel}.
     *
     * @return ordered list of preset DTOs
     */
    public List<ChecklistPresetDTO> getAll() {
        return repo.findAllByOrderByTabAscSortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ChecklistPresetDTO toDto(ChecklistPreset entity) {
        ChecklistPresetDTO dto = new ChecklistPresetDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setPriority(entity.getPriority());
        dto.setTab(entity.getTab());
        dto.setGroupLabel(entity.getGroupLabel());
        dto.setSortOrder(entity.getSortOrder());
        return dto;
    }
}
