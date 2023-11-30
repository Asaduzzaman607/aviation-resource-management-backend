package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefectSearchDto implements SDto {

    private LocalDate fromDate;
    private LocalDate toDate;
    private Long locationId;
    private Long partId;
    private Long aircraftId;
    private Long aircraftModelId;
    private Boolean isActive;
    private Boolean isPageable = true;
}
