package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.WorkPackage;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageReportDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageCertificateReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageSummaryReportViewModel;
import com.digigate.engineeringmanagement.planning.service.WorkPackageIService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Work Package Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/work-package")
public class WorkPackageController extends AbstractSearchController<WorkPackage, WorkPackageDto, WorkPackageSearchDto> {

    private WorkPackageIService workPackageIService;

    private static final String CREATED_SUCCESSFULLY_MESSAGE = "Created Successfully";
    private static final String UPDATED_SUCCESSFULLY_MESSAGE = "Updated Successfully";
    /**
     * Parameterized Constructor
     *
     * @param iSearchService {@link ISearchService}
     */
    public WorkPackageController(ISearchService<WorkPackage, WorkPackageDto, WorkPackageSearchDto> iSearchService,
                                 WorkPackageIService workPackageIService) {
        super(iSearchService);
        this.workPackageIService = workPackageIService;
    }

    /**
     * This is an API endpoint to get Work Package Summary Report by aircraft check index id
     *
     * @param workPackageReportDto {@link WorkPackageReportDto}
     * @return {@link ResponseEntity<WorkPackageSummaryReportViewModel>}
     */
    @PostMapping("/report")
    public ResponseEntity<WorkPackageSummaryReportViewModel> getReport(@RequestBody WorkPackageReportDto
                                                                                   workPackageReportDto) {
        return ResponseEntity.ok(workPackageIService.getReport(workPackageReportDto.getWorkPackageId()));
    }

    /**
     * This is an API endpoint to get Work Package Certificate report by aircraft check index id
     *
     * @param workPackageReportDto {@link WorkPackageReportDto}
     * @return {@link ResponseEntity<WorkPackageCertificateReportViewModel>}
     */
    @PostMapping("/report-certificate")
    public ResponseEntity<WorkPackageCertificateReportViewModel> getCertificateReport(@RequestBody WorkPackageReportDto
                                                                                              workPackageReportDto) {
        return ResponseEntity.ok(workPackageIService.getCertificateReport(workPackageReportDto.getWorkPackageId()));
    }


    @PostMapping("/certification")
    public ResponseEntity<MessageResponse> saveCertificate(@Valid @RequestBody WorkPackageDto dto) {
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE,
                workPackageIService.saveCertification(dto).getId()));
    }

    @PutMapping("/certification/{id}")
    public ResponseEntity<MessageResponse> updateCertificate(@Valid @RequestBody WorkPackageDto dto,  @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE,
                workPackageIService.updateCertification(dto, id).getId()));
    }

}
