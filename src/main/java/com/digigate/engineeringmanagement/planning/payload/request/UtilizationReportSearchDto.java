package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Sector-wise utilization search dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UtilizationReportSearchDto {
    @NotNull
    Long aircraftId;
    @NotNull
    LocalDate fromDate;
    @NotNull
    LocalDate toDate;
}
