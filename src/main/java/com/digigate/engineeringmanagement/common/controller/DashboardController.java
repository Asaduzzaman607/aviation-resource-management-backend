package com.digigate.engineeringmanagement.common.controller;

import com.digigate.engineeringmanagement.common.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/part/info")
    public ResponseEntity<?> getPartInfo(@RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate ,
                                         @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return new ResponseEntity<>(dashboardService.findPartInfoAndIsActiveTrue(startDate,
                endDate), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getDashboardData(@RequestParam(value = "month", required = false, defaultValue = "-4") Integer month) {
        return new ResponseEntity<>(dashboardService.getDashboardData(month), HttpStatus.OK);
    }
}
