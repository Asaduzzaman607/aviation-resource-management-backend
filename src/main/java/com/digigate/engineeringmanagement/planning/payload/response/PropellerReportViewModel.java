package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PropellerReportViewModel {
    private String nomenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Double installationTsn;
    private Double installationTso;
    private Double currentTsn;
    private Double currentTso;
    private LocalDate dueDate;
    private Double limitFh;
    private Long remainingDay;
    private Double remainingHour;
    private LocalDate estimatedDate;
}
