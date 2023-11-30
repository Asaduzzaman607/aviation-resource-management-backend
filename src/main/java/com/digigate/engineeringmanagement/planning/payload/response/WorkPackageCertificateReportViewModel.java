package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkPackageCertificateReportViewModel {
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
    private List<JobCardsViewModel> jobCardsViewModels;
    private LocalDate asOfDate;
}
