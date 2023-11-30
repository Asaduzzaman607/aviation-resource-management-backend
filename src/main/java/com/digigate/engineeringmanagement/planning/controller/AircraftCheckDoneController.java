package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckDone;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDoneDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDoneSearchDto;
import com.digigate.engineeringmanagement.planning.service.AircraftCheckDoneService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * AircraftCheckDone controller
 *
 * @author Nafiul Islam
 */

@RestController
@RequestMapping("/api/ac-check-done")
public class AircraftCheckDoneController extends AbstractController<AircraftCheckDone, AircraftCheckDoneDto> {

    private final AircraftCheckDoneService aircraftCheckDoneListService;

    public AircraftCheckDoneController(IService<AircraftCheckDone, AircraftCheckDoneDto> service,
                                       AircraftCheckDoneService aircraftCheckDoneListService) {
        super(service);
        this.aircraftCheckDoneListService = aircraftCheckDoneListService;
    }

    @PostMapping("/search")
    public PageData searchAircraftCheckDone(@Valid @RequestBody AircraftCheckDoneSearchDto aircraftCheckDoneSearchDto,
                                                @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return aircraftCheckDoneListService.searchAircraftCheckDone(aircraftCheckDoneSearchDto,pageable);
    }

}
