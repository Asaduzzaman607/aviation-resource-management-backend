package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckIndex;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckIndexDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckIndexSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.ManHourDto;
import com.digigate.engineeringmanagement.planning.payload.request.ManHourReportDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckIndexForListView;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckIndexForSingleViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ManHourReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkScopeReportViewModel;
import com.digigate.engineeringmanagement.planning.service.AircraftCheckIndexService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AircraftCheckIndex Controller
 *
 * @author Ashraful
 */
@RestController
@RequestMapping("/api/aircraft-check-index")
public class AircraftCheckIndexController extends AbstractSearchController<AircraftCheckIndex, AircraftCheckIndexDto,
        AircraftCheckIndexSearchDto> {
    private final AircraftCheckIndexService aircraftCheckIndexService;

    /**
     * Parameterized constructor
     *
     * @param iSearchService {@link ISearchService}
     * @param aircraftCheckIndexService {@link AircraftCheckIndexService}
     */
    public AircraftCheckIndexController(ISearchService<AircraftCheckIndex, AircraftCheckIndexDto,
            AircraftCheckIndexSearchDto> iSearchService, AircraftCheckIndexService aircraftCheckIndexService) {

        super(iSearchService);
        this.aircraftCheckIndexService = aircraftCheckIndexService;
    }

    /**
     * This method is responsible for getAcCheckIndexById ForSingleView
     *
     * @param acCheckIndexId {@link Long}
     * @return aircraftCheckIndexForSingleViewModel  {@link AircraftCheckIndexForSingleViewModel}
     */
    @GetMapping("/get-by-id/{acCheckIndexId}")
    public AircraftCheckIndexForSingleViewModel getAcCheckIndexByIdForSingleView(@PathVariable Long acCheckIndexId)
    {
       return aircraftCheckIndexService.getAcCheckIndexByIdForSingleView(acCheckIndexId);
    }

    /**
     * responsible generating man hour report
     *
     * @param manHourDto  {@link ManHourDto}
     * @return            man hour report response
     */
    @PostMapping("/man-hour")
    public ResponseEntity<ManHourReportViewModel> getManHourReport(@RequestBody ManHourDto manHourDto,
                                                                   @PageableDefault(sort =
                                                                           ApplicationConstant.DEFAULT_SORT,
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(aircraftCheckIndexService
                .getManHourReport(manHourDto.getAcCheckIndexId(), manHourDto.getIsPageable(), pageable), HttpStatus.OK);
    }

    /**
     * responsible for updating ldnd
     *
     * @param manHourReportDto   {@link ManHourReportDto}
     * @return                   response status message
     */
    @PostMapping("/update-ldnd")
    public ResponseEntity<String> updateLdndFromManHourReport(@RequestBody ManHourReportDto manHourReportDto) {
        return new ResponseEntity<>(
                aircraftCheckIndexService.updateLdndFromManHourReport(manHourReportDto), HttpStatus.OK);
    }

    /**
     * responsible for finding ac check index list by aircraft
     *
     * @param aircraftId   aircraft id
     * @return             list of aircraft check index list as view model
     */
    @GetMapping("/find-all-ac-check-index/{aircraftId}")
    public ResponseEntity<List<AircraftCheckIndexForListView>> getAllAircraftCheckIndex(@PathVariable Long aircraftId) {
        return new ResponseEntity<>(aircraftCheckIndexService.getAllAircraftCheckIndex(aircraftId), HttpStatus.OK);
    }

    /**
     * responsible generating man work scope report
     *
     * @param manHourDto  {@link ManHourDto}
     * @return             work scope report response
     */
    @PostMapping("/work-scope")
    public ResponseEntity<WorkScopeReportViewModel> getWorkScopeReport(@RequestBody ManHourDto manHourDto,
                                                                       @PageableDefault(sort =
                                                                               ApplicationConstant.DEFAULT_SORT,
                                                                               direction = Sort.Direction.ASC)
                                                                               Pageable pageable) {
        return new ResponseEntity<>(aircraftCheckIndexService.getWorkScopeReport(manHourDto.getAcCheckIndexId(),
                manHourDto.getIsPageable(), pageable), HttpStatus.OK);
    }
}
