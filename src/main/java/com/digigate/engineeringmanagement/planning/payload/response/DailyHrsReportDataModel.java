package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Daily Flying Hours Data Model
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyHrsReportDataModel {
    private Long amlId;
    private LocalDate date;
    private String flightNo;
    private String pageNo;
    private String sector;
    private Integer amlType;
    private String blockOnTime;
    private String blockOffTime;
    private Double blockTime;
    private String takeOffTime;
    private String landingTime; //LDG
    private Integer noOfLanding; //CYC
    private Double airTime; //Sector Hrs
    private Double grandTotalAirTime; //TAT
    private Integer grandTotalLanding; //TAC
    private Double engineOil1;
    private Double engineOil2;
    private Double apuOil;
}
