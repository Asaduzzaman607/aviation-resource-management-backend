package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 *Daily Utilization Report List Dto
 *
 * @author Nafiul Islam
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyUtilizationReportList {
    List<DailyUtilizationReport> dailyUtilizationReport;
    private Double totalFH;
    private Integer totalFC;
    private Double totalTat;
    private Integer totalTac;
    private LocalDate asOfDate;
    private String aircraftName;
    private String aircraftSerial;
}
