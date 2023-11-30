package com.digigate.engineeringmanagement.storemanagement.controller.scrap;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrap;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.scrap.StoreScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/store/scraps")
public class StoreScrapController extends AbstractSearchController<StoreScrap, StoreScrapDto, CommonWorkFlowSearchDto> {
    private final StoreScrapService storeScrapService;

    public StoreScrapController(StoreScrapService storeScrapService) {
        super(storeScrapService);
        this.storeScrapService = storeScrapService;
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        storeScrapService.makeDecision(id, approvalRequestDto);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}
