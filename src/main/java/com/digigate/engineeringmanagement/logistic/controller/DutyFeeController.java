package com.digigate.engineeringmanagement.logistic.controller;


import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.logistic.entity.DutyFee;
import com.digigate.engineeringmanagement.logistic.payload.request.DutyFeeRequestDto;
import com.digigate.engineeringmanagement.logistic.service.DutyFeeService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/duty-fees")
public class DutyFeeController extends AbstractSearchController<DutyFee, DutyFeeRequestDto, IdQuerySearchDto> {

    public DutyFeeController(DutyFeeService dutyFeeService) {
        super(dutyFeeService);
    }
}
