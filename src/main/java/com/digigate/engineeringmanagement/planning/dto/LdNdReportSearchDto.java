package com.digigate.engineeringmanagement.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LdNdReportSearchDto {
    private Long aircraftId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String taskType;
    private Integer status;
}
