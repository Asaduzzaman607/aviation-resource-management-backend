package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuoteRequestVendorDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuoteRequestVendorSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import com.digigate.engineeringmanagement.procurementmanagement.service.QuoteRequestVendorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quote-request-vendors")
public class QuoteRequestVendorController extends
        AbstractSearchController<QuoteRequestVendor, QuoteRequestVendorDto, QuoteRequestVendorSearchDto> {

    public QuoteRequestVendorController(QuoteRequestVendorService quoteRequestVendorService) {
        super(quoteRequestVendorService);
    }
}

