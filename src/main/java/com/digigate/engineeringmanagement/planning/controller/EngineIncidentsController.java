package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.EngineIncidents;
import com.digigate.engineeringmanagement.planning.payload.request.EngineIncidentsDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.EngineIncidentsReportViewModel;
import com.digigate.engineeringmanagement.planning.service.EngineIncidentsService;
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

/**
 * Engine Incidents Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/engine-incidents")
public class EngineIncidentsController extends AbstractController<EngineIncidents, EngineIncidentsDto> {

    private final EngineIncidentsService engineIncidentsService;

    public EngineIncidentsController(IService<EngineIncidents, EngineIncidentsDto> service,
                                     EngineIncidentsService engineIncidentsService) {
        super(service);
        this.engineIncidentsService = engineIncidentsService;
    }

    @PostMapping("/search")
    public PageData searchEngineIncidents(@Valid @RequestBody EngineIncidentsSearchDto engineIncidentsSearchDto,
                                  @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                          direction = Sort.Direction.ASC) Pageable pageable){
        return engineIncidentsService.searchEngineIncidents(engineIncidentsSearchDto,pageable);
    }

    @PostMapping("/report")
    public ResponseEntity<EngineIncidentsReportViewModel> engineIncidentsReport(@Valid @RequestBody
                                                                                    EngineIncidentsSearchDto engineIncidentsSearchDto){
        return new ResponseEntity<>(engineIncidentsService.engineIncidentsReport(engineIncidentsSearchDto), HttpStatus.OK);
    }
}
