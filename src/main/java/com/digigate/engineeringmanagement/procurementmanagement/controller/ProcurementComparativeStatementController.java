package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsGenerateDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsRemarksDto;
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

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE;

@RestController
@RequestMapping("/api/procurement/comparative-statements/material-management")
public class ProcurementComparativeStatementController {
    private final ComparativeStatementService comparativeStatementService;
    private final CsUtilService csUtilService;
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";
    private static final String REMARKS_UPDATED_SUCCESSFULLY_MESSAGE = "Remarks Updated Successfully";

    public ProcurementComparativeStatementController(ComparativeStatementService comparativeStatementService, CsUtilService csUtilService) {
        this.comparativeStatementService = comparativeStatementService;
        this.csUtilService = csUtilService;
    }

    @GetMapping("existing/{id}")
    public ResponseEntity<CsResponseDto> getExistingCs(@PathVariable Long id) {
        return ResponseEntity.ok(comparativeStatementService.getExistingCs(id, RfqType.PROCUREMENT));
    }

    @PostMapping("/generate")
    public ResponseEntity<CsResponseDto> generateCs(@Valid @RequestBody CsGenerateDto csGenerateDto, @RequestParam("type") OrderType type) {
        csGenerateDto.setRfqType(RfqType.PROCUREMENT);
        return ResponseEntity.ok(comparativeStatementService.generateCs(csGenerateDto, type));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody CsDto csDto) {
        csDto.setRfqType(RfqType.PROCUREMENT);
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, comparativeStatementService.create(csDto).getId()));
    }

    @Transactional
    @PutMapping("/decide/{id}")
    public ResponseEntity<MessageResponse> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        csUtilService.decision(id, approvalRequestDto, CsWorkflowType.CS_INITIAL, RfqType.PROCUREMENT);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.STATUS_CHANGED + " (CS INITIAL)"));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id, @RequestParam("active") Boolean isActive) {
        csUtilService.updateActiveStatus(id, isActive, CsWorkflowType.CS_INITIAL);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }

    @PatchMapping("/update-remarks/{id}")
    public ResponseEntity<MessageResponse> updateRemarks(@PathVariable Long id, @RequestBody CsRemarksDto csRemarksDto) {
        csUtilService.updateRemarks(id, csRemarksDto);
        return ResponseEntity.ok(new MessageResponse(REMARKS_UPDATED_SUCCESSFULLY_MESSAGE));
    }


    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid CsSearchDto searchDto,
                                           @PageableDefault(
                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        searchDto.setWorkflowType(CsWorkflowType.CS_INITIAL);
        searchDto.setRfqType(RfqType.PROCUREMENT);
        return new ResponseEntity<>(csUtilService.search(searchDto, pageable), HttpStatus.OK);
    }
}
