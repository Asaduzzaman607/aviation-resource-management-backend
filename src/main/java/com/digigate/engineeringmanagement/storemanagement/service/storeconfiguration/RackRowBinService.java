package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRow;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.RackRowBin;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowBinProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackRowBinDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowBinResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.RackRowBinRepository;
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

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class RackRowBinService extends AbstractSearchService<RackRowBin, RackRowBinDto, IdQuerySearchDto> {
    private final RackRowService rackRowService;
    private final RackRowBinRepository rackRowBinRepository;
    private final StorePartAvailabilityService storePartAvailabilityService;

    public RackRowBinService(RackRowService rackRowService,
                             RackRowBinRepository rackRowBinRepository,
                             @Lazy StorePartAvailabilityService storePartAvailabilityService) {
        super(rackRowBinRepository);
        this.rackRowService = rackRowService;
        this.rackRowBinRepository = rackRowBinRepository;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }

    @Override
    public RackRowBinResponseDto getSingle(Long id) {
        List<RackRowBinResponseDto> responseDtos =
                getDataFromParents(Collections.singletonList(super.findByIdUnfiltered(id)));
        return CollectionUtils.isEmpty(responseDtos) ? null : responseDtos.get(ApplicationConstant.LIST_FIRST_INDEX);
    }

    /**
     * Custom update status with dependency
     *
     * @param id       for update status
     * @param isActive boolean
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && storePartAvailabilityService.existsByRackRowBinIdAndIsActiveTrue(id)) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
        }

        RackRowBin rackRowBin = findByIdUnfiltered(id);
        if (rackRowBin.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        if (isActive == TRUE) {
            if (rackRowService.findOptionalById(rackRowBin.getRackRowId(), true).isEmpty()) {
                throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.PARENT_DATA_EXISTS, RACK_ROW));
            }
            RackRowBinDto rackRowBinDto = new RackRowBinDto();
            rackRowBinDto.setRackRowId(rackRowBin.getRackRowId());
            rackRowBinDto.setRackRowBinCode(rackRowBin.getCode());
            validate(rackRowBinDto, null);
        }
        rackRowBin.setIsActive(isActive);
        saveItem(rackRowBin);
    }

    /**
     * Check dependency
     */
    public boolean existByRackRow(Long rackId) {
        return rackRowBinRepository.existsByRackRowIdAndIsActiveTrue(rackId);
    }

    public List<RackRowBin> findByIdIn(List<Long> ids){return rackRowBinRepository.findByIdIn(ids);}

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<RackRowBin> rackRowBinSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<RackRowBin>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<RackRowBin> pagedData = rackRowBinRepository.findAll(rackRowBinSpecification, pageable);
        List<RackRowBin> content = pagedData.getContent();
        return PageData.builder()
                .model(getDataFromParents(content))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public List<RackRowBinResponseDto> getDataFromParents(List<RackRowBin> rackRowBinList) {
        Set<Long> rackRowIds = rackRowBinList.stream()
                .map(RackRowBin::getRackRowId).collect(Collectors.toSet());
        Map<Long, RackRowBinProjection> rackRowProjectionMap = rackRowService.findByIdIn(rackRowIds).stream()
                .collect(Collectors.toMap(RackRowBinProjection::getId, Function.identity()));
        List<RackRowBinResponseDto> rackRowBinResponseDtoList = rackRowBinList.stream().map(rackRowBin ->
                        convertToRackRowBinResponseDto(rackRowBin, rackRowProjectionMap.get(rackRowBin.getRackRowId())))
                .collect(Collectors.toList());
        return rackRowBinResponseDtoList;
    }

    public RackRowBinResponseDto convertToRackRowBinResponseDto(RackRowBin entity,
                                                                 RackRowBinProjection rackRowBinProjection) {
        RackRowBinResponseDto responseDto = new RackRowBinResponseDto();
        responseDto.setRackRowBinId(entity.getId());
        responseDto.setRackRowBinCode(entity.getCode());
        if (Objects.nonNull(rackRowBinProjection)) {
            responseDto.setRackRowId(rackRowBinProjection.getId());
            responseDto.setRackRowCode(rackRowBinProjection.getCode());
            responseDto.setRackId(rackRowBinProjection.getRackId());
            responseDto.setRackCode(rackRowBinProjection.getRackCode());
            responseDto.setRoomId(rackRowBinProjection.getRackRoomId());
            responseDto.setRoomName(rackRowBinProjection.getRackRoomName());
            responseDto.setRoomCode(rackRowBinProjection.getRackRoomCode());
            responseDto.setOfficeCode(rackRowBinProjection.getRackRoomOfficeCode());
            responseDto.setOfficeId(rackRowBinProjection.getRackRoomOfficeId());
        }
        return responseDto;
    }

    /**
     * This method is responsible for validating case-sensitive rack-row-bin code
     *
     * @param dto {@link RackRowBinDto}
     * @param old {@link RackRowBin}
     */
    private void validate(RackRowBinDto dto, RackRowBin old) {
        List<RackRowBin> rackRowBinList = rackRowBinRepository.
                findByRackRowIdAndCodeIgnoreCaseAndIsActiveTrue(
                        dto.getRackRowId(),
                        dto.getRackRowBinCode());

        if (CollectionUtils.isNotEmpty(rackRowBinList) && (
                Objects.isNull(old) ||
                        rackRowBinList.size() > VALUE_ONE ||
                        !rackRowBinList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.CODE_ALREADY_EXIST);
        }
    }

    @Override
    protected RackRowBin convertToEntity(RackRowBinDto rackRowBinDto) {
        validate(rackRowBinDto, null);
        RackRow rackRow = rackRowService.findById(rackRowBinDto.getRackRowId());
        RackRowBin rackRowBin = new RackRowBin();
        rackRowBin.setCode(rackRowBinDto.getRackRowBinCode());
        rackRowBin.setRackRow(rackRow);
        return rackRowBin;
    }

    @Override
    protected RackRowBinDto convertToResponseDto(RackRowBin rackRowBin) {
        RackRow rackRow = rackRowBin.getRackRow();
        return RackRowBinDto.builder()
                .rackRowBinId(rackRowBin.getId())
                .rackRowBinCode(rackRowBin.getCode())
                .rackRowId(rackRow.getId())
                .build();
    }

    @Override
    protected RackRowBin updateEntity(RackRowBinDto dto, RackRowBin entity) {
        validate(dto, entity);
        entity.setCode(dto.getRackRowBinCode());
        entity.setRackRow(rackRowService.findById(dto.getRackRowId()));
        return entity;
    }

    @Override
    protected Specification<RackRowBin> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<RackRowBin> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getId(), ApplicationConstant.RACK_ROW_ID))
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }
}
