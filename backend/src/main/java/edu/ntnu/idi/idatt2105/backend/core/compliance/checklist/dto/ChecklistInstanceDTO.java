package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * Data transfer object representing a concrete checklist instance for a
 * specific date and compliance module.
 * <p>
 * Contains high-level metadata such as name, status and module together with
 * the list of individual {@link ChecklistItemDTO} entries that make up the
 * checklist.
 */
@Data
public class ChecklistInstanceDTO {

    private Long id;
    private String status;
    private String name;
    private String module;
    private LocalDate date;
    private List<ChecklistItemDTO> items;
}
