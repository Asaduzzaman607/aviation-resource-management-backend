package com.digigate.engineeringmanagement.logistic.service;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.logistic.entity.DutyFee;
import com.digigate.engineeringmanagement.logistic.payload.request.DutyFeeRequestDto;
import com.digigate.engineeringmanagement.logistic.payload.response.DutyFeeResponseDto;
import com.digigate.engineeringmanagement.logistic.payload.response.DutyPartViewModel;
import com.digigate.engineeringmanagement.logistic.repository.DutyFeeRepository;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartsInvoiceItemService;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DutyFeeService extends AbstractSearchService<DutyFee, DutyFeeRequestDto, IdQuerySearchDto> {

    private final PartsInvoiceItemService partsInvoiceItemService;
    private final GenericAttachmentService genericAttachmentService;
    private final DutyFeeItemService dutyFeeItemService;
    private final DutyFeeRepository repository;


    public DutyFeeService(PartsInvoiceItemService partsInvoiceItemService,
                          GenericAttachmentService genericAttachmentService, DutyFeeItemService dutyFeeItemService, DutyFeeRepository repository) {
        super(repository);
        this.partsInvoiceItemService = partsInvoiceItemService;
        this.genericAttachmentService = genericAttachmentService;
        this.dutyFeeItemService = dutyFeeItemService;
        this.repository = repository;
    }

    @Override
    public DutyFee create(DutyFeeRequestDto dutyFeeRequestDto) {
        DutyFee dutyFee = convertToEntity(dutyFeeRequestDto);
        DutyFee entity = super.saveItem(dutyFee);
        if (Objects.nonNull(dutyFeeRequestDto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(dutyFeeRequestDto.getAttachment(), FeatureName.DUTY_FEES, entity.getId());
        }
        dutyFeeItemService.saveAll(dutyFeeRequestDto.getDutyFeeItemRequestDtoList(), entity);
        return dutyFee;

    }

    private DutyFeeResponseDto convertToResponseDto(DutyFee dutyFee, Set<String> attachmentLinks) {
        DutyFeeResponseDto responseDto = new DutyFeeResponseDto();
        responseDto.setId(dutyFee.getId());
        if (Objects.nonNull(dutyFee.getPartsInvoiceItem())) {
            responseDto.setInvoiceNo(dutyFee.getPartsInvoiceItem().getPartsInvoice().getInvoiceNo());
            responseDto.setPartInvoiceId(dutyFee.getPartsInvoiceItem().getPartsInvoice().getId());
            DutyPartViewModel dutyPartViewModel = repository.findByDutyFeeId(dutyFee.getId());
            if(dutyPartViewModel!=null) {
                responseDto.setPartId(dutyPartViewModel.getId());
                responseDto.setPartNo(dutyPartViewModel.getPartNo());
            }

        }
        responseDto.setPartsInvoiceItemId(dutyFee.getPartsInvoiceItemId());
        responseDto.setDutyFeeItemList(dutyFeeItemService.getResponseData(dutyFee.getId()));
        responseDto.setAttachment(attachmentLinks);

        return responseDto;
    }

    @Override
    protected DutyFee convertToEntity(DutyFeeRequestDto dutyFeeRequestDto) {
        DutyFee dutyFee = new DutyFee();
        if (Objects.nonNull(dutyFeeRequestDto.getPartsInvoiceItemId())) {
            dutyFee.setPartsInvoiceItem(partsInvoiceItemService.findById(dutyFeeRequestDto.getPartsInvoiceItemId()));
        }
        return dutyFee;
    }


    @Override
    public DutyFee update(DutyFeeRequestDto dutyFeeRequestDto, Long id) {
        DutyFee dutyFee = findByIdUnfiltered(id);
        DutyFee entity = updateEntity(dutyFeeRequestDto, dutyFee);
        if (Objects.nonNull(dutyFeeRequestDto.getAttachment())) {
            genericAttachmentService.updateByRecordId(FeatureName.DUTY_FEES, entity.getId(), dutyFeeRequestDto.getAttachment());
        }
        dutyFeeItemService.updateAll(dutyFeeRequestDto.getDutyFeeItemRequestDtoList(), dutyFee);
        return super.saveItem(entity);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        DutyFee dutyFee = findByIdUnfiltered(id);
        dutyFee.setIsActive(isActive);
        saveItem(dutyFee);
    }

    @Override
    protected <T> T convertToResponseDto(DutyFee dutyFee) {
        return null;
    }

    @Override
    protected DutyFee updateEntity(DutyFeeRequestDto dto, DutyFee entity) {
        if (Objects.nonNull(dto.getPartsInvoiceItemId())) {
            entity.setPartsInvoiceItem(partsInvoiceItemService.findById(dto.getPartsInvoiceItemId()));
        }
        return entity;
    }

    private List<DutyFeeResponseDto> getResponseData(List<DutyFee> dutyFees) {

        Set<Long> dutyFeeIds = dutyFees.stream().map(DutyFee::getId).collect(Collectors.toSet());
        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.DUTY_FEES, dutyFeeIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        return dutyFees
                .stream()
                .map(dutyFee -> convertToResponseDto(
                        dutyFee,
                        attachmentLinksMap.get(dutyFee.getId())))
                .collect(Collectors.toList());

    }

    @Override
    public DutyFeeResponseDto getSingle(Long id) {
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id))).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));

    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<DutyFee> pagedData = repository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pagedData.getContent()))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }
    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<DutyFee> pageData = repository.findAll(buildSpecification(searchDto),pageable);
        return PageData.builder()
                .model(getResponseData(pageData.getContent()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected Specification<DutyFee> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<DutyFee> customSpecification = new CustomSpecification<>();
     return  Specification.where(
                        customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD)
                                .and(customSpecification.likeSpecificationAtSecondLayerChild(searchDto.getQuery(),
                                ApplicationConstant.PARTS_INVOICE_ITEM,ApplicationConstant.PARTS_INVOICE,ApplicationConstant.INVOICE_NO))
        );
    }
}
