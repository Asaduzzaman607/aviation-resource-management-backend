package com.digigate.engineeringmanagement.storemanagement.controller.scrap;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapPartDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.scrap.StoreScrapPartService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/scrap-parts")
public class StoreScrapPartController extends AbstractSearchController<StoreScrapPart, StoreScrapPartDto, IdQuerySearchDto> {

    public StoreScrapPartController(StoreScrapPartService storeScrapPartService) {
        super(storeScrapPartService);
    }
}
