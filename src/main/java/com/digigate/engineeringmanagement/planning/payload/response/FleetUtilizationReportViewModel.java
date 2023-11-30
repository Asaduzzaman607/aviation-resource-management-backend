package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FleetUtilizationReportViewModel {

    private Integer month;
    private Integer year;
    private Long numOfAcFleet;
    private Double monthlyHours;
    private Integer monthlyCycles;
    private Double monthlyAvgFltUtilHours;
    private Double monthlyAvgFltUtilCycles;
    private Double dailyAvgFltUtilHours;
    private Double dailyAvgFltUtilCycles;

}
