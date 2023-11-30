package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.planning.entity.ModelTree;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.request.PositionDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeExcelViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.PositionModelView;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ModelTreeIService {
    List<ModelTreeViewModel> getLowerModelList(Long modelId);
    List<ModelTreeViewModel> findLocationAndPosition(Long higherModelId, Long modelId);
    ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId);
    List<ModelTree> findAllModelTreeByAircraftId(Set<Long> modelIds);
    Optional<Long> findIdForUniqueEntry(Long modelId, Long higherModelId, Long locationId, Long positionId);
    List<PositionDto> getPositionListByModelId(Long modelId);
    List<PositionModelView> getPositionsByModelIds(Set<Long> modelId);

    List<ModelTreeExcelViewModel> getAllModelTreeList();
}
