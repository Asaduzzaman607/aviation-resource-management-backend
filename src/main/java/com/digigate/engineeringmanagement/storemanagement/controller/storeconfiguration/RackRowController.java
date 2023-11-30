package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRow;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackRowDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.RackRowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-management/rack-rows")
public class RackRowController extends AbstractSearchController<RackRow, RackRowDto, IdQuerySearchDto> {
    public RackRowController(RackRowService service) {
        super(service);
    }
}
