package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartAvailabilityRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/part-availabilities")
public class StorePartAvailabilityController extends AbstractSearchController<StorePartAvailability, StorePartAvailabilityRequestDto, IdQuerySearchDto> {
    public StorePartAvailabilityController(StorePartAvailabilityService service) {
        super(service);
    }
}
