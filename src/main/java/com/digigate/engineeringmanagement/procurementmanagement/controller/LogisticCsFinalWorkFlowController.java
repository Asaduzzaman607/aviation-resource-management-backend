package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.service.ComparativeStatementService;
import com.digigate.engineeringmanagement.procurementmanagement.util.CsUtilService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/logistic/comparative-statements/final-management")
public class LogisticCsFinalWorkFlowController {
    private final ComparativeStatementService comparativeStatementService;
    private final CsUtilService csUtilService;
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";

    public LogisticCsFinalWorkFlowController(ComparativeStatementService comparativeStatementService, CsUtilService csUtilService) {
        this.comparativeStatementService = comparativeStatementService;
        this.csUtilService = csUtilService;
    }


    @Transactional
    @PutMapping("/decide/{id}")
    public ResponseEntity<MessageResponse> makeDecisionToFinal(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        csUtilService.decision(id, approvalRequestDto, CsWorkflowType.CS_FINAL, RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.STATUS_CHANGED + " (CS FINAL)"));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id, @RequestParam("active") Boolean isActive) {
        csUtilService.updateActiveStatus(id, isActive, CsWorkflowType.CS_FINAL);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid CsSearchDto searchDto,
                                           @PageableDefault(
                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        searchDto.setWorkflowType(CsWorkflowType.CS_FINAL);
        searchDto.setRfqType(RfqType.LOGISTIC);
        return new ResponseEntity<>(csUtilService.search(searchDto, pageable), HttpStatus.OK);
    }

    @GetMapping("/existing/{id}")
    public ResponseEntity<CsResponseDto> getExistingCs(@PathVariable Long id) {
        return ResponseEntity.ok(comparativeStatementService.getExistingCs(id, RfqType.LOGISTIC));
    }
}
