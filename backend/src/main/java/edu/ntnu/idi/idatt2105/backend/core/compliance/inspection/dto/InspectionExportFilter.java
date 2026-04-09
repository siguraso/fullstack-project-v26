package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class InspectionExportFilter {

    /** Which log types to include. Null/empty = include all. */
    private List<String> types; // DEVIATION, TEMPERATURE, ALCOHOL

    /** Deviation severities to include. Null/empty = include all. */
    private List<String> severities; // CRITICAL, HIGH, MEDIUM, LOW

    /** Deviation/log statuses to include. Null/empty = include all. */
    private List<String> statuses; // OPEN, IN_PROGRESS, RESOLVED, OK, WARNING, CRITICAL

    /** Inclusive lower date bound (recorded/created date). */
    private LocalDate dateFrom;

    /** Inclusive upper date bound (recorded/created date). */
    private LocalDate dateTo;
}
