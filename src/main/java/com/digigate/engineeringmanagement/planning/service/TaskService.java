package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;
import com.digigate.engineeringmanagement.planning.payload.response.ConsumablePartTaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskModelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.TaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskViewModelForAcCheck;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface TaskService {

    List<TaskViewModel> findTaskByAircraftModelId(Long aircraftModelId);

    void saveAircraftSpecificTask(List<AircraftEffectivityTaskDto> aircraftEffectivityTaskDtoList);

    List<AircraftEffectivityTaskDto> getTaskListByAircraft(Long aircraftId);

    List<TaskViewModelForAcCheck> findAllTaskByAircraftModelId(Long acModelId, Double hour, Integer day);

    List<TaskModelResponseDto> getTaskModelByAircraftModelId(Long aircraftModelId);

    ExcelDataResponse uploadTaskExcel(MultipartFile file, Long aircraftModelId);

    ExcelDataResponse importExcelFileAircraftEffectivity(MultipartFile file, Long aircraftModelId);

    ExcelDataResponse importExcelFileTaskProcedure(MultipartFile file, Long aircraftModelId);

    ExcelDataResponse importExcelFileConsumableParts(MultipartFile file, Long aircraftModelId);
}
