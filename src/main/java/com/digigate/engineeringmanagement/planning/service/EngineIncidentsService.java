package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.EngineIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.EngineIncidentsReportViewModel;
import org.springframework.data.domain.Pageable;

/**
 * Engine Incidents Service
 *
 * @author Nafiul Islam
 */
public interface EngineIncidentsService {

    PageData searchEngineIncidents(EngineIncidentsSearchDto engineIncidentsSearchDto, Pageable pageable);

    EngineIncidentsReportViewModel engineIncidentsReport(EngineIncidentsSearchDto engineIncidentsSearchDto);
}
