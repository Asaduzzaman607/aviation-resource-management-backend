package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpStatReportViewModel {

    private Integer month;
    private Integer year;
    private Long numOfAcFleet;
    private Double numOfAcInService;
    private Double availability;
    private Double totalFlightHour;
    private Integer totalFlightCycle;
}
