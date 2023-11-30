package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EngineAdReportSearchDto {

    @NotNull
    private Long aircraftId;

    @NotNull
    private Long partId;

    @NotNull
    private Long serialId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thDay;
    private Double thHour;
    private Integer thCycle;
    private String taskSource;
    private LocalDate date;

    private Boolean isPageable = false;
}
