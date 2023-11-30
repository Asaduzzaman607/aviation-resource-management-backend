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
public class ManHourReportViewModel {
    private String aircraftChecksName;
    private String aircraftName;
    private String airframeSerial;
    private String woNo;
    private LocalDate date;
    private List<ManHourTaskViewModel> manHourTaskViewModels;
    private Double totalManHour;
    private Double totalProposedManHour;
    private Double totalB1ProposedManHour;
    private Double totalB2ProposedManHour;
    private Integer totalB1Task;
    private Integer totalB2Task;
    PageData pageData;
}
