package com.digigate.engineeringmanagement.planning.service;


import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.DefectDto;
import com.digigate.engineeringmanagement.planning.payload.request.DefectSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DefectService {

    void createDefectBulk(List<DefectDto> defectDtoList);

    PageData searchDefects(DefectSearchDto searchDto, Pageable pageable);

    List<DefectDto> getGeneratedDefectList(List<DefRectSearchViewModel> generatedDefects, Long aircraftId);

    PageData findTopAtaReport(DefectSearchDto searchDto, Pageable pageable);

    PageData findCrrReport(DefectSearchDto searchDto, Pageable pageable);
}
