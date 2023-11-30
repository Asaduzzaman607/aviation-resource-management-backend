package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.DashboardItem;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DashboardService {

    PageData getAircraftDashboard(Long aircraftIdModelId, Pageable pageable);

    void processAndSaveDashboardLdndDueDate();
}
