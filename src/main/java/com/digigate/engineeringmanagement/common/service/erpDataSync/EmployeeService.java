package com.digigate.engineeringmanagement.common.service.erpDataSync;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.projection.DesignationProjection;
import com.digigate.engineeringmanagement.common.payload.projection.EmployeeProjection;
import com.digigate.engineeringmanagement.common.payload.request.erp.EmployeeDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.SectionDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.EmployeeResponseDto;
import com.digigate.engineeringmanagement.common.repository.erpDataSync.EmployeeRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmployeeService extends AbstractSearchService<Employee, EmployeeDto, ERPSearchRequestDto> {
    private final EmployeeRepository employeeRepository;
    private final DesignationService designationService;

    public EmployeeService(EmployeeRepository employeeRepository, DesignationService designationService) {
        super(employeeRepository);
        this.employeeRepository = employeeRepository;
        this.designationService = designationService;
    }

    @Override
    public PageData search(ERPSearchRequestDto searchDto, Pageable pageable) {
        Specification<Employee> employeeSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Employee>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Employee> employeePage = employeeRepository.findAll(employeeSpecification, pageable);
        return PageData.builder()
                .model(getDataFromParents(employeePage.getContent()))
                .totalPages(employeePage.getTotalPages())
                .totalElements(employeePage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private List<EmployeeResponseDto> getDataFromParents(List<Employee> employeeList) {
        Set<Long> designationIds = employeeList.stream()
                .map(Employee::getDesignationId).collect(Collectors.toSet());
        Map<Long, DesignationProjection> employeeProjectionMap = designationService.findByIdIn(designationIds).stream()
                .collect(Collectors.toMap(DesignationProjection::getId, Function.identity()));
        return employeeList.stream().map(employee ->
                        convertToEmployeeResponseDto(employee, employeeProjectionMap.get(employee.getDesignationId())))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDto getSingle(Long id) {
        Employee employee = findByIdUnfiltered(id);
        Long designationId = employee.getDesignationId();
        Map<Long, DesignationProjection> employeeProjectionMap = designationService.findByIdIn(Collections.singleton(designationId)).stream()
                .collect(Collectors.toMap(DesignationProjection::getId, Function.identity()));
        return convertToEmployeeResponseDto(employee, employeeProjectionMap.get(designationId));
    }

    private EmployeeResponseDto convertToEmployeeResponseDto(Employee entity, DesignationProjection designationProjection) {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setName(entity.getName());
        responseDto.setCode(entity.getCode());
        responseDto.setPresentAddress(entity.getPresentAddress());
        responseDto.setFatherName(entity.getFatherName());
        responseDto.setMotherName(entity.getMotherName());
        responseDto.setNationalId(entity.getNationalId());
        responseDto.setPassport(entity.getPassport());
        responseDto.setActivationCode(entity.getActivationCode());
        responseDto.setEmail(entity.getEmail());
        responseDto.setOfficeMobile(entity.getOfficeMobile());
        responseDto.setOfficePhone(entity.getOfficePhone());
        responseDto.setPermanentAddress(entity.getPermanentAddress());
        responseDto.setResidentPhone(entity.getResidentPhone());
        responseDto.setBloodGroup(entity.getBloodGroup());
        responseDto.setResidentMobile(entity.getResidentMobile());
        responseDto.setErpId(entity.getErpId());
        if (Objects.nonNull(designationProjection)) {
            responseDto.setDesignation(IdNameResponse.of(designationProjection.getId(), designationProjection.getName()));
            responseDto.setSection(IdNameResponse.of(designationProjection.getSectionId(), designationProjection.getSectionName()));
            responseDto.setDepartment(DepartmentResponseDto.builder()
                    .id(designationProjection.getSectionDepartmentId())
                    .code(designationProjection.getSectionDepartmentCode())
                    .companyId(designationProjection.getSectionDepartmentCompanyId())
                    .info(designationProjection.getSectionDepartmentInfo())
                    .name(designationProjection.getSectionDepartmentName())
                    .build());
        }
        return responseDto;
    }

    @Override
    protected Employee convertToResponseDto(Employee employee) {
        Employee newEmployee = new Employee();
        newEmployee.setName(employee.getName());
        newEmployee.setDesignationId(employee.getDesignationId());
        newEmployee.setId(employee.getId());
        newEmployee.setEmail(employee.getEmail());
        newEmployee.setOfficePhone(employee.getOfficePhone());
        return newEmployee;
    }

    @Override
    protected Employee convertToEntity(EmployeeDto employeeDto) {
        return populate(employeeDto, new Employee());
    }

    @Override
    protected Employee updateEntity(EmployeeDto dto, Employee entity) {
        return populate(dto, entity);
    }

    private Employee populate(EmployeeDto employeeDto, Employee employee) {
        validate(employeeDto, employee);
        employee.setDesignation(designationService.findById(employeeDto.getDesignationId()));
        employee.setName(employeeDto.getName());
        employee.setCode(employeeDto.getCode());
        employee.setPresentAddress(employeeDto.getPresentAddress());
        employee.setFatherName(employeeDto.getFatherName());
        employee.setMotherName(employeeDto.getMotherName());
        employee.setNationalId(employeeDto.getNationalId());
        employee.setPassport(employeeDto.getPassport());
        employee.setActivationCode(employeeDto.getActivationCode());
        employee.setEmail(employeeDto.getEmail());
        employee.setOfficePhone(employeeDto.getOfficePhone());
        employee.setOfficeMobile(employeeDto.getOfficeMobile());
        employee.setPermanentAddress(employeeDto.getPermanentAddress());
        employee.setResidentPhone(employeeDto.getResidentPhone());
        employee.setResidentMobile(employeeDto.getResidentMobile());
        employee.setBloodGroup(employeeDto.getBloodGroup());
        employee.setErpId(employeeDto.getErpId());
        return employee;
    }

    @Override
    protected Specification<Employee> buildSpecification(ERPSearchRequestDto searchDto) {
        CustomSpecification<Employee> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)
                        .or(customSpecification.likeSpecificationAtRoot(searchDto.getCode(), ApplicationConstant.ENTITY_CODE))
                        .or(customSpecification.likeSpecificationAtRoot(searchDto.getEmail(), ApplicationConstant.ENTITY_EMAIL))
                        .or(customSpecification.equalSpecificationAtRoot(searchDto.getId(),ApplicationConstant.DESIGNATION_ID))
        );
    }

    public List<EmployeeProjection> findByIdIn(Set<Long> empIds) {
        return employeeRepository.findByIdIn(empIds);
    }

    public List<Employee> findByErpIdIn(Set<Long> ids) {
        return employeeRepository.findByErpIdIn(ids);
    }

    private void validate(EmployeeDto dto, Employee old) {
        List<Employee> employees = employeeRepository.findByDesignationIdAndNameAndIsActiveTrue(dto.getDesignationId(), dto.getName());
        if (!CollectionUtils.isEmpty(employees)) {
            employees.forEach(employee -> {
                if (Objects.nonNull(old) && employee.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.EMPLOYEE_UNDER_THIS_DESIGNATION_ALREADY_EXISTS);
            });
        }
    }
}
