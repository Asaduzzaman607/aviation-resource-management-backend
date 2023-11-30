package com.digigate.engineeringmanagement.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@RestController
@RequestMapping("/erp")
public class ErpApiController {
    @Value("classpath:erp/departments.json")
    String departmentsFile;
    @Value("classpath:erp/sections.json")
    String sectionsFile;
    @Value("classpath:erp/designations.json")
    String designationsFile;
    @Value("classpath:erp/employees.json")
    String employeesFile;
    @Value("classpath:erp/employee.json")
    String singleEmployeeFile;

    @GetMapping("/data")
    public ResponseEntity<String> getData(@RequestParam(value = "company") String company,
                                          @RequestParam(value = "dept_id", required = false) Integer deptId,
                                          @RequestParam(value = "section_id", required = false) Integer sectionId) throws IOException {
        String data = null;
        if (Objects.nonNull(company) && Objects.nonNull(deptId) && Objects.nonNull(sectionId)) {
            data = getData(designationsFile);
        } else if (Objects.nonNull(company) && Objects.nonNull(deptId)) {
            data = getData(sectionsFile);
        } else if (Objects.nonNull(company)) {
            data = getData(departmentsFile);
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/employees")
    public ResponseEntity<String> getEmployees(@RequestParam(value = "company") String company,
                                               @RequestParam(value = "dept_id", required = false) Integer deptId,
                                               @RequestParam(value = "section_id", required = false) Integer sectionId,
                                               @RequestParam(value = "designation_id", required = false) Integer designationId,
                                               @RequestParam(value = "employee_id", required = false) Integer employeeId) throws IOException {
        String data = null;
        if (Objects.nonNull(company)) {
            if (Objects.nonNull(employeeId)) {
                data = getData(singleEmployeeFile);
            } else {
                data = getData(employeesFile);
            }
        }
        return ResponseEntity.ok(data);
    }

    private String getData(String fileLocation) throws IOException {
        File file = ResourceUtils.getFile(fileLocation);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
