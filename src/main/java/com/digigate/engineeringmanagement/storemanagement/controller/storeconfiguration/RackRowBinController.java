package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRowBin;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackRowBinDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.RackRowBinService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-management/rack-row-bins")
public class RackRowBinController extends AbstractSearchController<RackRowBin, RackRowBinDto, IdQuerySearchDto> {
    public RackRowBinController(RackRowBinService service) {
        super(service);
    }
}
