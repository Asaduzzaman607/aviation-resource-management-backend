package com.digigate.engineeringmanagement.planning.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Sector-wise Utilization report Response
 *
 * @author  Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UtilizationReportResponse {
    private String aircraftName;
    private String airframeSerial;
    List<SectorWiseUtilizationReportDto> utilizationReportDtoList;
    private Double totalHours ;
    private Integer totalCycle;

    private UtilizationReportTotalView previousOneMonth;
    private UtilizationReportTotalView previousSecondMonth;
    private UtilizationReportTotalView previousThirdMonth;

    public UtilizationReportResponse(String aircraftName, String airframeSerial) {
        this.aircraftName = aircraftName;
        this.airframeSerial = airframeSerial;
    }
}

