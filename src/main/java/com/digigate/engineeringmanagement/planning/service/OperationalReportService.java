package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.OpStatSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.FleetUtilizationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.OpStatReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ServiceUtilizationReportViewModel;

import java.util.List;

public interface OperationalReportService {

    List<OpStatReportViewModel> opStatReport(OpStatSearchDto searchDto);

    List<FleetUtilizationReportViewModel> getFleetUtilReport(OpStatSearchDto searchDto);

    List<ServiceUtilizationReportViewModel> getServiceUtilReport(OpStatSearchDto searchDto);
}
