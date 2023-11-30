package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeSearchDto;
import com.digigate.engineeringmanagement.planning.entity.TaskType;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeDto;

import java.util.List;

public interface TaskTypeService extends ISearchService<TaskType, TaskTypeDto, TaskTypeSearchDto> {
    List<TaskType> getAllActiveTaskTypes(Boolean isActive);
}
