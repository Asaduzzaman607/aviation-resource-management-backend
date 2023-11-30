package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Rack;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRow;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRowBin;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowBinProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackRowDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.RackRowRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.VALUE_ONE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class RackRowService extends AbstractSearchService<RackRow, RackRowDto, IdQuerySearchDto> {
    private final RackService rackService;
    private final RackRowBinService rackRowBinService;
    private final RackRowRepository rackRowRepository;
    private final StorePartAvailabilityService storePartAvailabilityService;

    public RackRowService(RackService rackService,
                          @Lazy RackRowBinService rackRowBinService,
                          @Lazy RackRowRepository rackRowRepository,
                          @Lazy StorePartAvailabilityService storePartAvailabilityService) {
        super(rackRowRepository);
        this.rackService = rackService;
        this.rackRowBinService = rackRowBinService;
        this.rackRowRepository = rackRowRepository;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<RackRow> rackRowSpecification = buildSpecification(searchDto);

        Page<RackRow> rackRowPage = rackRowRepository.findAll(rackRowSpecification, pageable);
        List<RackRow> rackRowList = rackRowPage.getContent();
        return PageData.builder()
                .model(getDataFromParents(rackRowList))
                .totalPages(rackRowPage.getTotalPages())
                .totalElements(rackRowPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public RackRowResponseDto getSingle(Long id) {
        List<RackRowResponseDto> responseDtos = getDataFromParents(Collections.singletonList(super.findByIdUnfiltered(id)));
        return CollectionUtils.isEmpty(responseDtos) ? null : responseDtos.get(ApplicationConstant.LIST_FIRST_INDEX);
    }

    /**
     * Check dependency
     */
    public boolean existByRack(Long rackId) {
        return rackRowRepository.existsByRackIdAndIsActiveTrue(rackId);
    }


    public Set<RackRowBinProjection> findByIdIn(Set<Long> ids) {
        return rackRowRepository.findByIdIn(ids);
    }

    public List<RackRow> findByIdIn(List<Long> ids) {
        return rackRowRepository.findByIdIn(ids);
    }
    /**
     * Custom update status with dependency
     *
     * @param id       for update status
     * @param isActive boolean
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE &&(rackRowBinService.existByRackRow(id)
                ||storePartAvailabilityService.existsByRackRowIdAndIsActiveTrue(id))){
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
        }

        RackRow rackRow = findByIdUnfiltered(id);
        if (rackRow.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == TRUE) {
            if (rackService.findOptionalById(rackRow.getRackId(), true).isEmpty()) {
                throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.PARENT_DATA_EXISTS, ApplicationConstant.RACK));
            }
            RackRowDto rackRowDto = new RackRowDto();
            rackRowDto.setRackId(rackRow.getRackId());
            rackRowDto.setRackRowCode(rackRow.getCode());
            validate(rackRowDto, null);
        }
        rackRow.setIsActive(isActive);
        saveItem(rackRow);
    }

    public List<RackRowResponseDto> getDataFromParents(List<RackRow> rackRowList) {
        Set<Long> rackIds = rackRowList.stream()
                .map(RackRow::getRackId).collect(Collectors.toSet());
        Map<Long, RackRowProjection> rackRowProjection = rackService.findByIdIn(rackIds).stream()
                .collect(Collectors.toMap(RackRowProjection::getId, Function.identity()));
        List<RackRowResponseDto> rackRowResponseDtos = rackRowList.stream().map(rackRow ->
                        convertToRackRowResponseDto(rackRow, rackRowProjection.get(rackRow.getRackId())))
                .collect(Collectors.toList());
        return rackRowResponseDtos;
    }

    private RackRowResponseDto convertToRackRowResponseDto(RackRow entity, RackRowProjection rackRowProjection) {
        RackRowResponseDto responseDto = new RackRowResponseDto();
        responseDto.setRackRowId(entity.getId());
        responseDto.setRackRowCode(entity.getCode());
        if (Objects.nonNull(rackRowProjection)) {
            responseDto.setRackId(rackRowProjection.getId());
            responseDto.setRackCode(rackRowProjection.getCode());
            responseDto.setRoomId(rackRowProjection.getRoomId());
            responseDto.setRoomName(rackRowProjection.getRoomName());
            responseDto.setRoomCode(rackRowProjection.getRoomCode());
            responseDto.setOfficeCode(rackRowProjection.getRoomOfficeCode());
            responseDto.setOfficeId(rackRowProjection.getRoomOfficeId());
        }
        return responseDto;
    }

    /**
     * This method is responsible for validating case-sensitive rack-row code
     *
     * @param dto {@link RackRowDto}
     * @param old {@link RackRow}
     */
    private void validate(RackRowDto dto, RackRow old) {
        List<RackRow> rackRowList = rackRowRepository.
                findByRackIdAndCodeIgnoreCaseAndIsActiveTrue(
                        dto.getRackId(),
                        dto.getRackRowCode());

        if (CollectionUtils.isNotEmpty(rackRowList) && (
                Objects.isNull(old) ||
                        rackRowList.size() > VALUE_ONE ||
                        !rackRowList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.CODE_ALREADY_EXIST);
        }
    }

    @Override
    protected RackRowDto convertToResponseDto(RackRow rackRow) {
        Rack rack = rackRow.getRack();
        return RackRowDto.builder()
                .rackRowId(rackRow.getId())
                .rackRowCode(rackRow.getCode())
                .rackId(rack.getId())
                .build();
    }

    @Override
    protected RackRow convertToEntity(RackRowDto rackRowDto) {
        validate(rackRowDto, null);
        Rack rack = rackService.findById(rackRowDto.getRackId());
        RackRow rackRow = new RackRow();
        rackRow.setCode(rackRowDto.getRackRowCode());
        rackRow.setRack(rack);
        return rackRow;
    }

    @Override
    protected RackRow updateEntity(RackRowDto dto, RackRow entity) {
        validate(dto, entity);
        entity.setCode(dto.getRackRowCode());
        entity.setRack(rackService.findById(dto.getRackId()));
        return entity;
    }

    @Override
    protected Specification<RackRow> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<RackRow> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD))
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getId(), ApplicationConstant.RACK_ID))
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }
}
