package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftIncidentsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.IncidentsStatisticsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.IncidentsStatisticsViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TechIncViewModel;
import org.springframework.data.domain.Pageable;
/**
 * Aircraft Incidents Service
 *
 * @author Nafiul Islam
 */
public interface AircraftIncidentsService {

    PageData searchAircraftIncidents(AircraftIncidentsSearchDto aircraftIncidentsSearchDto, Pageable pageable);

    IncidentsStatisticsViewModel incidentStatisticsReport(IncidentsStatisticsSearchDto searchDto);

    TechIncViewModel techIncReport(IncidentsStatisticsSearchDto searchDto);
}
