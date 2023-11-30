package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeSearchDto;
import com.digigate.engineeringmanagement.planning.entity.TaskType;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task-type")
public class TaskTypeController extends AbstractSearchController<TaskType, TaskTypeDto, TaskTypeSearchDto> {

    public TaskTypeController(ISearchService<TaskType, TaskTypeDto, TaskTypeSearchDto> service) {
        super(service);
    }
}
