package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.WorkFLowActionRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.WorkFlowActionSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflow-actions")
public class WorkFlowActionController extends AbstractSearchController<WorkFlowAction, WorkFLowActionRequestDto, WorkFlowActionSearchDto> {
    public WorkFlowActionController(WorkFlowActionService service) {
        super(service);
    }
}
