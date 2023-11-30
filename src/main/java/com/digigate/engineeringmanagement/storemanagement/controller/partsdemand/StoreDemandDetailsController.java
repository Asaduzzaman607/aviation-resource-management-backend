package com.digigate.engineeringmanagement.storemanagement.controller.partsdemand;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-demand-details")
public class StoreDemandDetailsController extends AbstractSearchController<StoreDemandItem, StoreDemandDetailsDto, IdQuerySearchDto> {
    public StoreDemandDetailsController(StoreDemandDetailsService storeDemandDetailsService) {
        super(storeDemandDetailsService);
    }
}
