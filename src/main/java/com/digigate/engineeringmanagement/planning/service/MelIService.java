package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Mel;
import com.digigate.engineeringmanagement.planning.payload.request.MelDto;
import com.digigate.engineeringmanagement.planning.payload.request.MelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.DueResponse;
import com.digigate.engineeringmanagement.planning.payload.response.MelViewMode;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MelIService extends IService<Mel, MelDto> {
    PageData searchMelReport(MelSearchDto melSearchDto, Pageable pageable);
    List<MelViewMode> findAllUnclearedMel(Long aircraftId);
    List<DueResponse> findOpenClosestMel(Long aircraftId);
}
