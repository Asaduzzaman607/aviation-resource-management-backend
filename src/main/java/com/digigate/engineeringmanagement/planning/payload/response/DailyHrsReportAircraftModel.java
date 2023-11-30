package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Header Model for
 * Daily Flying Hours Report
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@NoArgsConstructor
public class DailyHrsReportAircraftModel {
    private Double airFrameTotalTime ;
    private Integer airframeTotalCycle ;
    private Double bdTotalTime ;
    private Integer bdTotalCycle ;
    private String aircraftName;
    private String airframeSerial;
    private Long aircraftModelId;
    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;
    private Double aCheckTimeRemainHours;
    private Long aCheckTimeRemainDays;

    public DailyHrsReportAircraftModel(String aircraftName, String airframeSerial,
                                       Double airFrameTotalTime, Integer airframeTotalCycle,
                                       Double bdTotalTime, Integer bdTotalCycle, Long aircraftModelId,
                                       Double aircraftCheckDoneHour, LocalDate aircraftCheckDoneDate) {
        this.aircraftName = aircraftName;
        this.airframeSerial = airframeSerial;
        this.airFrameTotalTime = airFrameTotalTime;
        this.airframeTotalCycle = airframeTotalCycle;
        this.bdTotalTime = bdTotalTime;
        this.bdTotalCycle = bdTotalCycle;
        this.aircraftModelId = aircraftModelId;
        this.aircraftCheckDoneHour = aircraftCheckDoneHour;
        this.aircraftCheckDoneDate = aircraftCheckDoneDate;
    }

    public DailyHrsReportAircraftModel(Double airFrameTotalTime, Integer airframeTotalCycle, Double bdTotalTime, Integer bdTotalCycle) {
        this.airFrameTotalTime = airFrameTotalTime;
        this.airframeTotalCycle = airframeTotalCycle;
        this.bdTotalTime = bdTotalTime;
        this.bdTotalCycle = bdTotalCycle;
    }
}
