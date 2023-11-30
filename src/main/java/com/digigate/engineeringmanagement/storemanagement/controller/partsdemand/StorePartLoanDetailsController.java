package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoanDetails;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDetailDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartLoanDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store-parts-loan-details")
public class StorePartLoanDetailsController extends AbstractSearchController<StorePartLoanDetails, StorePartLoanDetailDto, IdQuerySearchDto> {
    private final StorePartLoanDetailsService storePartLoanDetailsService;

    public StorePartLoanDetailsController(StorePartLoanDetailsService storePartLoanDetailsService) {
        super(storePartLoanDetailsService);
        this.storePartLoanDetailsService = storePartLoanDetailsService;
    }
}
