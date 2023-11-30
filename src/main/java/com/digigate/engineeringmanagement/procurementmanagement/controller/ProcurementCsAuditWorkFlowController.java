package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsAuditDisposalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsAuditDisposalResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.service.ComparativeStatementService;
import com.digigate.engineeringmanagement.procurementmanagement.service.CsAuditDisposalService;
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
import java.util.List;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE;

@RestController
@RequestMapping("/api/procurement/comparative-statements/audit-management")
public class ProcurementCsAuditWorkFlowController {

    private final CsUtilService csUtilService;
    private final CsAuditDisposalService csAuditDisposalService;
    private final ComparativeStatementService comparativeStatementService;
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";

    public ProcurementCsAuditWorkFlowController(CsUtilService csUtilService,
                                                CsAuditDisposalService csAuditDisposalService, ComparativeStatementService comparativeStatementService) {
        this.csUtilService = csUtilService;
        this.csAuditDisposalService = csAuditDisposalService;
        this.comparativeStatementService = comparativeStatementService;
    }

    @GetMapping("/disposal/{id}")
    public ResponseEntity<List<CsAuditDisposalResponseDto>> getAuditDisposalByItemPartId(@PathVariable Long id) {
        return ResponseEntity.ok(csAuditDisposalService.findByItemPartId(id));
    }

    @GetMapping("/single/disposal/{id}")
    public ResponseEntity<CsAuditDisposalResponseDto> getSingle(@PathVariable Long id) {
        return ResponseEntity.ok(csAuditDisposalService.getSingle(id));
    }

    @Transactional
    @PutMapping("/decide/{id}")
    public ResponseEntity<MessageResponse> makeDecisionToAudit(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        csUtilService.decision(id, approvalRequestDto, CsWorkflowType.AUDIT, RfqType.PROCUREMENT);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.STATUS_CHANGED + " (AUDIT)"));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id, @RequestParam("active") Boolean isActive) {
        csUtilService.updateActiveStatus(id, isActive, CsWorkflowType.AUDIT);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid CsSearchDto searchDto,
                                           @PageableDefault(
                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        searchDto.setWorkflowType(CsWorkflowType.AUDIT);
        searchDto.setRfqType(RfqType.PROCUREMENT);
        return new ResponseEntity<>(csUtilService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/disposal")
    public ResponseEntity<MessageResponse> disposal(@RequestBody @Valid CsAuditDisposalDto csAuditDisposalDto) {
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE,
                csUtilService.disposal(csAuditDisposalDto).getId()));
    }

    @PutMapping("/disposal/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody CsAuditDisposalDto dto, @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, csAuditDisposalService.update(dto, id).getId()));
    }
    @GetMapping("/existing/{id}")
    public ResponseEntity<CsResponseDto> getExistingCs(@PathVariable Long id) {
        return ResponseEntity.ok(comparativeStatementService.getExistingCs(id, RfqType.PROCUREMENT));
    }
}
