package com.digigate.engineeringmanagement.planning.dto.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Daily Hrs Repor tSearch Dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
public class DailyHrsReportSearchDto {
    @NotNull
    LocalDate date;
    @NotNull
    Long aircraftId;
}
