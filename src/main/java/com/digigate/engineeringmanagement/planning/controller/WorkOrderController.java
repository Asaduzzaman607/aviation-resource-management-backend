package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.WorkOrder;
import com.digigate.engineeringmanagement.planning.payload.request.MultipleWorkOrderSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkOrderDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkOrderSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAcCheckIndexViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderAirCraftViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkOrderViewModel;
import com.digigate.engineeringmanagement.planning.service.WorkOrderIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Work Order Controller
 *
 * @author ashinisingha
 */

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController extends AbstractSearchController<WorkOrder, WorkOrderDto, WorkOrderSearchDto> {

    private final WorkOrderIService workOrderIService;

    /**
     * Parameterized Constructor
     *
     * @param iSearchService {@link ISearchService}
     * @param workOrderIService {@link WorkOrderIService}
     */
    public WorkOrderController(ISearchService<WorkOrder, WorkOrderDto, WorkOrderSearchDto> iSearchService,
                               WorkOrderIService workOrderIService) {
        super(iSearchService);
        this.workOrderIService = workOrderIService;
    }

    /**
     * This is an API end point to get aircraft data by aircraft id
     *
     * @param aircraftId {@link Long}
     * @return {@link  WorkOrderAirCraftViewModel}
     */
    @GetMapping("/aircraft/{aircraftId}")
    public ResponseEntity<WorkOrderAirCraftViewModel> getAircraftDataByAircraftId(@PathVariable Long aircraftId) {
        return ResponseEntity.ok(workOrderIService.getAircraftData(aircraftId));
    }

    /**
     * This method is responsible for getting work order data by aircraft id
     *
     * @param aircraftId {@link Long}
     * @return workOrderAcCheckIndexViewModel  {@link WorkOrderAcCheckIndexViewModel}
     */
    @GetMapping("work-order-by-aircraft/{aircraftId}")
    public List<WorkOrderAcCheckIndexViewModel> getWorkOrderDataByAircraftId(@PathVariable Long aircraftId)
    {
        return workOrderIService.getWorkOrderDataByAircraftId(aircraftId);
    }

    @PostMapping("/multiple-report")
    public List<WorkOrderViewModel> getMultipleReport(@RequestBody @Valid MultipleWorkOrderSearchDto multipleWorkOrderSearchDto){
        return workOrderIService.getMultipleReport(multipleWorkOrderSearchDto);
    }
}
