package com.digigate.engineeringmanagement.common.controller.erpDataSync;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.payload.request.erp.DepartmentDto;
import com.digigate.engineeringmanagement.common.payload.request.search.DepartmentSearchDto;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/department")
public class DepartmentController extends AbstractSearchController<Department, DepartmentDto, DepartmentSearchDto> {
    private final DepartmentService departmentService;
    public DepartmentController(DepartmentService departmentService) {
        super(departmentService);
        this.departmentService = departmentService;
    }

}
