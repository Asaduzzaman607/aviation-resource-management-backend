package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ApprovalEmployeeDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approval-employee")
public class ApprovalEmployeeController extends AbstractController<ApprovalEmployee, ApprovalEmployeeDto> {
    public ApprovalEmployeeController(ApprovalEmployeeService service) {
        super(service);
    }
}
