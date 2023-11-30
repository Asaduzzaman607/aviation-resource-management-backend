package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.StoreStockRoom;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.StoreStockRoomDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.StoreStockRoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-management/store-stock-rooms")
public class StoreStockRoomController extends AbstractSearchController<StoreStockRoom, StoreStockRoomDto, IdQuerySearchDto> {
    public StoreStockRoomController(StoreStockRoomService storeStockRoomService) {
        super(storeStockRoomService);
    }
}
