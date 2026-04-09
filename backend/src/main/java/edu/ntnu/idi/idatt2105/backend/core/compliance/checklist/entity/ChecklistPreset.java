package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Global (non-tenant) checklist preset item seeded from Mattilsynet IK requirements.
 *
 * <p>Presets are read-only reference data. When a user selects a preset in the UI, a new
 * {@link ChecklistItemLibrary} item is created for their tenant using this data as a template.
 *
 * <p>To add more presets: insert rows into the {@code checklist_presets} table via {@code data.sql}.
 * See that file for the column mapping and existing examples.
 */
@Entity
@Data
@Table(name = "checklist_presets")
public class ChecklistPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display title shown in the picker and used as the library item title. */
    private String title;

    /** Full instruction text used as the library item description. */
    @Column(length = 1000)
    private String description;

    /**
     * Compliance module this item belongs to when added to the library.
     * Matches {@code ComplianceModule} enum values: {@code IK_FOOD} or {@code IK_ALCOHOL}.
     */
    private String category;

    /** Item priority: {@code LOW} or {@code HIGH}. */
    private String priority;

    /**
     * Display tab in the preset picker UI.
     * One of: {@code IK_FOOD}, {@code IK_ALCOHOL}, {@code HACCP}.
     * Note: {@code HACCP} items use {@code category = IK_FOOD} since HACCP is part of food safety.
     */
    private String tab;

    /**
     * Sub-group label within a tab, e.g. "Temperature Control" or "Age Verification".
     * Items with the same tab and groupLabel are rendered together in the picker.
     */
    private String groupLabel;

    /** Display order within the group. Lower values appear first. */
    private int sortOrder;
}
