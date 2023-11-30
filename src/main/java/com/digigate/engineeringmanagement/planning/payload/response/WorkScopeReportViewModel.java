package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkScopeReportViewModel {
    private String aircraftChecksName;
    private String aircraftModelName;
    private String aircraftName;
    private String airframeSerial;
    private Double airFrameTotalTime;
    private Integer airframeTotalCycle;
    private LocalDate manufactureDate;
    private String woNo;
    private LocalDate asOfDate;
    List<WorkScopeTaskViewModel> workScopeTaskViewModels;
    PageData pageData;
}
