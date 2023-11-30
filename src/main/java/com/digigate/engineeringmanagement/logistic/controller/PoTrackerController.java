package com.digigate.engineeringmanagement.logistic.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.logistic.entity.PoTracker;
import com.digigate.engineeringmanagement.logistic.payload.request.PoTrackerRequestDto;
import com.digigate.engineeringmanagement.logistic.service.PoTrackerService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/po-tracker")
public class PoTrackerController extends AbstractSearchController<PoTracker, PoTrackerRequestDto, IdQuerySearchDto> {
    public PoTrackerController(PoTrackerService poTrackerService) {
        super(poTrackerService);
    }
}
