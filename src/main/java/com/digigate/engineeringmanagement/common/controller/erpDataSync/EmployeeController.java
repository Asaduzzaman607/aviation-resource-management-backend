package com.digigate.engineeringmanagement.common.controller.erpDataSync;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.payload.request.erp.EmployeeDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController extends AbstractSearchController<Employee, EmployeeDto, ERPSearchRequestDto> {
    public EmployeeController(EmployeeService service) {
        super(service);
    }
}
