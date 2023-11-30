package com.digigate.engineeringmanagement.planning.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Oil UpLift Report Search Dto
 *
 * @author ashraful
 */
@Getter
@Setter
public class OilUpLiftReportSearchDto {
    @NotNull
    LocalDate fromDate;
    @NotNull
    LocalDate toDate;
    @NotNull
    Long aircraftId;

    private Boolean isPageable = false;
}
