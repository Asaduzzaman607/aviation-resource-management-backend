package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Rack;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Room;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackRowProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RackDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.RackRepository;
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
public class RackService extends AbstractSearchService<Rack, RackDto, IdQuerySearchDto> {
    private final RoomService roomService;
    private final RackRowService rackRowService;
    private final RackRepository rackRepository;
    private final StorePartAvailabilityService storePartAvailabilityService;

    public RackService(RackRepository rackRepository, RoomService roomService,
                       @Lazy RackRowService rackRowService,
                       @Lazy StorePartAvailabilityService storePartAvailabilityService) {
        super(rackRepository);
        this.roomService = roomService;
        this.rackRepository = rackRepository;
        this.rackRowService = rackRowService;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Rack> rackSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Rack>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Rack> rackPage = rackRepository.findAll(rackSpecification, pageable);
        List<Rack> rackPageContent = rackPage.getContent();
        return PageData.builder()
                .model(getDataFromParents(rackPageContent))
                .totalPages(rackPage.getTotalPages())
                .totalElements(rackPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public RackResponseDto getSingle(Long id) {
        List<RackResponseDto> responseDtos = getDataFromParents(Collections.singletonList(super.findByIdUnfiltered(id)));
        return CollectionUtils.isEmpty(responseDtos) ? null : responseDtos.get(ApplicationConstant.LIST_FIRST_INDEX);
    }

    /**
     * Check dependency
     */
    public boolean existByRack(Long roomId) {
        return rackRepository.existsByRoomIdAndIsActiveTrue(roomId);
    }

    /**
     * Custom update status with dependency
     *
     * @param id       for update status
     * @param isActive boolean
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && (rackRowService.existByRack(id)
                || storePartAvailabilityService.existsByRackIdAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
        }

        Rack rack = findByIdUnfiltered(id);
        if (rack.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == TRUE) {
            if (roomService.findOptionalById(rack.getRoomId(), true).isEmpty()) {
                throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.PARENT_DATA_EXISTS, ApplicationConstant.ROOM));
            }
            RackDto rackDto = new RackDto();
            rackDto.setRackCode(rack.getCode());
            rackDto.setRoomId(rack.getRoomId());
            validate(rackDto, null);
        }
        rack.setIsActive(isActive);
        saveItem(rack);
    }

    public Set<RackRowProjection> findByIdIn(Set<Long> rackIds) {
        return rackRepository.findByIdIn(rackIds);
    }

    public List<Rack> findByIdIn(List<Long> rackIds) {
        return rackRepository.findByIdIn(rackIds);
    }

    public List<RackResponseDto> getDataFromParents(List<Rack> rackList) {
        Set<Long> roomIds = rackList.stream()
                .map(Rack::getRoomId).collect(Collectors.toSet());
        Map<Long, RackProjection> rackProjection = roomService.findByIdIn(roomIds).stream()
                .collect(Collectors.toMap(RackProjection::getId, Function.identity()));
        List<RackResponseDto> rackResponseDtos = rackList.stream().map(rack ->
                        convertToRackResponseDto(rack, rackProjection.get(rack.getRoomId())))
                .collect(Collectors.toList());
        return rackResponseDtos;
    }

    private RackResponseDto convertToRackResponseDto(Rack entity, RackProjection rackProjection) {
        RackResponseDto responseDto = new RackResponseDto();
        responseDto.setRackId(entity.getId());
        responseDto.setRackCode(entity.getCode());
        responseDto.setRackHeight(entity.getHeight());
        responseDto.setRackWidth(entity.getWidth());
        if (Objects.nonNull(rackProjection)) {
            responseDto.setRoomId(rackProjection.getId());
            responseDto.setRoomName(rackProjection.getName());
            responseDto.setRoomCode(rackProjection.getCode());
            responseDto.setOfficeCode(rackProjection.getOfficeCode());
            responseDto.setOfficeId(rackProjection.getOfficeId());
        }
        return responseDto;
    }

    /**
     * This method is responsible for validating case-sensitive rack code
     *
     * @param dto {@link RackDto}
     * @param old {@link Rack}
     */
    private void validate(RackDto dto, Rack old) {
        List<Rack> rackList = rackRepository.
                findByRoomIdAndCodeIgnoreCaseAndIsActiveTrue(
                        dto.getRoomId(),
                        dto.getRackCode());

        if (CollectionUtils.isNotEmpty(rackList) && (
                Objects.isNull(old) ||
                        rackList.size() > VALUE_ONE ||
                        !rackList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.CODE_ALREADY_EXIST);
        }
    }

    @Override
    protected RackDto convertToResponseDto(Rack rack) {
        Room room = rack.getRoom();
        return RackDto.builder()
                .rackId(rack.getId())
                .rackCode(rack.getCode())
                .rackHeight(rack.getHeight())
                .rackWidth(rack.getWidth())
                .roomId(room.getId())
                .build();
    }

    @Override
    protected Rack convertToEntity(RackDto rackDto) {
        validate(rackDto, null);
        Room room = roomService.findById(rackDto.getRoomId());
        Rack rack = new Rack();
        rack.setCode(rackDto.getRackCode());
        rack.setHeight(rackDto.getRackHeight());
        rack.setWidth(rackDto.getRackWidth());
        rack.setRoom(room);
        return rack;
    }

    @Override
    protected Rack updateEntity(RackDto dto, Rack entity) {
        validate(dto, entity);
        entity.setCode(dto.getRackCode());
        entity.setHeight(dto.getRackHeight());
        entity.setWidth(dto.getRackWidth());
        entity.setRoom(roomService.findById(dto.getRoomId()));
        return entity;
    }

    @Override
    protected Specification<Rack> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Rack> customSpecification = new CustomSpecification<>();
        Specification<Rack> rackSpecification =
                Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getId(), ROOM_ID))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ENTITY_CODE));
        return rackSpecification;
    }
}