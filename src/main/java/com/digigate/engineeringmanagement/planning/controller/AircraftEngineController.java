package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEngineDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEngineSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineLlpStatusReportDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftEngineTmmRgbViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineInfoViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineLlpStatusReportViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Aircraft Engine Controller
 *
 * @author Pranoy Das
 */
@RestController
@RequestMapping("/api/engine")
@Validated
public class AircraftEngineController {
    private final AircraftEngineService aircraftEngineService;

    /**
     * autowired constructor
     *
     * @param aircraftEngineService {@link AircraftEngineService}
     */
    @Autowired
    public AircraftEngineController(AircraftEngineService aircraftEngineService) {
        this.aircraftEngineService = aircraftEngineService;
    }

    /**
     * responsible for generating engine llp status report
     *
     * @param engineLlpStatusReportDto {@link EngineLlpStatusReportDto}
     * @return                         engine LLP status report as view model
     */
    @PostMapping("/engine-llp-status-report")
    public ResponseEntity<EngineLlpStatusReportViewModel> getEngineLlpStatusReport(
            @Valid @RequestBody EngineLlpStatusReportDto engineLlpStatusReportDto) {
        return new ResponseEntity<>(aircraftEngineService.generateEngineLlpStatusReport(engineLlpStatusReportDto),
                HttpStatus.OK);
    }

    @PostMapping("/inactivate-engine-llp-status-report")
    public ResponseEntity<EngineLlpStatusReportViewModel> getInactivateEngineLlpStatusReport(
            @Valid @RequestBody EngineLlpStatusReportDto engineLlpStatusReportDto) {
        return new ResponseEntity<>(aircraftEngineService.generateInactivateEngineLlpStatusReport(engineLlpStatusReportDto),
                HttpStatus.OK);
    }

    /**
     * responsible for saving or updating Engine Tmm and Rgb Info
     *
     * @param aircraftEngineDto {@link AircraftEngineDto}
     * @return                  response message
     */
    @PostMapping("/save-update-engine-info")
    public ResponseEntity<String> saveOrUpdateEngineTmmRgbInfo(@Valid
                                                                   @RequestBody AircraftEngineDto aircraftEngineDto) {
        return new ResponseEntity<>(aircraftEngineService.saveOrUpdateEngineTmmRgbInfo(aircraftEngineDto),
                HttpStatus.OK);
    }

    /**
     * responsible for search aircraft engine info by aircraft id
     *
     * @param aircraftEngineSearchDto {@link AircraftEngineSearchDto}
     * @param pageable                {@link Pageable}
     * @return                        response info as page data
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> searchAircraftEngineInfo(
            @RequestBody AircraftEngineSearchDto aircraftEngineSearchDto,
            @PageableDefault(sort =
                               ApplicationConstant.DEFAULT_SORT,
                               direction = Sort.Direction.ASC) Pageable pageable) {

        Page<EngineInfoViewModel> engineInfoViewModelPage =
                aircraftEngineService.searchAircraftEngineInfo(aircraftEngineSearchDto, pageable);

        PageData pageData = PageData.builder()
                .model(engineInfoViewModelPage.getContent())
                .totalPages(engineInfoViewModelPage.getTotalPages())
                .totalElements(engineInfoViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    /**
     * responsible for finding engine tmm and rgb info by aircraft build id
     *
     * @param aircraftBuildId aircraftBuildId
     * @return                engine tmm or rgb info as view model
     */
    @GetMapping("/find-by-aircraft-build/{aircraftBuildId}")
    public ResponseEntity<AircraftEngineTmmRgbViewModel> findEngineTmmRgbInfo(@PathVariable Long aircraftBuildId) {
        return new ResponseEntity<>(aircraftEngineService
                .findEngineTmmRgbInfoByAircraftBuild(aircraftBuildId), HttpStatus.OK);
    }
}
