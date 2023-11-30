package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.StoreStockRoom;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockRoomProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.StoreStockRoomDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.StoreStockRoomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.StoreStockRoomRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.ENTITY_CODE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class StoreStockRoomService extends AbstractSearchService<StoreStockRoom, StoreStockRoomDto, IdQuerySearchDto> {
    private final OfficeService officeService;
    private final StoreStockRoomRepository storeStockRoomRepository;

    public StoreStockRoomService(StoreStockRoomRepository storeStockRoomRepository, OfficeService officeService) {
        super(storeStockRoomRepository);
        this.officeService = officeService;
        this.storeStockRoomRepository = storeStockRoomRepository;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<StoreStockRoom> storeStockRoomSpecification = buildSpecification(searchDto);
        Page<StoreStockRoom> storeStockRoomPage = storeStockRoomRepository.findAll(storeStockRoomSpecification, pageable);
        return PageData.builder()
                .model(getDataFromParents(storeStockRoomPage.getContent()))
                .totalPages(storeStockRoomPage.getTotalPages())
                .totalElements(storeStockRoomPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public StoreStockRoomResponseDto getSingle(Long id) {
        StoreStockRoom storeStockRoom = findByIdUnfiltered(id);
        return getDataFromParents(Collections.singletonList(storeStockRoom)).stream().findFirst().orElseThrow(() -> EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_FOUND));
    }

    @Override
    public StoreStockRoom create(StoreStockRoomDto dto) {
        validate(dto, null);
        return super.create(dto);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive && officeService.findOptionalById(findByIdUnfiltered(id).getOfficeId(), true).isEmpty()) {
            throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.PARENT_DATA_EXISTS, ApplicationConstant.OFFICE));
        }
        super.updateActiveStatus(id, isActive);
    }

    private List<StoreStockRoomResponseDto> getDataFromParents(List<StoreStockRoom> storeStockRoomList) {
        Set<Long> officeIds = storeStockRoomList.stream()
                .map(StoreStockRoom::getOfficeId).collect(Collectors.toSet());
        Map<Long, OfficeProjection> storeStockRoomProjection = officeService
                .findByIdIn(officeIds)
                .stream()
                .collect(Collectors
                        .toMap(OfficeProjection::getId, Function.identity()));
        List<StoreStockRoomResponseDto> storeStockRoomResponseDtos = storeStockRoomList
                .stream().map(storeStockRoom ->
                        convertToStoreStockRoomResponseDto(storeStockRoom, storeStockRoomProjection
                                .get(storeStockRoom.getOfficeId())))
                .collect(Collectors.toList());
        return storeStockRoomResponseDtos;
    }

    private StoreStockRoomResponseDto convertToStoreStockRoomResponseDto(StoreStockRoom entity, OfficeProjection storeStockOfficeProjection) {
        StoreStockRoomResponseDto responseDto = new StoreStockRoomResponseDto();
        responseDto.setStoreStockRoomId(entity.getId());
        responseDto.setStoreStockRoomCode(entity.getCode());
        responseDto.setStoreStockRoomNo(entity.getStockRoomNo());
        responseDto.setStoreStockRoomDescription(entity.getDescription());
        if (Objects.nonNull(storeStockOfficeProjection)) {
            responseDto.setOfficeId(storeStockOfficeProjection.getId());
            responseDto.setOfficeCode(storeStockOfficeProjection.getCode());
        }
        return responseDto;
    }

    @Override
    protected <T> T convertToResponseDto(StoreStockRoom storeStockRoom) {
        return null;
    }

    @Override
    protected StoreStockRoom convertToEntity(StoreStockRoomDto storeStockRoomDto) {
        StoreStockRoom storeStockRoom = new StoreStockRoom();
        storeStockRoom.setStockRoomNo(storeStockRoomDto.getStoreStockRoomNo());
        storeStockRoom.setCode(storeStockRoomDto.getStoreStockRoomCode());
        storeStockRoom.setDescription(storeStockRoomDto.getStoreStockRoomDescription());
        storeStockRoom.setOffice(officeService.findById(storeStockRoomDto.getOfficeId()));
        return storeStockRoom;
    }

    private void validate(StoreStockRoomDto requestDto, StoreStockRoom oldEntity) {
        List<StoreStockRoom> stockRoomList = storeStockRoomRepository.findByCodeIgnoreCase(requestDto.getStoreStockRoomCode());
        if (CollectionUtils.isNotEmpty(stockRoomList)) {
            stockRoomList.forEach(storeStockRoom -> {
                if (Objects.nonNull(oldEntity) && storeStockRoom.equals(oldEntity)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.STORE_STOCK_ROOM_CODE_EXIST);
            });
        }
    }

    @Override
    protected StoreStockRoom updateEntity(StoreStockRoomDto dto, StoreStockRoom entity) {
        validate(dto, entity);
        entity.setCode(dto.getStoreStockRoomCode());
        entity.setDescription(dto.getStoreStockRoomDescription());
        entity.setStockRoomNo(dto.getStoreStockRoomNo());
        return entity;
    }

    @Override
    protected Specification<StoreStockRoom> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StoreStockRoom> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ENTITY_CODE))
        );
    }

    public List<StoreStockRoomProjection> findByIdIn(Set<Long> collect) {
        return storeStockRoomRepository.findStoreStockRoomByIdIn(collect);
    }
}
