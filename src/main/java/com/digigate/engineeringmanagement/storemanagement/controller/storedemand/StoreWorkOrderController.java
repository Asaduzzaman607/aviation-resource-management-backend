package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreWorkOrder;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreWorkOrderDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreWorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/store-work-order")
public class StoreWorkOrderController extends AbstractSearchController<StoreWorkOrder,
        StoreWorkOrderDto, CommonWorkFlowSearchDto> {
    private final StoreWorkOrderService storeWorkOrderService;

    public StoreWorkOrderController(StoreWorkOrderService storeWorkOrderService) {
        super(storeWorkOrderService);
        this.storeWorkOrderService = storeWorkOrderService;
    }

    @GetMapping("/component")
    public ResponseEntity<?> getWorkOrderComponent(@RequestParam Long unserviceableId) {
        return ResponseEntity.ok(storeWorkOrderService.getWorkOrderComponent(unserviceableId));
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody
    ApprovalRequestDto approvalRequestDto) {
        storeWorkOrderService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);

    }
}
