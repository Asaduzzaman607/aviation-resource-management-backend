package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PropellerReportQueryViewModel {
    private String partDesc;
    private String partNo;
    private String serialNo;
    private LocalDate doneDate;
    private Double aircraftInHour;
    private Double tsnHour;
    private Double tsoHour;
    private LocalDate dueDate;
    private Double dueHour;
    private Double remainingHour;
    private LocalDate estimatedDueDate;
    private Boolean isTsnAvailable;
    private Boolean isOverhauled;
}
