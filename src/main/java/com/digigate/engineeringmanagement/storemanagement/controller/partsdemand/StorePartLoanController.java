package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoan;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartLoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/store-parts-loan")
public class StorePartLoanController extends AbstractSearchController<StorePartLoan, StorePartLoanDto, CommonWorkFlowSearchDto> {
    private final StorePartLoanService storePartLoanService;

    public StorePartLoanController(StorePartLoanService storePartLoanService) {
        super(storePartLoanService);
        this.storePartLoanService = storePartLoanService;
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        storePartLoanService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}
