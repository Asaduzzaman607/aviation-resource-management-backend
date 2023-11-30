package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AMLDefectRectificationDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlDefectRectificationReportDto;
import com.digigate.engineeringmanagement.planning.payload.request.ClientRequestListData;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationModelView;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import com.digigate.engineeringmanagement.planning.service.AmlDefectRectificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * AMLDefectRectification Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/aml-defect-rectification")
public class AmlDefectRectificationController {

    private final AmlDefectRectificationService amlDefectRectificationService;

    /**
     * Parameterized constructor
     *
     * @param amlDefectRectificationService {@link AmlDefectRectificationService}
     */
    public AmlDefectRectificationController(AmlDefectRectificationService amlDefectRectificationService) {
        this.amlDefectRectificationService = amlDefectRectificationService;
    }

    /**
     * get list by aml id
     *
     * @param amlId {@link Long}
     * @return response {@link List<AmlDefectRectificationModelView>}
     */
    @GetMapping("/{amlId}")
    public ResponseEntity<List<AmlDefectRectificationModelView>> getDefectRectificationList(
            @PathVariable Long amlId) {
        return new ResponseEntity<>(amlDefectRectificationService.getDefectRectificationsByAmlId(amlId), HttpStatus.OK);
    }

    /**
     *  create api
     *
     * @param defectRectificationDtos {@link ClientRequestListData<AMLDefectRectificationDto>}
     * @return response {@link List<AmlDefectRectificationModelView>}
     */
    @Transactional
    @PostMapping("")
    public ResponseEntity<List<AmlDefectRectificationModelView>> create(
            @RequestBody @Valid List<AMLDefectRectificationDto> defectRectificationDtos) {
        return ResponseEntity.ok(amlDefectRectificationService.create(defectRectificationDtos));
    }

    /**
     *  update api
     *
     * @param defectRectificationDtos {@link ClientRequestListData<AMLDefectRectificationDto>}
     * @return response {@link List<AmlDefectRectificationModelView>}
     */
    @Transactional
    @PutMapping("")
    public ResponseEntity<List<AmlDefectRectificationModelView>> update(
            @RequestBody @Valid List<AMLDefectRectificationDto> defectRectificationDtos) {
        return ResponseEntity.ok(amlDefectRectificationService.update(defectRectificationDtos));
    }

    /**
     * This is an API endpoint to generate report of AmlDefectRectification
     *
     * @param reportDto {@link AmlDefectRectificationReportDto}
     * @param pageable {@link Pageable}
     * @return AmlDefectRectificationReportViewModel as page data
     */
    @PostMapping("/report")
    public ResponseEntity<PageData> getReport(@RequestBody AmlDefectRectificationReportDto reportDto,
                                              @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                      direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AmlDefectRectificationReportViewModel> rectificationReport =
                amlDefectRectificationService.generateAmlDefectRectificationReport(reportDto,
                        reportDto.getIsPageable() ? pageable : Pageable.unpaged());
        PageData pageData = new PageData(rectificationReport.getContent(), rectificationReport.getTotalPages(),
                rectificationReport.getNumber() + 1, rectificationReport.getTotalElements());
        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    @PostMapping("/search-defect-rect")
    public ResponseEntity<List<DefRectSearchViewModel>> searchDefectRectificationList(
            @RequestBody AmlDefectRectificationReportDto reportDto) {
        return new ResponseEntity<>(amlDefectRectificationService.searchDefectRectificationList(
                reportDto.getAircraftId(), reportDto.getStartDate(), reportDto.getEndDate()), HttpStatus.OK);
    }


}
