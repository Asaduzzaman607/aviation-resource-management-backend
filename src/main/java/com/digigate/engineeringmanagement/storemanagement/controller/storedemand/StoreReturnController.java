package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturn;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreReturnRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/store-return-parts")
public class StoreReturnController extends AbstractSearchController<StoreReturn, StoreReturnRequestDto, CommonWorkFlowSearchDto> {
    private final StoreReturnService storeReturnService;
    public StoreReturnController(StoreReturnService storeReturnService) {
        super(storeReturnService);
        this.storeReturnService = storeReturnService;
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody
    ApprovalRequestDto approvalRequestDto) {
        storeReturnService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}
