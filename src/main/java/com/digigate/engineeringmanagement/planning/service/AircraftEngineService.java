package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.AircraftEngineDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEngineSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineLlpStatusReportDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftEngineTmmRgbViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineInfoViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.EngineLlpStatusReportViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Aircraft Engine Service
 *
 * @author Pranoy Das
 */
public interface AircraftEngineService {
    EngineLlpStatusReportViewModel generateEngineLlpStatusReport(
            EngineLlpStatusReportDto engineLlpStatusReportDto);

    String saveOrUpdateEngineTmmRgbInfo(AircraftEngineDto aircraftEngineDto);

    Page<EngineInfoViewModel> searchAircraftEngineInfo(AircraftEngineSearchDto aircraftEngineSearchDto,
                                                       Pageable pageable);

    AircraftEngineTmmRgbViewModel findEngineTmmRgbInfoByAircraftBuild(Long aircraftBuildId);

    EngineLlpStatusReportViewModel generateInactivateEngineLlpStatusReport(EngineLlpStatusReportDto engineLlpStatusReportDto);
}
