package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.payload.request.ModelDto;
import com.digigate.engineeringmanagement.planning.payload.request.ModelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelExcelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface IModelService extends ISearchService<Model, ModelDto, ModelSearchDto> {
    List<ModelResponseByAircraftDto> getModelListByAircraft(Long aircraftId);
    List<ModelResponseByAircraftDto> getModelListByAircraftId(Long aircraftModelId);
    List<ModelResponseByAircraftDto> getConsumableModelByAircraftModelId(Long aircraftModelId);
    ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId);
    Set<Long> findModelIdsByAircraftId(Long aircraftModelId);
    List<Model> findAllModelByAircraftModelId(Long aircraftModelId);

    List<ModelExcelResponseDto> getAllModelList();
}
