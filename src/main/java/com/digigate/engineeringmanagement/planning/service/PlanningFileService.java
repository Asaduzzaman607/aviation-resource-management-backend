package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.PlanningFile;
import com.digigate.engineeringmanagement.planning.payload.request.FileSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.PlanningFileDto;
import com.digigate.engineeringmanagement.planning.payload.request.ValidateMatchStringDto;
import com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel;

import java.util.List;

/**
 * PlanningFile Service
 *
 * @author Junaid Khan Pathan
 */

public interface PlanningFileService {
    PlanningFile getPlanningFileById(Long id);
    List<PlanningFileViewModel> getAllPlanningFilesByFolderId(Long folderId);
//    PlanningFileViewModel renamePlanningFile(PlanningFileDto planningFileDto, Long id);
    List<PlanningFileViewModel> getPlanningFilesBySearchKeyword(FileSearchDto fileSearchDto);

//    PlanningFileViewModel getPlanningFileByMatchString(String matchString);

    void uploadFile(List<PlanningFileDto> planningFileDto);

//    void validateMatchString(List<ValidateMatchStringDto> validateMatchStringDto);
}
