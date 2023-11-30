package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.exception.ErrorCodeReader;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.dto.request.DailyHrsReportSearchDto;
import com.digigate.engineeringmanagement.planning.dto.request.OilUpLiftReportSearchDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.payload.dto.request.MultipleDailyHrsReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftMaintenanceLogDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.PageNoDto;
import com.digigate.engineeringmanagement.planning.payload.request.UtilizationReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AmlDetailsService;
import com.digigate.engineeringmanagement.planning.service.AircraftMaintenanceLogService;
import com.digigate.engineeringmanagement.planning.service.AmlSaveService;
import com.digigate.engineeringmanagement.planning.service.DailyUtilizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Aircraft maintenance log controller
 *
 * @author Pranoy Das
 */
@RestController
@RequestMapping("/api/aircraft-maintenance-log")
public class AircraftMaintenanceLogController
        extends AbstractSearchController<AircraftMaintenanceLog, AircraftMaintenanceLogDto, AmlSearchDto> {

    private static final String PAGE_NO_IS_VALID = "Page no is valid";

    private static final String ATL_DELETED_SUCCESSFULLY = "atl deleted successfully";
    private static final String CREATED_SUCCESSFULLY_MESSAGE = "Created Successfully";
    private static final String UPDATED_SUCCESSFULLY_MESSAGE = "Updated Successfully";
    private final AircraftMaintenanceLogService aircraftMaintenanceLogService;
    private final AmlDetailsService amlDetailsService;
    private final AmlSaveService amlSaveService;

    private final DailyUtilizationService dailyUtilizationService;


    /**
     * autowired constructor
     *
     * @param service                       {@link ISearchService}
     * @param aircraftMaintenanceLogService {@link AircraftMaintenanceLogService}
     * @param amlDetailsService             {@link AmlDetailsService}
     */
    public AircraftMaintenanceLogController(
            ISearchService<AircraftMaintenanceLog, AircraftMaintenanceLogDto, AmlSearchDto> service,
            AircraftMaintenanceLogService aircraftMaintenanceLogService, AmlDetailsService amlDetailsService, AmlSaveService amlSaveService, DailyUtilizationService dailyUtilizationService) {
        super(service);
        this.aircraftMaintenanceLogService = aircraftMaintenanceLogService;
        this.amlDetailsService = amlDetailsService;
        this.amlSaveService = amlSaveService;
        this.dailyUtilizationService = dailyUtilizationService;
    }

    /**
     * Responsible for finding all active AML
     *
     * @return AmlDropdownViewModel as response entity
     */
    @GetMapping("/all")
    public ResponseEntity<List<AmlDropdownViewModel>> getAllActiveAml() {
        return new ResponseEntity<>(aircraftMaintenanceLogService.getAllActiveAml(), HttpStatus.OK);
    }

    /**
     * This method create daily flying hrs report
     *
     * @param searchDto {@link  DailyHrsReportSearchDto}
     * @return {@link  DailyFlyingHoursReportViewModel}
     */
    @PostMapping("/daily-flying-hrs")
    public DailyFlyingHoursReportViewModel getDailyReport(
            @RequestBody DailyHrsReportSearchDto searchDto,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return aircraftMaintenanceLogService.getDailyHrsReport(searchDto.getDate(),
                searchDto.getAircraftId(), page, size);
    }

    @PostMapping("/daily-flying-hrs-multiple-report")
    public List<MultipleDailyFlyingHoursReportViewModel> getMultipleDailyReport(
            @RequestBody @Valid MultipleDailyHrsReportSearchDto multipleDailyHrsReportSearchDto) {
        return aircraftMaintenanceLogService.getMultipleDailyHrsReport(multipleDailyHrsReportSearchDto);
    }
    /**
     * AML details api
     *
     * @return AmlDetailsResponseDto as response entity
     */
    @GetMapping("/aml-details/{id}")
    public ResponseEntity<AmlDetailsResponseDto> findAmlFullResponseById(@PathVariable Long id) {
        return new ResponseEntity<>(amlDetailsService.findAmlDetailsResponseById(id), HttpStatus.OK);
    }


    /**
     * Responsible for validating page no
     *
     * @param pageNoDto {@link PageNoDto}
     * @return Success or failure response as string
     */
    @PostMapping("/validate-page-no")
    public ResponseEntity<String> validatePageNumber(@Valid @RequestBody PageNoDto pageNoDto) {
        try {
            aircraftMaintenanceLogService.validateAmlPageNo(pageNoDto);
        } catch (EngineeringManagementServerException exception) {
            return new ResponseEntity<>(ErrorCodeReader.errorMap.get(exception.getErrorId()).getMessage(),
                    exception.getStatus());
        }
        return new ResponseEntity<>(PAGE_NO_IS_VALID, HttpStatus.OK);
    }

    /**
     * This Method purpose to generate sector wise utilization report
     *
     * @param searchDto {@link  UtilizationReportSearchDto}
     * @return report response model
     */
    @PostMapping("/utilization-report")
    public UtilizationReportResponse getUtilizationReport(
            @RequestBody @Valid UtilizationReportSearchDto searchDto) {
        return aircraftMaintenanceLogService.getUtilizationReport(searchDto);
    }

    /**
     * responsible for finding highest page no of aml
     *
     * @return highest page no of aml
     */
    @GetMapping("/find-aircraft-last-page-no/{aircraftId}")
    public ResponseEntity<AmlLastPageAndAircraftInfo> findAircraftInfoAndLastAmlPageNo(@PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftMaintenanceLogService.findAircraftInfoAndLastAmlPageNo(aircraftId),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete-atl/{aircraftId}")
    public ResponseEntity<MessageResponse> deleteAtlInfo(@PathVariable Long aircraftId){
        amlSaveService.deleteAtlInfo(aircraftId);
        return ResponseEntity.ok(new MessageResponse(ATL_DELETED_SUCCESSFULLY));
    }

    /**
     * responsible for generate report of oil up lift data
     *
     * @param searchDto {@link OilUpLiftReportSearchDto}
     * @return oilUpLiftReportViewModel  {@link OilUpLiftReportViewModel}
     */
    @PostMapping("/oil-uplift-report")
    public ResponseEntity<PageData> amlUpliftOilReport(
            @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestBody @Valid OilUpLiftReportSearchDto searchDto) {
        Page<OilUpLiftReportViewModel> report = aircraftMaintenanceLogService.getOilUpLiftReport(searchDto.getFromDate(),
                searchDto.getToDate(), searchDto.getAircraftId(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());
        PageData pageData = new PageData(report.getContent(), report.getTotalPages(), report.getNumber() + 1,
                report.getTotalElements());
        return new ResponseEntity<>(pageData, HttpStatus.OK);

    }

    @GetMapping("/find-airframe-info/{pageNo}/{aircraftId}")
    public ResponseEntity<AmlLastPageAndAircraftInfo> findAirframeInfoByPageNo(@PathVariable Integer pageNo,
                                                                               @PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftMaintenanceLogService.findAirframeInfoByPageNo(pageNo, aircraftId), HttpStatus.OK);
    }

    @GetMapping("/verify-atl/{amlId}")
    public ResponseEntity<MessageResponse> verifyAtl(@PathVariable Long amlId) {
        String responseMessage;
        if (aircraftMaintenanceLogService.verifyAtl(amlId)) {
            responseMessage = "Created Successfully";
        } else {
            responseMessage = "AML Was Not Saved!!";
        }
        return ResponseEntity.ok(new MessageResponse(responseMessage, amlId));
    }

    @Override
    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody AircraftMaintenanceLogDto dto) {
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, amlSaveService.createAml(dto).getId()));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody AircraftMaintenanceLogDto dto, @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, amlSaveService.updateAml(dto, id).getId()));
    }

    @GetMapping("/findAmlPageAndAlphabets/{aircraftId}")
    public ResponseEntity<List<AmlPageViewModel>> getAmlPageAndAlphabets(@PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftMaintenanceLogService.getAmlPageAndAlphabets(aircraftId),
                HttpStatus.OK);
    }

    @GetMapping("/interruptionInfo/{amlId}")
    public ResponseEntity<List<DefectRectViewModel>> getInterruptionInfo(@PathVariable Long amlId) {
        return new ResponseEntity<>(aircraftMaintenanceLogService.getInterruptionInfo(amlId),
                HttpStatus.OK);
    }
}
