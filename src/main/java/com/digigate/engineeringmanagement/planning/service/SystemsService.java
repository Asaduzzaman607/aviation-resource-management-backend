package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.request.AlertLevelSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.AlertLevelReport;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AlertLevelViewSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemReliabilitySearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.SystemsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AlertLevelListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LocationViewModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Systems Service
 *
 * @author Nafiul Islam
 */
public interface SystemsService {
    PageData searchSystems(SystemsSearchDto systemsSearchDto, Pageable pageable);

    AlertLevelReport alertLevelReport(AlertLevelSearchDto alertLevelSearchDto);

    List<AlertLevelListViewModel> alertLevelListView(AlertLevelViewSearchDto alertLevelViewSearchDto);

    List<LocationViewModel> createSystemReliability(SystemReliabilitySearchDto systemReliabilitySearchDto);
}
