package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Office;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Room;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.RoomDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RoomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.RoomRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnService;
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
public class RoomService extends AbstractSearchService<Room, RoomDto, IdQuerySearchDto> {
    private final OfficeService officeService;
    private final RackService rackService;
    private final RoomRepository roomRepository;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final StoreReturnService storeReturnService;

    public RoomService(OfficeService officeService,
                       @Lazy RackService rackService,
                       @Lazy RoomRepository roomRepository,
                       @Lazy StorePartAvailabilityService storePartAvailabilityService,
                       @Lazy StoreReturnService storeReturnService) {
        super(roomRepository);
        this.officeService = officeService;
        this.rackService = rackService;
        this.roomRepository = roomRepository;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.storeReturnService = storeReturnService;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Room> roomSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Room>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Room> roomPage = roomRepository.findAll(roomSpecification, pageable);
        List<Room> roomPageContent = roomPage.getContent();
        return PageData.builder()
                .model(getDataFromParents(roomPageContent))
                .totalPages(roomPage.getTotalPages())
                .totalElements(roomPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public RoomResponseDto getSingle(Long id) {
        List<RoomResponseDto> responseDtos = getDataFromParents(Collections.singletonList(super.findByIdUnfiltered(id)));
        return CollectionUtils.isEmpty(responseDtos) ? null : responseDtos.get(ApplicationConstant.LIST_FIRST_INDEX);
    }

    public Set<RackProjection> findByIdIn(Set<Long> roomIds) {
        return roomRepository.findByIdIn(roomIds);
    }

    public List<Room> findByIdIn(List<Long> roomIds) {
        return roomRepository.findByIdIn(roomIds);
    }
    /**
     * Custom update status with dependency
     *
     * @param id       for update status
     * @param isActive boolean
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && (rackService.existByRack(id)
                || storePartAvailabilityService.existsByRoomIdAndIsActiveTrue(id)
                || storeReturnService.existsByRoomIdAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
        }
        Room room = findByIdUnfiltered(id);
        if (room.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == TRUE) {
            if (officeService.findOptionalById(room.getOfficeId(), true).isEmpty()) {
                throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.PARENT_DATA_EXISTS, ApplicationConstant.OFFICE));
            }
            RoomDto roomDto = new RoomDto();
            roomDto.setCode(room.getCode());
            roomDto.setOfficeId(room.getOfficeId());
            validate(roomDto, null);
        }
        room.setIsActive(isActive);
        saveItem(room);

    }

    public List<RoomResponseDto> getDataFromParents(List<Room> roomList) {
        Set<Long> officeIds = roomList.stream()
                .map(Room::getOfficeId).collect(Collectors.toSet());
        Map<Long, OfficeProjection> roomProjection = officeService.findByIdIn(officeIds).stream()
                .collect(Collectors.toMap(OfficeProjection::getId, Function.identity()));
        List<RoomResponseDto> roomResponseDtos = roomList.stream().map(room ->
                        convertToRoomResponseDto(room, roomProjection.get(room.getOfficeId())))
                .collect(Collectors.toList());
        return roomResponseDtos;
    }

    private RoomResponseDto convertToRoomResponseDto(Room entity, OfficeProjection officeProjection) {
        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setRoomId(entity.getId());
        responseDto.setRoomCode(entity.getCode());
        responseDto.setRoomName(entity.getName());
        if (Objects.nonNull(officeProjection)) {
            responseDto.setOfficeCode(officeProjection.getCode());
            responseDto.setOfficeId(officeProjection.getId());
        }
        return responseDto;
    }

    /**
     * This method is responsible for validating case-sensitive room code
     *
     * @param dto {@link RoomDto}
     * @param old {@link Room}
     */
    private void validate(RoomDto dto, Room old) {
        List<Room> roomList = roomRepository.
                findByOfficeIdAndCodeIgnoreCaseAndIsActiveTrue(
                        dto.getOfficeId(),
                        dto.getCode());

        if (CollectionUtils.isNotEmpty(roomList) && (
                Objects.isNull(old) ||
                        roomList.size() > VALUE_ONE ||
                        !roomList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.CODE_ALREADY_EXIST);
        }
    }

    @Override
    protected RoomDto convertToResponseDto(Room room) {
        Office office = room.getOffice();
        return RoomDto.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .officeId(office.getId())
                .build();
    }

    @Override
    protected Room convertToEntity(RoomDto roomDto) {
        validate(roomDto, null);
        Office office = officeService.findById(roomDto.getOfficeId());
        Room room = new Room();
        room.setCode(roomDto.getCode());
        room.setName(roomDto.getName());
        room.setOffice(office);
        return room;
    }

    @Override
    protected Room updateEntity(RoomDto dto, Room entity) {
        validate(dto, entity);
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setOffice(officeService.findById(dto.getOfficeId()));
        return entity;
    }

    /**
     * Check dependency
     */
    public boolean existByStore(Long storeId) {
        return roomRepository.existsByOfficeIdAndIsActiveTrue(storeId);
    }

    @Override
    protected Specification<Room> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Room> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(
                                searchDto.getId(), ApplicationConstant.OFFICE_ID)
                        .and(customSpecification.likeSpecificationAtRoot(
                                searchDto.getQuery(), ApplicationConstant.ENTITY_CODE)));
    }


}
