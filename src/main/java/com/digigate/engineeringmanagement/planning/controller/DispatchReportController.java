package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.planning.payload.request.DispatchReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.DispatchReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.InterruptionReportViewModel;
import com.digigate.engineeringmanagement.planning.service.DispatchReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Dispatch Report Controller
 *
 * @author Nafiul Islam
 */
@RestController
@RequestMapping("/api/dispatch-report")
public class DispatchReportController {

    private final DispatchReportService dispatchReportService;

    public DispatchReportController(DispatchReportService dispatchReportService) {
        this.dispatchReportService = dispatchReportService;
    }

    @PostMapping("/dispatch-stat-report")
    public ResponseEntity<List<DispatchReportViewModel>> dispatchReport(@Valid @RequestBody DispatchReportSearchDto searchDto) {
        return new ResponseEntity<>(dispatchReportService.dispatchReport(searchDto), HttpStatus.OK);
    }

    @PostMapping("/interruption")
    public ResponseEntity<List<InterruptionReportViewModel>> interruptionReport(@Valid @RequestBody
                                                                                    DispatchReportSearchDto searchDto) {
        return new ResponseEntity<>(dispatchReportService.interruptionReport(searchDto), HttpStatus.OK);
    }

}
