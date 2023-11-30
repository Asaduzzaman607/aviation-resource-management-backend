package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandsDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreDemandResponseDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/store-demands")
public class StoreDemandController extends AbstractSearchController<StoreDemand, StoreDemandsDto, CommonWorkFlowSearchDto> {
    private final StoreDemandService storeDemandService;

    public StoreDemandController(StoreDemandService storeDemandService) {
        super(storeDemandService);
        this.storeDemandService = storeDemandService;
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        storeDemandService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }

    @GetMapping("withAlterPart/{id}")
    public ResponseEntity<StoreDemandResponseDto> getSingleWithAlterPArt(@PathVariable Long id) {
        return ResponseEntity.ok(storeDemandService.getSingleWithAlterPart(id));
    }
}
