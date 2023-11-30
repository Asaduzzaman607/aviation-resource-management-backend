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

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@RestController
@RequestMapping("/api/procurement/finance/parts-invoice")
public class ProcurementPartsInvoiceFinanceController {
    private final PartsInvoicesService partsInvoicesService;

    public ProcurementPartsInvoiceFinanceController(PartsInvoicesService partsInvoicesService) {
        this.partsInvoicesService = partsInvoicesService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody PartsInvoicesDto partsInvoicesDto) {
        partsInvoicesDto.setRfqType(RfqType.PROCUREMENT);
        partsInvoicesDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.FINANCE);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE, partsInvoicesService.create(partsInvoicesDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody PartsInvoicesDto partsInvoicesDto, @PathVariable Long id) {
        partsInvoicesDto.setRfqType(RfqType.PROCUREMENT);
        partsInvoicesDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.FINANCE);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE, partsInvoicesService.update(partsInvoicesDto, id).getId()));
    }

    @PostMapping("/partially")
    public ResponseEntity<MessageResponse> partiallyUpdate(@Valid @RequestBody PartsInvoicesDto partsInvoicesDto) {
        partsInvoicesService.partiallyUpdate(partsInvoicesDto);
        return ResponseEntity.ok(new MessageResponse(SUCCESSFULLY_PARTIALLY_APPROVED));
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
        searchDto.setRfqType(RfqType.PROCUREMENT);
        searchDto.setPartsInVoiceWorkFlowType(PartsInVoiceWorkFlowType.FINANCE);
        return new ResponseEntity<>(partsInvoicesService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        partsInvoicesService.updateActiveStatus(id, isActive, PartsInVoiceWorkFlowType.FINANCE);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PutMapping("decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id,
                                               @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        partsInvoicesService.makeDecision(id, approvalRequestDto, RfqType.PROCUREMENT , PartsInVoiceWorkFlowType.FINANCE);
        return ResponseEntity.ok(ApplicationConstant.STATUS_CHANGED);
    }
}
