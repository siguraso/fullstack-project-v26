package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.instance;

import java.time.LocalDate;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistItemTemplate;
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
@Table(name = "checklist_item_instances")
public class ChecklistItemInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChecklistInstance checklist;

    private boolean completed;

    private String comment;

    private LocalDate completedAt;

    @ManyToOne
    @JoinColumn(name = "template_item_id")
    private ChecklistItemTemplate templateItem;
}
