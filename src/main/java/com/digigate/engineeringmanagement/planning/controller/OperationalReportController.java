package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.planning.payload.request.OpStatSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.FleetUtilizationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.OpStatReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ServiceUtilizationReportViewModel;
import com.digigate.engineeringmanagement.planning.service.OperationalReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * TaskReportController Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/operation-report")
public class OperationalReportController {

    private final OperationalReportService operationalReportService;

    public OperationalReportController(OperationalReportService operationalReportService) {
        this.operationalReportService = operationalReportService;
    }


    @PostMapping("/op-stat-report")
    public ResponseEntity<List<OpStatReportViewModel>> opStatReport(@Valid @RequestBody OpStatSearchDto searchDto) {
        return new ResponseEntity<>(operationalReportService.opStatReport(searchDto), HttpStatus.OK);
    }

    @PostMapping("/fleet-util-report")
    public ResponseEntity<List<FleetUtilizationReportViewModel>> getFleetUtilReport(
            @Valid @RequestBody OpStatSearchDto searchDto) {
        return new ResponseEntity<>(operationalReportService.getFleetUtilReport(searchDto), HttpStatus.OK);
    }

    @PostMapping("/service-util-report")
    public ResponseEntity<List<ServiceUtilizationReportViewModel>> getServiceUtilReport(
            @Valid @RequestBody OpStatSearchDto searchDto) {
        return new ResponseEntity<>(operationalReportService.getServiceUtilReport(searchDto), HttpStatus.OK);
    }

}
