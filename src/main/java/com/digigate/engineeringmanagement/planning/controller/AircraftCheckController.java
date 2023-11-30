package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheck;
import com.digigate.engineeringmanagement.planning.payload.request.AcCheckLdndDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskCheckRequestDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckForAircraftViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LdndForTaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskAndAcCheckViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * AircraftCheck Controller
 *
 * @author Ashraful
 */
@RestController
@RequestMapping("/api/aircraft-check")
public class AircraftCheckController extends AbstractSearchController<AircraftCheck, AircraftCheckDto,
        AircraftCheckSearchDto> {
    private final AircraftCheckService aircraftCheckService;

    /**
     * Parameterized constructor
     *
     * @param service              {@link IService}
     * @param aircraftCheckService {@link AircraftCheckService}
     */
    public AircraftCheckController(ISearchService<AircraftCheck, AircraftCheckDto, AircraftCheckSearchDto> service,
                                   AircraftCheckService aircraftCheckService) {
        super(service);
        this.aircraftCheckService = aircraftCheckService;
    }

    /**
     * This method is responsible for find all Task By Aircraft Model
     * @return askAndAcCheckViewModel {@link TaskAndAcCheckViewModel}
     */
    @PostMapping("/ac-model")
    public ResponseEntity<TaskAndAcCheckViewModel> findAllByAcModelId(@RequestBody TaskCheckRequestDto dto) {
        return ResponseEntity.ok(aircraftCheckService.findAllByAcModelId(dto.getAcModelId(), dto.getThresholdHour(),
                dto.getThresholdDay()));
    }

    /**
     * This method is responsible for find all AcCheck By Aircraft
     *
     * @param aircraftId {@link Long}
     * @return aircraftCheckForAircraftViewModel {@link AircraftCheckForAircraftViewModel}
     */
    @GetMapping("/aircraft/{aircraftId}")
    public List<AircraftCheckForAircraftViewModel> findAllAircraftCheckByAircraft(@PathVariable Long aircraftId) {
        return aircraftCheckService.findAllAircraftCheckByAircraft(aircraftId);
    }

    /**
     * This method is responsible for find all Ldnd Task by acCheck ids
     *
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    @PostMapping("/ac-check-ids")
    public List<LdndForTaskViewModel> findAllLdndTaskByAcCheckIds(@RequestBody @Valid AcCheckLdndDto acCheckLdndDto) {
        return aircraftCheckService.findAllLdndTaskByAcCheckIdIn(acCheckLdndDto.getAcCheckIds(), acCheckLdndDto.getAircraftId());
    }

    /**
     * This method is responsible for find all Ldnd Task by aircraft
     *
     * @param aircraftId {@link Long}
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    @GetMapping("/ldnd-task/aircraft/{aircraftId}")
    public List<LdndForTaskViewModel> findAllLdndTaskByAircraftId(@PathVariable Long aircraftId) {
        return aircraftCheckService.findAllLdndTaskByAircraftId(aircraftId);
    }
}
