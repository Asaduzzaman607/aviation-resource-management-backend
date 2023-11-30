package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Multiple Daily Flying Hours Report View
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleDailyFlyingHoursReportViewModel {
    LocalDate date;
    MultipleFlyingHoursReportViewModel multipleFlyingHoursReportViewModel;
}
