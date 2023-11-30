package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkPackageSummaryReportViewModel {
    private String aircraftName;
    private String aircraftModelName;
    private LocalDate asOn;
    private LocalDate inputDate;
    private Double acHours;
    private Integer acCycles;
    private Double dueAt;
    private String woNo;
    private LocalDate woDate;
    private String checkNo;
    private Integer noOfTaskCards;
    private Integer categoryB1;
    private Integer categoryB2;
    private LocalDate asOfDate;
}
