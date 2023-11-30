package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftInterruptions;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftInterruptionsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftInterruptionsSearchDto;
import com.digigate.engineeringmanagement.planning.service.AircraftInterruptionsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;

/**
 * Aircraft Interruptions Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/aircraft-interruptions")
public class AircraftInterruptionsController extends AbstractController<AircraftInterruptions,AircraftInterruptionsDto>{

    private final AircraftInterruptionsService aircraftInterruptionsService;

    public AircraftInterruptionsController(IService<AircraftInterruptions, AircraftInterruptionsDto> service,
                                           AircraftInterruptionsService aircraftInterruptionsService) {
        super(service);

        this.aircraftInterruptionsService = aircraftInterruptionsService;
    }

    @PostMapping("/search")
    public PageData searchAircraftInterruptions(@Valid @RequestBody AircraftInterruptionsSearchDto aircraftInterruptionsSearchDto,
                                                @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return aircraftInterruptionsService.searchAircraftInterruptions(aircraftInterruptionsSearchDto,pageable);
    }

}
