package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ChecklistInstanceDTO {

    private Long id;
    private String status;
    private String name;
    private LocalDate date;

    private List<ChecklistItemDTO> items;
}
