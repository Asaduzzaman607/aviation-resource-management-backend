package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Daily Flying Hours Report View
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyFlyingHoursReportViewModel {
    DailyHrsReportAircraftModel dailyHrsReportAircraftModel;
    DailyHrsReportBfDto dailyHrsReportBfDto;
    DailyHrsReportTotalModel total;
    PageData pageData;
}
