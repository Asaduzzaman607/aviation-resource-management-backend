package com.digigate.engineeringmanagement.common.service.erpDataSync;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Department;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.projection.SelectionProjection;
import com.digigate.engineeringmanagement.common.payload.request.erp.DepartmentDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.SectionDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.section.SectionResponseDto;
import com.digigate.engineeringmanagement.common.repository.erpDataSync.SectionRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.webClient.RestConsumer;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.DepartmentProjection;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SectionService extends AbstractSearchService<Section, SectionDto, ERPSearchRequestDto> {
    private final SectionRepository sectionRepository;
    private final DepartmentService departmentService;

    public SectionService(SectionRepository sectionRepository, DepartmentService departmentService) {
        super(sectionRepository);
        this.sectionRepository = sectionRepository;
        this.departmentService = departmentService;
    }


    @Override
    public PageData search(ERPSearchRequestDto searchDto, Pageable pageable) {
        Specification<Section> sectionSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Section>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Section> sectionPage = sectionRepository.findAll(sectionSpecification, pageable);
        return PageData.builder()
                .model(getDataFromParents(sectionPage.getContent()))
                .totalPages(sectionPage.getTotalPages())
                .totalElements(sectionPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public List<Section> findByErpIdIn(Set<Long> ids) {
        return sectionRepository.findByErpIdIn(ids);
    }

    private List<SectionResponseDto> getDataFromParents(List<Section> sectionList) {
        Set<Long> departmentIds = sectionList.stream()
                .map(Section::getDepartmentId).collect(Collectors.toSet());
        Map<Long, DepartmentProjection> departmentProjectionMap = departmentService.findByIdIn(departmentIds).stream()
                .collect(Collectors.toMap(DepartmentProjection::getId, Function.identity()));
        List<SectionResponseDto> sectionResponseDtos = sectionList.stream().map(section ->
                        convertToSectionResponseDto(section, departmentProjectionMap.get(section.getDepartmentId())))
                .collect(Collectors.toList());
        return sectionResponseDtos;
    }

    @Override
    public SectionResponseDto getSingle(Long id) {
        Section section = findByIdUnfiltered(id);
        Long departmentId = section.getDepartmentId();
        Map<Long, DepartmentProjection> departmentProjectionMap = departmentService.findByIdIn(Collections.singleton(departmentId)).stream()
                .collect(Collectors.toMap(DepartmentProjection::getId, Function.identity()));

        SectionResponseDto sectionResponseDto = convertToSectionResponseDto(section, departmentProjectionMap.get(departmentId));
        return sectionResponseDto;
    }

    private SectionResponseDto convertToSectionResponseDto(Section entity, DepartmentProjection departmentProjection) {
        SectionResponseDto responseDto = new SectionResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setName(entity.getName());
        responseDto.setErpId(entity.getErpId());
        if (Objects.nonNull(departmentProjection)) {
            responseDto.setDepartment(DepartmentResponseDto.builder()
                    .id(departmentProjection.getId())
                    .code(departmentProjection.getCode())
                    .companyId(departmentProjection.getCompanyId())
                    .info(departmentProjection.getInfo())
                    .name(departmentProjection.getName())
                    .build());
        }
        return responseDto;
    }

    @Override
    protected Section convertToResponseDto(Section section) {
        Section newSection = new Section();
        newSection.setName(section.getName());
        newSection.setDepartmentId(section.getDepartmentId());
        newSection.setId(section.getId());
        return newSection;
    }

    @Override
    protected Section convertToEntity(SectionDto sectionDto) {
        return populateEntity(sectionDto, new Section());
    }


    @Override
    protected Section updateEntity(SectionDto dto, Section entity) {
        return populateEntity(dto, entity);
    }

    private Section populateEntity(SectionDto sectionDto, Section section) {
        validate(sectionDto, section);
        section.setDepartment(departmentService.findById(sectionDto.getDepartmentId()));
        section.setErpId(sectionDto.getErpId());
        section.setName(sectionDto.getName());
        return section;
    }

    public Set<SelectionProjection> findByIdIn(Set<Long> sectionIds) {
        return sectionRepository.findByIdIn(sectionIds);
    }

    @Override
    protected Specification<Section> buildSpecification(ERPSearchRequestDto searchDto) {
        CustomSpecification<Section> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getId(),ApplicationConstant.DEPARTMENT_ID))
        );
    }

    private void validate(SectionDto dto, Section old) {
        List<Section> sections = sectionRepository.findByDepartmentIdAndNameAndIsActiveTrue(dto.getDepartmentId(), dto.getName());
        if (!CollectionUtils.isEmpty(sections)) {
            sections.forEach(section -> {
                if (Objects.nonNull(old) && section.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.SECTION_NAME_UNDER_THIS_DEPARTMENT_ALREADY_EXIST);
            });
        }
    }
}
