package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.ChecklistItemLibrary;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "checklist_item_templates")
public class ChecklistItemTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ChecklistTemplate checklistTemplate;

    private String title;

    private String description;

    private int sortOrder;

    @ManyToOne
    @JoinColumn(name = "library_item_id")
    private ChecklistItemLibrary libraryItem;
}