package com.digigate.engineeringmanagement.common.service.impl;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Designation;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.payload.request.erp.*;
import com.digigate.engineeringmanagement.common.service.ErpDataSyncService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DepartmentService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DesignationService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.SectionService;
import com.digigate.engineeringmanagement.common.webClient.RestConsumer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Service
public class ErpDataSyncServiceImpl implements ErpDataSyncService {

    private final RestConsumer restConsumer;
    private final DepartmentService departmentService;
    private final SectionService sectionService;
    private final DesignationService designationService;
    private final EmployeeService employeeService;

    @Autowired
    public ErpDataSyncServiceImpl(RestConsumer restConsumer, DepartmentService departmentService,
                                  SectionService sectionService, DesignationService designationService,
                                  EmployeeService employeeService) {
        this.restConsumer = restConsumer;
        this.departmentService = departmentService;
        this.sectionService = sectionService;
        this.designationService = designationService;
        this.employeeService = employeeService;
    }

    @Override
    public void sync(Boolean isAll) {
        String lastSyncTime = findLastSyncTime(isAll);
        syncDepartments(lastSyncTime);
        syncSection(lastSyncTime);
        syncDesignation(lastSyncTime);
        syncEmployee(lastSyncTime);
    }

    private void syncDepartments(String lastSyncTime) {
        DepartmentSyncDto departmentModel = restConsumer.getDepartmentsAsJson(lastSyncTime);
        List<DepartmentDataDto> dtos = departmentModel.getDepartments();
        Set<Long> ids = dtos.stream().map(DepartmentDataDto::getId).collect(Collectors.toSet());
        Map<Long, Department> departmentMap = departmentService.findByErpIdIn(ids).stream()
            .collect(Collectors.toMap(Department::getErpId, Function.identity()));

        List<Department> departments = dtos.stream().map(dto -> {
                Department department = departmentMap.getOrDefault(dto.getId(), new Department());
                department.setCode(dto.getCode());
                department.setInfo(dto.getInfo());
                department.setName(dto.getName());
                department.setCompanyId(EMPTY_STRING + departmentModel.getCompanyId());
                department.setErpId(dto.getId());
                return department;
            }
        ).collect(Collectors.toList());
        departmentService.saveItemList(departments);
    }

    private void syncSection(String lastSyncTime) {
        SectionSyncDto sectionsAsJson = restConsumer.getSectionsAsJson(lastSyncTime);
        List<SectionDataDto> dtos = sectionsAsJson.getSections();
        Set<Long> ids = dtos.stream().map(SectionDataDto::getId).collect(Collectors.toSet());
        Map<Long, Section> sectionMap = sectionService.findByErpIdIn(ids).stream()
            .collect(Collectors.toMap(Section::getErpId, Function.identity()));

        List<Section> sections = dtos.stream().map(dto -> {
                Section section = sectionMap.getOrDefault(dto.getId(), new Section());
                section.setName(dto.getName());
                section.setErpId(dto.getId());
                section.setDepartment(Department.withId(dto.getDepartmentId()));
                return section;
            }
        ).collect(Collectors.toList());
        sectionService.saveItemList(sections);
    }

    private void syncDesignation(String lastSyncTime) {
        DesignationSyncDto designationsAsJson = restConsumer.getDesignationsAsJson(lastSyncTime);

        List<DesignationDataDto> dtos = designationsAsJson.getDesignations();
        Set<Long> ids = dtos.stream().map(DesignationDataDto::getId).collect(Collectors.toSet());
        Map<Long, Designation> designationMap = designationService.findByErpIdIn(ids).stream()
            .collect(Collectors.toMap(Designation::getErpId, Function.identity()));

        List<Designation> designations = dtos.stream().map(dto -> {
                Designation designation = designationMap.getOrDefault(dto.getId(), new Designation());
                designation.setName(dto.getName());
                designation.setSection(Section.withId(dto.getSectionId()));
                designation.setErpId(dto.getId());
                return designation;
            }
        ).collect(Collectors.toList());
        designationService.saveItemList(designations);
    }

    private void syncEmployee(String lastSyncTime) {
        EmployeeSyncDto employeesAsJson = restConsumer.getEmployeesAsJson(lastSyncTime);
        List<EmployeeDataDto> dtos = employeesAsJson.getEmployees();
        Set<Long> ids = dtos.stream().map(EmployeeDataDto::getId).collect(Collectors.toSet());
        Map<Long, Employee> employeeMap = employeeService.findByErpIdIn(ids).stream()
            .collect(Collectors.toMap(Employee::getErpId, Function.identity()));

        List<Employee> employees = dtos.stream().map(dto -> {
                Employee employee = new Employee();
                BeanUtils.copyProperties(dto, employee);
                employee.setId(employeeMap.containsKey(dto.getId()) ? employeeMap.get(dto.getId()).getId() : null);
                employee.setDesignation(Designation.withId(dto.getDesignationId()));
                employee.setErpId(dto.getId());
                return employee;
            }
        ).collect(Collectors.toList());
        employeeService.saveItemList(employees);
    }

    private String findLastSyncTime(Boolean isAll) {
        String firstMili = "0";
        return isAll == Boolean.TRUE ? firstMili : String.valueOf(LocalDateTime.now().minusDays(2).toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
