package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Dispatch Report Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchReportSearchDto {
    @NotNull
    private Long aircraftModelId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
}
