package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionCustomSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/procurement-requisitions")
public class ProcurementRequisitionController extends AbstractSearchController<
        ProcurementRequisition,
        ProcurementRequisitionDto,
        ProcurementRequisitionCustomSearchDto> {
    private final ProcurementRequisitionService procurementRequisitionService;
    private final ProcurementRequisitionItemService requisitionItemService;

    public ProcurementRequisitionController(ProcurementRequisitionService procurementRequisitionService,
                                            ProcurementRequisitionItemService requisitionItemService) {
        super(procurementRequisitionService);
        this.procurementRequisitionService = procurementRequisitionService;
        this.requisitionItemService = requisitionItemService;
    }

    @GetMapping("item/{id}")
    public ResponseEntity<RfqPartViewModel> findItemById(@PathVariable Long id){
        return ResponseEntity.ok(requisitionItemService.findItemById(id));
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody
    ApprovalRequestDto approvalRequestDto) {
        procurementRequisitionService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}