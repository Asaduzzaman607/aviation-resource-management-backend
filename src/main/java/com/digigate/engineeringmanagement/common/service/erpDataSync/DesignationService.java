package com.digigate.engineeringmanagement.common.service.erpDataSync;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Designation;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Section;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.projection.DesignationProjection;
import com.digigate.engineeringmanagement.common.payload.projection.SelectionProjection;
import com.digigate.engineeringmanagement.common.payload.request.erp.DesignationDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.SectionDto;
import com.digigate.engineeringmanagement.common.payload.request.search.ERPSearchRequestDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.DesignationModelDto;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.designation.DesignationResponseDto;
import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import com.digigate.engineeringmanagement.common.repository.erpDataSync.DesignationRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.webClient.RestConsumer;
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
public class DesignationService extends AbstractSearchService<Designation, DesignationDto, ERPSearchRequestDto> {
    private final DesignationRepository designationRepository;
    private final SectionService sectionService;

    public DesignationService(DesignationRepository repository, DesignationRepository designationRepository,
                              SectionService sectionService) {
        super(repository);
        this.designationRepository = designationRepository;
        this.sectionService = sectionService;
    }

    @Override
    public PageData search(ERPSearchRequestDto searchDto, Pageable pageable) {
        Specification<Designation> specification = buildSpecification(searchDto)
                .and(new CustomSpecification<Designation>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Designation> designations = designationRepository.findAll(specification, pageable);
        return PageData.builder()
                .model(getDataFromParents(designations.getContent()))
                .totalPages(designations.getTotalPages())
                .totalElements(designations.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private List<DesignationResponseDto> getDataFromParents(List<Designation> designationList) {
        Set<Long> sectionsIds = designationList.stream()
                .map(Designation::getSectionId).collect(Collectors.toSet());
        Map<Long, SelectionProjection> designationProjectionMap = sectionService.findByIdIn(sectionsIds).stream()
                .collect(Collectors.toMap(SelectionProjection::getId, Function.identity()));
        return designationList.stream().map(designation ->
                        convertToDesignationResponseDto(designation, designationProjectionMap.get(designation.getSectionId())))
                .collect(Collectors.toList());
    }

    @Override
    public DesignationResponseDto getSingle(Long id) {
        Designation designation = findByIdUnfiltered(id);
        Long sectionId = designation.getSectionId();
        Map<Long, SelectionProjection> designationProjectionMap = sectionService.findByIdIn(Collections.singleton(sectionId)).stream()
                .collect(Collectors.toMap(SelectionProjection::getId, Function.identity()));
        return convertToDesignationResponseDto(designation, designationProjectionMap.get(sectionId));
    }

    public List<Designation> findByErpIdIn(Set<Long> ids) {
        return designationRepository.findByErpIdIn(ids);
    }

    private DesignationResponseDto convertToDesignationResponseDto(Designation entity, SelectionProjection selectionProjection) {
        DesignationResponseDto responseDto = new DesignationResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setName(entity.getName());
        responseDto.setErpId(entity.getErpId());
        if (Objects.nonNull(selectionProjection)) {
            responseDto.setSection(IdNameResponse.of(selectionProjection.getId(), selectionProjection.getName()));
            responseDto.setDepartment(DepartmentResponseDto.builder()
                    .id(selectionProjection.getDepartmentId())
                    .code(selectionProjection.getDepartmentCode())
                    .companyId(selectionProjection.getDepartmentCompanyId())
                    .info(selectionProjection.getDepartmentInfo())
                    .name(selectionProjection.getDepartmentName())
                    .build());
        }
        return responseDto;
    }

    @Override
    protected Designation convertToResponseDto(Designation designation) {
        Designation newDesignation = new Designation();
        newDesignation.setName(designation.getName());
        newDesignation.setSectionId(designation.getSectionId());
        newDesignation.setId(designation.getId());
        return newDesignation;
    }

    @Override
    protected Designation convertToEntity(DesignationDto designationDto) {
        return populateEntity(designationDto, new Designation());
    }

    private Designation populateEntity(DesignationDto designationDto, Designation designation) {
        validate(designationDto,designation);
        designation.setName(designationDto.getName());
        designation.setSection(sectionService.findById(designationDto.getSectionId()));
        designation.setErpId(designationDto.getErpId());
        return designation;
    }

    @Override
    protected Designation updateEntity(DesignationDto dto, Designation entity) {
        return populateEntity(dto, entity);
    }

    public Set<DesignationProjection> findByIdIn(Set<Long> designationsIds) {
        return designationRepository.findByIdIn(designationsIds);
    }

    @Override
    protected Specification<Designation> buildSpecification(ERPSearchRequestDto searchDto) {
        CustomSpecification<Designation> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getId(),ApplicationConstant.SECTION_ID))
        );
    }
    private void validate(DesignationDto dto, Designation old) {
        List<Designation> designations = designationRepository.findBySectionIdAndNameAndIsActiveTrue(dto.getSectionId(), dto.getName());
        if (!CollectionUtils.isEmpty(designations)) {
            designations.forEach(designation -> {
                if (Objects.nonNull(old) && designation.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.DESIGNATION_NAME_UNDER_THIS_SECTION_ALREADY_EXIST);
            });
        }
    }
}
