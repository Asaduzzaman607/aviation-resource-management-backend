package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationInvoiceDetailDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VqdQuantityDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor-quotation-detail")
public class VendorQuotationDetailController extends AbstractSearchController<
        VendorQuotationInvoiceDetail,
        VendorQuotationInvoiceDetailDto,
        IdQuerySearchDto> {

    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    public VendorQuotationDetailController(VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService) {
        super(vendorQuotationInvoiceDetailService);
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
    }

    @PutMapping("/quantity")
    public ResponseEntity<MessageResponse> updateQuantity(@RequestBody VqdQuantityDto vqdQuantityDto){
        vendorQuotationInvoiceDetailService.updateQuantity(vqdQuantityDto);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE));
    }
}
