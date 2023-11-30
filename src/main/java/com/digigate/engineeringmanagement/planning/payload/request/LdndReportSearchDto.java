package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LdndReportSearchDto {

    @NotNull
    private Long aircraftId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thDay;
    private Double thHour;
    private Integer thCycle;
    private String taskSource;
    private String model;
    private String ampTaskNo;
    private String partNo;
    private String serialNumber;
    private Boolean isPageable = false;
}
