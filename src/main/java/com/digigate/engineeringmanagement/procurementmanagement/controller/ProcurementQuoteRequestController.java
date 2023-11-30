package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.RfqSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.RfqRequestDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqAndPartViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.service.QuoteRequestService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.STATUS_CHANGED;

@RestController
@RequestMapping("/api/procurement/quote-requests")
public class ProcurementQuoteRequestController {

    private final QuoteRequestService quoteRequestService;

    public ProcurementQuoteRequestController(QuoteRequestService quoteRequestService) {
        this.quoteRequestService = quoteRequestService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody RfqRequestDto rfqRequestDto) {
        rfqRequestDto.setRfqType(RfqType.PROCUREMENT);
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, quoteRequestService.create(rfqRequestDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody RfqRequestDto rfqRequestDto, @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, quoteRequestService.update(rfqRequestDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RfqAndPartViewModel> getSingle(@PathVariable Long id) {
        RfqAndPartViewModel rfqAndPartViewModel = quoteRequestService.getSingle(id);
        return new ResponseEntity<>(rfqAndPartViewModel, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid RfqSearchDto searchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(quoteRequestService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        quoteRequestService.updateActiveStatus(id, isActive, RfqType.PROCUREMENT);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PutMapping("/decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        quoteRequestService.makeDecision(id, approvalRequestDto, RfqType.PROCUREMENT);
        return ResponseEntity.ok(STATUS_CHANGED);
    }

}
