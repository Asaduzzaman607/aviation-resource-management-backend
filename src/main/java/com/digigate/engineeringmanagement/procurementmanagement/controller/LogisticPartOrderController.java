package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PartOrderListDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PoResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartOrderService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.STATUS_CHANGED;

@RestController
@RequestMapping("/api/logistic/part-orders")
public class LogisticPartOrderController {
    private final PartOrderService partOrderService;

    public LogisticPartOrderController(PartOrderService partOrderService) {
        this.partOrderService = partOrderService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody PoInternalDto poInternalDto) {
        poInternalDto.setRfqType(RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE, partOrderService.create(poInternalDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody PoInternalDto poInternalDto, @PathVariable Long id) {
        poInternalDto.setRfqType(RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE, partOrderService.update(poInternalDto, id, ApprovalStatusType.LOGISTIC_PURCHASE_ORDER).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoResponseDto> getSingle(@PathVariable Long id) {
        PoResponseDto poResponseDto = partOrderService.getSingle(id);
        return new ResponseEntity<>(poResponseDto, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid PoSearchDto searchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        searchDto.setRfqType(RfqType.LOGISTIC);
        return new ResponseEntity<>(partOrderService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        partOrderService.updateActiveStatus(id, isActive, RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PutMapping("/decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        partOrderService.makeDecision(id, approvalRequestDto, RfqType.LOGISTIC);
        return ResponseEntity.ok(STATUS_CHANGED);
    }

    @Transactional
    @PostMapping("/all")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody PartOrderListDto partOrderListDto) {
        partOrderListDto.setRfqType(RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE + " AND MORE..",
                partOrderService.create(partOrderListDto).stream().findFirst().get().getId()));
    }
}
