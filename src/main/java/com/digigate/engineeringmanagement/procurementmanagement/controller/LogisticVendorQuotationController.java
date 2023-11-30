package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsQuotationViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@RestController
@RequestMapping("/api/logistic/vendor/quotations")
public class LogisticVendorQuotationController {
    private final VendorQuotationService vendorQuotationService;

    public LogisticVendorQuotationController(VendorQuotationService vendorQuotationService) {
        this.vendorQuotationService = vendorQuotationService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody VendorQuotationDto vendorQuotationDto) {
        vendorQuotationDto.setRfqType(RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, vendorQuotationService.create(vendorQuotationDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody VendorQuotationDto vendorQuotationDto, @PathVariable Long id) {
        vendorQuotationDto.setRfqType(RfqType.LOGISTIC);
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, vendorQuotationService.update(vendorQuotationDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorQuotationViewModel> getSingle(@PathVariable Long id) {
        VendorQuotationViewModel vendorQuotationViewModel = vendorQuotationService.getSingle(id);
        return new ResponseEntity<>(vendorQuotationViewModel, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid VendorQuotationSearchDto vendorQuotationSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT, direction = Sort.Direction.ASC) Pageable pageable) {
        vendorQuotationSearchDto.setRfqType(RfqType.LOGISTIC);
        return new ResponseEntity<>(vendorQuotationService.search(vendorQuotationSearchDto, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        vendorQuotationService.updateActiveStatus(id, isActive);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @GetMapping("/cs/{id}")
    public Pair<String, List<CsQuotationViewModel>> getAll(@PathVariable Long id) {
        return vendorQuotationService.findVendorQuotationByType(OrderType.PURCHASE, id);
    }
}
