package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Rack;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.RackService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-management/racks")
public class RackController extends AbstractSearchController<Rack, RackDto, IdQuerySearchDto> {
    public RackController(RackService service) {
        super(service);
    }
}
