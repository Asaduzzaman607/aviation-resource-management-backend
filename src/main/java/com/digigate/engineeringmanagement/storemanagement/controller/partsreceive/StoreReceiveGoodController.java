package com.digigate.engineeringmanagement.storemanagement.controller.partsreceive;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreReceivedGood;
import com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive.StoreReceiveGoodDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.StoreReceiveGoodSearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.partsreceive.StoreReceiveGoodService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/receive-goods")
public class StoreReceiveGoodController extends AbstractSearchController<
        StoreReceivedGood,
        StoreReceiveGoodDto,
        StoreReceiveGoodSearchDto> {
    public StoreReceiveGoodController(StoreReceiveGoodService service) {
        super(service);
    }
}
