package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ReturnPartsDetailDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/return-unserviceable-parts")
public class ReturnUnserviceablePartController extends
        AbstractSearchController<ReturnPartsDetail, ReturnPartsDetailDto, IdQuerySearchDto> {
    private final ReturnPartsDetailService returnPartsDetailService;

    public ReturnUnserviceablePartController(ReturnPartsDetailService returnPartsDetailService) {
        super(returnPartsDetailService);
        this.returnPartsDetailService = returnPartsDetailService;
    }
}
