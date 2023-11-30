package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftIncidents;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftIncidentsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.IncidentsStatisticsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.IncidentsStatisticsViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TechIncViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftIncidentsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Aircraft Incidents Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/aircraft-incidents")
public class AircraftIncidentsController extends AbstractController<AircraftIncidents, AircraftIncidentsDto> {

    private final AircraftIncidentsService aircraftIncidentsService;

    public AircraftIncidentsController(IService<AircraftIncidents, AircraftIncidentsDto> service,
                                       AircraftIncidentsService aircraftIncidentsService) {
        super(service);
        this.aircraftIncidentsService = aircraftIncidentsService;
    }

    @PostMapping("/search")
    public PageData searchAircraftInterruptions(@Valid @RequestBody AircraftIncidentsSearchDto aircraftIncidentsSearchDto,
                                                @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                        direction = Sort.Direction.ASC) Pageable pageable){
        return aircraftIncidentsService.searchAircraftIncidents(aircraftIncidentsSearchDto,pageable);
    }

    @PostMapping("/statistics")
    public ResponseEntity<IncidentsStatisticsViewModel> incidentStatisticsReport(
            @Valid @RequestBody IncidentsStatisticsSearchDto searchDto) {
        return new ResponseEntity<>(aircraftIncidentsService.incidentStatisticsReport(searchDto), HttpStatus.OK);
    }

    @PostMapping("/tech-inc")
    public ResponseEntity<TechIncViewModel> techIncReport(@Valid @RequestBody IncidentsStatisticsSearchDto searchDto){
        return new ResponseEntity<>(aircraftIncidentsService.techIncReport(searchDto), HttpStatus.OK);
    }
}
