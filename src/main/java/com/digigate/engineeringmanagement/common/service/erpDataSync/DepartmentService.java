package com.digigate.engineeringmanagement.common.service.erpDataSync;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.erp.DepartmentDto;
import com.digigate.engineeringmanagement.common.payload.request.search.DepartmentSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import com.digigate.engineeringmanagement.common.repository.erpDataSync.DepartmentRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.DepartmentProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.CurrencyRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Boolean.FALSE;

@Service
public class DepartmentService extends AbstractSearchService<Department, DepartmentDto, DepartmentSearchDto> {
    private final DepartmentRepository departmentRepository;
    private final StoreReturnService storeReturnService;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository,
                             @Lazy StoreReturnService storeReturnService) {
        super(departmentRepository);
        this.departmentRepository = departmentRepository;
        this.storeReturnService = storeReturnService;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && storeReturnService.existsByDepartmentIdAndIsActiveTrue(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        super.updateActiveStatus(id, isActive);
    }

    public List<Department> findByErpIdIn(Set<Long> ids) {
        return departmentRepository.findByErpIdIn(ids);
    }

    @Override
    protected DepartmentResponseDto convertToResponseDto(Department department) {
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .erpId(department.getErpId())
                .companyId(department.getCompanyId())
                .info(department.getInfo())
                .build();
    }

    @Override
    protected Department convertToEntity(DepartmentDto departmentDto) {
        return populateEntity(departmentDto,new Department());
    }

    @Override
    protected Department updateEntity(DepartmentDto dto, Department entity) {
        return populateEntity(dto,entity);
    }

    private Department populateEntity(DepartmentDto dto, Department entity) {
        validate(dto,entity);
        entity.setName(dto.getName());
        entity.setCompanyId(dto.getCompanyId());
        entity.setErpId(dto.getErpId());
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setInfo(dto.getInfo());
        return  entity;
    }

    public Set<DepartmentProjection> findByIdIn(Set<Long> departmentIds) {
        return departmentRepository.findByIdIn(departmentIds);
    }

    @Override
    protected Specification<Department> buildSpecification(DepartmentSearchDto searchDto) {
        CustomSpecification<Department> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)
                        .or(customSpecification.likeSpecificationAtRoot(searchDto.getCode(), ApplicationConstant.ENTITY_CODE))
                        .and(customSpecification.active(searchDto.getIsActive(),ApplicationConstant.IS_ACTIVE_FIELD))
        );
    }
    private void validate(DepartmentDto dto, Department old) {
        List<Department> departments = departmentRepository.findByNameAndIsActiveTrue(dto.getName());
        if (!CollectionUtils.isEmpty(departments)) {
            departments.forEach(department -> {
                if (Objects.nonNull(old) && department.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.DEPARTMENT_NAME_EXISTS);
            });
        }
    }
}
