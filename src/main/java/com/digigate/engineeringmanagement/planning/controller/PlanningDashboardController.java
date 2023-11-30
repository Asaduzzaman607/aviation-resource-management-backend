package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class PlanningDashboardController {

    private final DashboardService dashboardService;
    private static final String DASHBOARD_DATA_SAVED_PROPERLY = "Dashboard Data Saved Properly";
    @Autowired
    public PlanningDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping("/ac-dashboard/{aircraftIdModelId}")
    public PageData getAircraftDashboard(@PathVariable Long aircraftIdModelId,
                                         @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                 direction = Sort.Direction.ASC) Pageable pageable) {
        return dashboardService.getAircraftDashboard(aircraftIdModelId, pageable);
    }

    @GetMapping("/upsert-due")
    public ResponseEntity<MessageResponse> upsertDueValue() {
        dashboardService.processAndSaveDashboardLdndDueDate();
        return ResponseEntity.ok(new MessageResponse(DASHBOARD_DATA_SAVED_PROPERLY));
    }

}
