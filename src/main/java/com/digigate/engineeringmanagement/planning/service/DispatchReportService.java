package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.DispatchReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.DispatchReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.InterruptionReportViewModel;

import java.util.List;

/**
 * Dispatch Report Service
 *
 * @author Nafiul Islam
 */
public interface DispatchReportService {
    List<DispatchReportViewModel> dispatchReport(DispatchReportSearchDto searchDto);

    List<InterruptionReportViewModel> interruptionReport(DispatchReportSearchDto searchDto);
}
