package edu.ntnu.idi.idatt2105.backend.core.compliance.inspection.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InspectionStatsDTO {

    // Deviation stats
    private long deviationTotal;
    private long deviationOpen;
    private long deviationInProgress;
    private long deviationResolved;
    private long deviationCritical;
    private long deviationHigh;
    private long deviationMedium;
    private long deviationLow;

    // Temperature log stats
    private long temperatureTotal;
    private long temperatureOk;
    private long temperatureWarning;
    private long temperatureCritical;

    // Alcohol log stats
    private long alcoholTotal;
    private long alcoholOk;
    private long alcoholWarning;
    private long alcoholCritical;
}
