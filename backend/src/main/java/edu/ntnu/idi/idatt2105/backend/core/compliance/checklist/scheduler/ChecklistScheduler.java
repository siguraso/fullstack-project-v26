package edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.entity.template.ChecklistTemplate;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistInstanceRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.repository.ChecklistTemplateRepository;
import edu.ntnu.idi.idatt2105.backend.core.compliance.checklist.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled job that generates daily checklist instances for all active
 * templates.
 * <p>
 * Runs every day at 06:00 and creates an instance from each active template
 * whose frequency matches the current day (daily, weekly on Monday, monthly
 * on the first of the month), provided no instance already exists for today.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistScheduler {

    private final ChecklistTemplateRepository templateRepo;
    private final ChecklistInstanceRepository instanceRepo;
    private final ChecklistService checklistService;

    /**
     * Scans all active checklist templates and generates instances for those
     * whose frequency is due today. Skips templates that already have an
     * instance for the current date.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void generateChecklists() {
        log.info("Starting scheduled checklist generation");

        List<ChecklistTemplate> templates = templateRepo.findAll();
        int generatedCount = 0;

        for (ChecklistTemplate template : templates) {

            boolean shouldGenerate = switch (template.getFrequency()) {
                case DAILY -> true;
                case WEEKLY -> LocalDate.now().getDayOfWeek() == DayOfWeek.MONDAY;
                case MONTHLY -> LocalDate.now().getDayOfMonth() == 1;
            };

            if (!shouldGenerate || !template.isActive())
                continue;

            boolean exists = instanceRepo
                    .findByTenantIdAndDate(template.getTenant().getId(), LocalDate.now())
                    .stream()
                    .anyMatch(i -> i.getTemplate().getId().equals(template.getId()));

            if (!exists) {
                checklistService.generateInstance(template.getId());
                generatedCount++;
            }
        }

        log.info("Completed scheduled checklist generation: templatesScanned={} instancesGenerated={}",
                templates.size(), generatedCount);
    }
}