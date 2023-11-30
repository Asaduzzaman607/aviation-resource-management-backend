package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.MultipleWorkOrderSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAcCheckIndexViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAirCraftViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderViewModel;

import java.util.List;

public interface WorkOrderIService {
    WorkOrderAirCraftViewModel getAircraftData(Long aircraftId);

    List<WorkOrderAcCheckIndexViewModel> getWorkOrderDataByAircraftId(Long aircraftId);

    List<WorkOrderViewModel> getMultipleReport(MultipleWorkOrderSearchDto multipleWorkOrderSearchDto);
}
