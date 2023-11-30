package com.digigate.engineeringmanagement.procurementmanagement.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartOrderItemService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/part-order-items")
public class PartOrderItemController extends AbstractSearchController<PartOrderItem, IDto, IdQuerySearchDto> {
    public PartOrderItemController(PartOrderItemService partOrderItemService) {
        super(partOrderItemService);
    }
}
