package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PISearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PartsInvoicesDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PartsInvoicesViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartsInvoicesService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.STATUS_CHANGED;

@RestController
@RequestMapping("/api/logistic/audit/parts-invoice")
public class LogisticPartInvoiceAuditController {
    private final PartsInvoicesService partsInvoicesService;

    public LogisticPartInvoiceAuditController(PartsInvoicesService partsInvoicesService) {
        this.partsInvoicesService = partsInvoicesService;
    }


    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody PartsInvoicesDto partsInvoicesDto) {
        partsInvoicesDto.setRfqType(RfqType.LOGISTIC);
        partsInvoicesDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.AUDIT);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE, partsInvoicesService.create(partsInvoicesDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody PartsInvoicesDto partsInvoicesDto, @PathVariable Long id) {
        partsInvoicesDto.setRfqType(RfqType.LOGISTIC);
        partsInvoicesDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.AUDIT);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE, partsInvoicesService.update(partsInvoicesDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartsInvoicesViewModel> getSingle(@PathVariable Long id) {
        PartsInvoicesViewModel partsInvoicesViewModel = partsInvoicesService.getSingle(id);
        return new ResponseEntity<>(partsInvoicesViewModel, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid PISearchDto searchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        searchDto.setRfqType(RfqType.LOGISTIC);
        searchDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.AUDIT);
        return new ResponseEntity<>(partsInvoicesService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        partsInvoicesService.updateActiveStatus(id, isActive, PartsInVoiceWorkFlowType.AUDIT);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id,
                                               @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        partsInvoicesService.makeDecision(id, approvalRequestDto, RfqType.LOGISTIC , PartsInVoiceWorkFlowType.AUDIT);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}
