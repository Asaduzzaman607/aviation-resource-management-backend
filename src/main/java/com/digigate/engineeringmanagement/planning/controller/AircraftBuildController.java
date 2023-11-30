package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftBuild;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.service.AircraftBuildIService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * AircraftBuild Controller
 *
 * @author Masud Rana
 */
@RestController
@RequestMapping("/api/aircraft-build")
public class AircraftBuildController
        extends AbstractSearchController<AircraftBuild, AircraftBuildPayload, AircraftBuildSearchPayload> {
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";
    private AircraftBuildIService aircraftBuildIService;

    /**
     * Parameterized constructor
     *
     * @param service               {@link IService}
     * @param aircraftBuildIService {@link AircraftBuildIService}
     */
    public AircraftBuildController(ISearchService<AircraftBuild,
            AircraftBuildPayload, AircraftBuildSearchPayload> service, AircraftBuildIService aircraftBuildIService) {
        super(service);
        this.aircraftBuildIService = aircraftBuildIService;
    }

    /**
     * Upload excel
     *
     * @param file       {@link MultipartFile}
     * @param aircraftId {@link Long}
     * @return {@link  ResponseEntity<ExcelDataResponse>}
     */
    @PostMapping("/upload/{aircraftId}")
    public ResponseEntity<ExcelDataResponse> importExcelFile(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftId) {
        ExcelDataResponse excelDataResponse = aircraftBuildIService.uploadExcel(file, aircraftId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    /**
     * This is an api endpoint to search Aircraft Build by part no and serial no
     *
     * @param searchDto {@link AircraftBuildPartSerialSearchDto}
     * @return {@link ResponseEntity<AircraftBuildPartSerialSearchViewModel>}
     */
    @PostMapping("/search/part-serial")
    public ResponseEntity<AircraftBuildPartSerialSearchViewModel>
    getByPartAndSerial(@RequestBody AircraftBuildPartSerialSearchDto searchDto) {
        return ResponseEntity.ok(aircraftBuildIService.searchByPartIdAndSerialByStoreInspection(searchDto));
    }

    @PostMapping("/make-in-active")
    public ResponseEntity<MessageResponse> inActiveStatus(@RequestBody @Valid AircraftBuildInactiveDto dto) {
        aircraftBuildIService.makeAcBuildInActive(dto);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }


    /**
     * This is an Api endpoint to generate OCCM report by aircraft Id
     *
     * @param pageable      {@link Pageable}
     * @param occmSearchDto {@link OCCMSearchDto}
     * @return {@link ResponseEntity<PageData> }
     */
    @PostMapping("/search/OCCM-report")
    public ResponseEntity<PageData> getOCCMReport(
            @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestBody @Valid OCCMSearchDto occmSearchDto) {
        Page<OCCMViewModel> report = aircraftBuildIService
                .findOCCMByAircraftId(occmSearchDto, occmSearchDto.getIsPageable() ? pageable : Pageable.unpaged());
        PageData pageData = new PageData(report.getContent(), report.getTotalPages(), report.getNumber() + 1,
                report.getTotalElements());
        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }


    @GetMapping("/find-engine-by-aircraft/{aircraftId}")
    public ResponseEntity<List<EngineViewModel>> findAircraftEngines(@PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftBuildIService.findAircraftEnginesByAircraftId(aircraftId), HttpStatus.OK);
    }

    @GetMapping("/find-inactive-engine-by-aircraft/{aircraftId}")
    public ResponseEntity<List<EngineViewModel>> findInactivateAircraftEngines(@PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftBuildIService.findInactivateAircraftEnginesByAircraftId(aircraftId), HttpStatus.OK);
    }

    /**
     * This is an API endpoint to generate report of Propeller
     *
     * @param propellerReportDto {@link PropellerReportDto}
     * @return PropellerReportViewModel as page data
     */
    @PostMapping("/propeller-report")
    public ResponseEntity<PropellerResponseData> getReport(@RequestBody PropellerReportDto propellerReportDto) {

        PropellerResponseData report = aircraftBuildIService.getPropellerReport(propellerReportDto);
        return ResponseEntity.ok(report);
    }

    /**
     * This is an API endpoint to get Propeller type position name by Aircraft Id
     *
     * @param aircraftId {@link Long}
     * @return {@link ResponseEntity<List<PropellerACBuildIdAndPositionViewModel>>}
     */
    @GetMapping("/propeller/position-name/{aircraftId}")
    public ResponseEntity<List<PropellerACBuildIdAndPositionViewModel>>
    getPositionNameByAircraftId(@PathVariable Long aircraftId) {
        return ResponseEntity.ok(aircraftBuildIService.getPropellerPositionNameByAircraftId(aircraftId));
    }

    @PostMapping("/ac-component/history")
    public AcComponentViewModel getComponentHistoryList(@RequestParam("partId") Long partId,
                                                        @RequestParam("serialId") Long serialId) {
        return aircraftBuildIService.getComponentHistoryList(partId, serialId);
    }

    @GetMapping("/ac-component/serial")
    public Set<AcSerialResponse> getAcSerialResponseByPartIdAndModelId(@RequestParam("partId") Long partId,
                                                                       @RequestParam("modelId") Long modelId) {
        return aircraftBuildIService.findAcSerialResponseByPartIdAndModelId(partId, modelId);
    }

    @GetMapping("/ac-component/part")
    public Set<AcPartResponse> getAcPartResponseByModelId(@RequestParam("modelId") Long modelId) {
        return aircraftBuildIService.getAcPartResponseByModelId(modelId);
    }

    @GetMapping("/ad-engine-details")
    public AircraftEngineDetailsViewModel findAircraftEngineDetailsForAdReport(
            @RequestParam("aircraftId") Long aircraftId,
            @RequestParam("partId") Long partId,
            @RequestParam("serialId") Long serialId,
            @RequestParam(value = "date", required = false) String dateString) {
        LocalDate date = null;
        if (Objects.nonNull(dateString)) {
            date = LocalDate.parse(dateString);
        }
        return aircraftBuildIService.findAircraftEngineDetailsForAdReport(serialId, partId, aircraftId, date);
    }

    @GetMapping("/apu-status-report")
    public ResponseEntity<ApuStatusReportViewModel> getApuStatusReport(@RequestParam("aircraftId") Long aircraftId) {
        return new ResponseEntity<>(aircraftBuildIService.getApuStatusReport(aircraftId), HttpStatus.OK);
    }

    @GetMapping("/apu-removed-status-report")
    public ResponseEntity<ApuStatusReportViewModel> getApuRemovedStatusReport(@RequestParam("aircraftId") Long aircraftId) {
        return new ResponseEntity<>(aircraftBuildIService.getApuRemovedStatusReport(aircraftId), HttpStatus.OK);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<AircraftBuildExcelViewModel>> getAllBuildAircraft() {
        return new ResponseEntity<>(aircraftBuildIService.getAllBuildAircraft(), HttpStatus.OK);
    }

}
