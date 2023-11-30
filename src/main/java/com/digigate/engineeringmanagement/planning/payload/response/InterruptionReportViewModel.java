package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Interruption Report ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterruptionReportViewModel {
    private String locationName;
    private Double dir;
    private LocalDate date;
    private String aircraftName;
    private String defect;
    private String rectification;
    private Double duration;
}
