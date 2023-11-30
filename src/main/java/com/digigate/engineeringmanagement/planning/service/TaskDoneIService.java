package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSaveDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface TaskDoneIService {

    List<TaskDonePositionDto> getTaskPositionByTaskId(Long taskId);

    List<TaskDoneSaveDto> getTaskAndPositionByAircraftId(Long aircraftId);

    List<LdndDataViewModel> getLdndListByAircraftAndDueDate(LdndDataFindDto searchDto);

    PageData searchTaskDone(TaskDoneSearchDto searchDto, Pageable pageable);

    Set<AcPartSerialResponse> findAcPartSerialResponse(Long aircraftId, Long modelId);

    ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftId);
}
