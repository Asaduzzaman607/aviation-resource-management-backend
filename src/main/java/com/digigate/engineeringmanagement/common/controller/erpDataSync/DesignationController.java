package com.digigate.engineeringmanagement.common.controller.erpDataSync;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Designation;
import com.digigate.engineeringmanagement.common.payload.request.erp.DesignationDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DesignationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/designation")
public class DesignationController extends AbstractSearchController<Designation, DesignationDto, ERPSearchRequestDto> {
    public DesignationController(DesignationService service) {
        super(service);
    }
}
