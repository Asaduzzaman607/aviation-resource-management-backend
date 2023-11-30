package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.planning.dto.request.DailyUtilizationSearchDto;
import com.digigate.engineeringmanagement.planning.entity.MonthlyUtilization;
import com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReportList;
import com.digigate.engineeringmanagement.planning.payload.response.MonthlyUtilizationReport;
import com.digigate.engineeringmanagement.planning.payload.response.YearlyUtilizationReport;
import com.digigate.engineeringmanagement.planning.service.DailyUtilizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * Daily Utilization Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/utilization")
public class DailyUtilizationController {

    private final DailyUtilizationService dailyUtilizationService;

    @Autowired
    public DailyUtilizationController(DailyUtilizationService dailyUtilizationService) {
        this.dailyUtilizationService = dailyUtilizationService;
    }

    @PostMapping("/daily_report")
    public ResponseEntity<DailyUtilizationReportList> dailyUtilizationReport(
            @RequestBody @Valid DailyUtilizationSearchDto dailyUtilizationSearchDto) {
        DailyUtilizationReportList report = dailyUtilizationService.getDailyUtilizationReport(
                dailyUtilizationSearchDto.getFromDate(), dailyUtilizationSearchDto.getToDate(),
                dailyUtilizationSearchDto.getAircraftId());
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PostMapping("/monthly_report")
    public ResponseEntity<List<MonthlyUtilizationReport>> getMonthlyUtilizationReport(
            @RequestBody @Valid DailyUtilizationSearchDto dailyUtilizationSearchDto) {
        List<MonthlyUtilizationReport> monthlyUtilizationReportList = dailyUtilizationService.getMonthlyUtilizationReport(
                dailyUtilizationSearchDto.getFromDate(), dailyUtilizationSearchDto.getToDate(),
                dailyUtilizationSearchDto.getAircraftId());
        return new ResponseEntity<>(monthlyUtilizationReportList, HttpStatus.OK);
    }

    @PostMapping("/yearly_report")
    public ResponseEntity<List<YearlyUtilizationReport>> getYearlyUtilizationReport(
            @RequestBody @Valid DailyUtilizationSearchDto dailyUtilizationSearchDto) {
        List<YearlyUtilizationReport> monthlyUtilizationReportList = dailyUtilizationService.getYearlyUtilizationReport(
                dailyUtilizationSearchDto.getFromDate(), dailyUtilizationSearchDto.getToDate(),
                dailyUtilizationSearchDto.getAircraftId());
        return new ResponseEntity<>(monthlyUtilizationReportList, HttpStatus.OK);
    }



    @GetMapping("/save_monthly_utilization")
    public ResponseEntity<HttpStatus> generateAndSaveMonthlyUtilization() {
        dailyUtilizationService.generateAndSaveMonthlyUtilization();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
