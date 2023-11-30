package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Multiple Flying Hours Report View
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleFlyingHoursReportViewModel {
    DailyHrsReportAircraftModel dailyHrsReportAircraftModel;
    DailyHrsReportBfDto dailyHrsReportBfDto;
    DailyHrsReportTotalModel total;
    List<DailyHrsReportDataModel> dailyHrsReportDataModelList;
}
