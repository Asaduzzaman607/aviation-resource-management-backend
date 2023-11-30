package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Room;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RoomDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.RoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/store-management/rooms")
public class RoomController extends AbstractSearchController<Room, RoomDto, IdQuerySearchDto> {
    public RoomController(RoomService service) {
        super(service);
    }
}
