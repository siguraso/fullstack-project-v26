package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChecklistTemplateDTO {

    private Long id;
    private String name;
    private String module;
    private String frequency;

    private List<ChecklistItemTemplateDTO> items;
}