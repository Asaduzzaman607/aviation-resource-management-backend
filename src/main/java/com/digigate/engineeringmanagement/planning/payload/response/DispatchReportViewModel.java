package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dispatch Report View Model
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DispatchReportViewModel {

    private Integer month;
    private Integer year;
    private Integer delay;
    private Integer initialCancellation;
    private Integer totalCancellation;
    private Integer scheduledDep;
    private Double dispatchReliability;
    private Double scheduleCompletionRate;
}
