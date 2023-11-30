package com.digigate.engineeringmanagement.planning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 *Daily Utilization Report Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyUtilizationSearchDto {
    @NotNull
    LocalDate fromDate;
    @NotNull
    LocalDate toDate;
    @NotNull
    Long aircraftId;
}
