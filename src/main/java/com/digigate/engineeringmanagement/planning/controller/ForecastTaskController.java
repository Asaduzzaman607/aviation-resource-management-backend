package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Forecast;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastAircraftDto;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastDto;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastGenerateDto;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.ForecastViewModel;
import com.digigate.engineeringmanagement.planning.service.ForecastIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Forecast Task Controller
 *
 * @author Masud Rana
 */
@RestController
@RequestMapping("/api/forecast-task")
public class ForecastTaskController extends AbstractController<Forecast, ForecastDto> {
    private final ForecastIService forecastIService;

    /**
     * Parameterized constructor
     *
     * @param forecastIService {@link ForecastIService}
     * @param service          {@link IService}
     */
    @Autowired
    public ForecastTaskController(ForecastIService forecastIService, IService<Forecast, ForecastDto> service) {
        super(service);
        this.forecastIService = forecastIService;
    }

    /**
     * Get all lower model
     *
     * @param forecastGenerateDto {@link ForecastGenerateDto}
     * @param aircraftId          {@link Long}
     * @return {@link ResponseEntity<ForecastAircraftDto>}
     */
    @PostMapping("/generate/{aircraftId}")
    public ResponseEntity<ForecastAircraftDto> generate(
            @RequestBody @Valid ForecastGenerateDto forecastGenerateDto, @PathVariable Long aircraftId) {
        return new ResponseEntity<>(forecastIService.generate(forecastGenerateDto, aircraftId), HttpStatus.OK);
    }

    /**
     * responsible for searching forecast info
     *
     * @param searchDto {@link ForecastSearchDto}
     * @param pageable  {@link Pageable}
     * @return {@link ResponseEntity}
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody ForecastSearchDto searchDto,
                                           @PageableDefault(
                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ForecastViewModel> forecastPageData = forecastIService.search(searchDto, pageable);

        PageData pageData = new PageData(forecastPageData.getContent(), forecastPageData.getTotalPages(),
                pageable.getPageNumber() + 1, forecastPageData.getTotalElements());

        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }
}
