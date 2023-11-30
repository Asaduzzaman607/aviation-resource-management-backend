package com.digigate.engineeringmanagement.planning.payload.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Multiple Daily Hrs Report Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
public class MultipleDailyHrsReportSearchDto {
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @NotNull
    Long aircraftId;
}
