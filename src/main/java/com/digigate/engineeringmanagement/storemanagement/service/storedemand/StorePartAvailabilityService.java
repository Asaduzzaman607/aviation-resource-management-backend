package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.storemanagement.constant.PartAvailabilityCountType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartAvailabilityProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockRoomProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.PartAvailUpdateInternalDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartAvailabilityRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardViewProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowBinResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RoomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailabilityResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailabilitySearchProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailabilitySearchResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StorePartAvailabilityRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class StorePartAvailabilityService extends AbstractSearchService<StorePartAvailability, StorePartAvailabilityRequestDto, IdQuerySearchDto> {

    private final RackRowBinService rackRowBinService;
    private final OfficeService officeService;
    private final RoomService roomService;
    private final RackService rackService;
    private final RackRowService rackRowService;
    private final PartService partService;
    private final StoreStockRoomService storeStockRoomService;
    private final StorePartAvailabilityLogService partAvailabilityLogService;
    private final StorePartSerialService storePartSerialService;
    private final StorePartAvailabilityRepository repository;

    @Autowired
    public StorePartAvailabilityService(StorePartAvailabilityRepository repository,
                                        @Lazy RackRowBinService rackRowBinService,
                                        @Lazy OfficeService officeService,
                                        @Lazy RoomService roomService,
                                        @Lazy RackService rackService,
                                        @Lazy RackRowService rackRowService,
                                        @Lazy PartService partService,
                                        StoreStockRoomService storeStockRoomService,
                                        @Lazy StorePartAvailabilityLogService partAvailabilityLogService,
                                        @Lazy StorePartSerialService storePartSerialService) {
        super(repository);
        this.rackRowBinService = rackRowBinService;
        this.officeService = officeService;
        this.roomService = roomService;
        this.rackService = rackService;
        this.rackRowService = rackRowService;
        this.partService = partService;
        this.repository = repository;
        this.storeStockRoomService = storeStockRoomService;
        this.partAvailabilityLogService = partAvailabilityLogService;
        this.storePartSerialService = storePartSerialService;
    }

    public List<StorePartAvailabilityProjection> findPartQuantityByPartIdIn(Set<Long> collectionsOfPartIds) {
        return repository.findStorePartAvailabilitiesByPartIdIn(collectionsOfPartIds);
    }

    public boolean existsByRoomIdAndIsActiveTrue(Long id) {
        return repository.existsByRoomIdAndIsActiveTrue(id);
    }

    public boolean existsByOfficeIdAndIsActiveTrue(Long id) {
        return repository.existsByOfficeIdAndIsActiveTrue(id);
    }

    public boolean existsByRackIdAndIsActiveTrue(Long id) {
        return repository.existsByRackIdAndIsActiveTrue(id);
    }

    public boolean existsByRackRowIdAndIsActiveTrue(Long id) {
        return repository.existsByRackRowIdAndIsActiveTrue(id);
    }

    public boolean existsByPartIdAndIsActiveTrue(Long id) {
        return repository.existsByPartIdAndIsActiveTrue(id);
    }

    public boolean existsByRackRowBinIdAndIsActiveTrue(Long id) {
        return repository.existsByRackRowBinIdAndIsActiveTrue(id);
    }

    @Override
    public StorePartAvailabilityResponseDto getSingle(Long id) {
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id))).get(ApplicationConstant.FIRST_INDEX);
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        List<StorePartAvailabilitySearchProjection> storePartAvailabilities = repository.findAlLGroupByUom(searchDto.getIsActive(), searchDto.getQuery());
        return Helper.buildCustomPagedData(convertToSearchResponse(storePartAvailabilities), pageable);
    }

    private List<StorePartAvailabilitySearchResponseDto> convertToSearchResponse(List<StorePartAvailabilitySearchProjection> storePartAvailabilities) {
        return storePartAvailabilities.stream().map(this::populateResponse).collect(Collectors.toList());
    }

    private StorePartAvailabilitySearchResponseDto populateResponse(StorePartAvailabilitySearchProjection storePartAvailabilitySearchProjection) {
        return StorePartAvailabilitySearchResponseDto.builder()
                .partId(storePartAvailabilitySearchProjection.getPartId())
                .partNo(storePartAvailabilitySearchProjection.getPartNo())
                .id(storePartAvailabilitySearchProjection.getId())
                .quantity(storePartAvailabilitySearchProjection.getQuantity())
                .demandQuantity(storePartAvailabilitySearchProjection.getDemandQuantity())
                .issuedQuantity(storePartAvailabilitySearchProjection.getIssuedQuantity())
                .requisitionQuantity(storePartAvailabilitySearchProjection.getRequisitionQuantity())
                .officeId(storePartAvailabilitySearchProjection.getOfficeId())
                .officeCode(storePartAvailabilitySearchProjection.getOfficeCode())
                .uomId(storePartAvailabilitySearchProjection.getUomId())
                .uomWiseQuantity(storePartAvailabilitySearchProjection.getUomWiseQuantity())
                .uomCode(storePartAvailabilitySearchProjection.getUomCode())
                .maxStock(storePartAvailabilitySearchProjection.getMaxStock())
                .minStock(storePartAvailabilitySearchProjection.getMinStock())
                .build();
    }

    /**
     * Custom get all method
     *
     * @param storePartAvailabilityList boolean field
     * @return data
     */
    public List<StorePartAvailabilityResponseDto> getResponseData(List<StorePartAvailability> storePartAvailabilityList) {

        List<Long> collectionOfPartIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getPartId).collect(Collectors.toList());

        Set<Long> collectionsOfStoreStockRoomIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getStockRoomId).collect(Collectors.toSet());

        List<Long> collectionOfOfficeIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getOfficeId).collect(Collectors.toList());

        List<Long> collectionOfRoomIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getRoomId).collect(Collectors.toList());

        List<Long> collectionOfRackIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getRackId).collect(Collectors.toList());

        List<Long> collectionOfRackRowIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getRackRowId).collect(Collectors.toList());

        List<Long> collectionOfRackRowBinIds = storePartAvailabilityList.stream()
                .map(StorePartAvailability::getRackRowBinId).collect(Collectors.toList());

        Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap = repository
                .findStorePartAvailabilitiesByPartIdIn(new HashSet<>(collectionOfPartIds)).stream()
                .collect(Collectors
                        .toMap(StorePartAvailabilityProjection::getId, Function.identity()));

        Map<Long, PartProjection> partProjectionMap =
                partService.findByIdIn(collectionOfPartIds)
                        .stream()
                        .collect(Collectors
                                .toMap(PartProjection::getId, Function.identity()));

        Map<Long, StoreStockRoomProjection> storeStockRoomProjectionMap = storeStockRoomService
                .findByIdIn(collectionsOfStoreStockRoomIds)
                .stream().collect(Collectors.toMap(StoreStockRoomProjection::getId, Function.identity()));

        Map<Long, OfficeProjection> officeProjectionMap =
                officeService.findByIdIn(collectionOfOfficeIds)
                        .stream()
                        .collect(Collectors
                                .toMap(OfficeProjection::getId, Function.identity()));

        Map<Long, RoomResponseDto> roomResponseDtoMap =
                roomService.getDataFromParents(roomService.findByIdIn(collectionOfRoomIds))
                        .stream()
                        .collect(Collectors
                                .toMap(RoomResponseDto::getRoomId, Function.identity()));

        Map<Long, RackResponseDto> rackResponseDtoMap =
                rackService.getDataFromParents(rackService.findByIdIn(collectionOfRackIds))
                        .stream()
                        .collect(Collectors
                                .toMap(RackResponseDto::getRackId, Function.identity()));

        Map<Long, RackRowResponseDto> rackRowResponseDtoMap =
                rackRowService.getDataFromParents(rackRowService.findByIdIn(collectionOfRackRowIds))
                        .stream()
                        .collect(Collectors
                                .toMap(RackRowResponseDto::getRackRowId, Function.identity()));

        Map<Long, RackRowBinResponseDto> rackRowBinResponseDtoMap =
                rackRowBinService.getDataFromParents(rackRowBinService.findByIdIn(collectionOfRackRowBinIds))
                        .stream()
                        .collect(Collectors
                                .toMap(RackRowBinResponseDto::getRackRowBinId, Function.identity()));

        return storePartAvailabilityList
                .stream()
                .map(storePartAvailability ->
                        convertToStorePartAvailabilityResponseDto(storePartAvailability,
                                partProjectionMap.get(storePartAvailability.getPartId()),
                                storeStockRoomProjectionMap.get(storePartAvailability.getStockRoomId()),
                                officeProjectionMap.get(storePartAvailability.getOfficeId()),
                                roomResponseDtoMap.get(storePartAvailability.getRoomId()),
                                rackResponseDtoMap.get(storePartAvailability.getRackId()),
                                rackRowResponseDtoMap.get(storePartAvailability.getRackRowId()),
                                storePartAvailabilityProjectionMap.get(storePartAvailability.getId()),
                                rackRowBinResponseDtoMap.get(storePartAvailability.getRackRowBinId())))
                .collect(Collectors.toList());
    }

    public void updateStorePartAvailabilityQuantityById(Long id, Integer value) {
        repository.updateStorePartAvailabilityQuantityById(id, value);
    }

    @Override
    protected Specification<StorePartAvailability> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StorePartAvailability> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD)
        ).and(customSpecification.likeSpecificationAtChild(searchDto.getQuery(), "part", "partNo"));
    }

    @Override
    protected StorePartAvailabilityResponseDto convertToResponseDto(StorePartAvailability storePartAvailability) {
        StorePartAvailabilityResponseDto storePartAvailabilityResponseDto = new StorePartAvailabilityResponseDto();

        storePartAvailabilityResponseDto.setId(storePartAvailability.getId());
        if (nonNull(storePartAvailability.getPart())) {
            storePartAvailabilityResponseDto.setPartId(storePartAvailability.getPart().getId());
            storePartAvailabilityResponseDto.setPartNo(storePartAvailability.getPart().getPartNo());
            storePartAvailabilityResponseDto.setPartType(storePartAvailability.getPart().getClassification());
        }
        if (nonNull(storePartAvailability.getOffice())) {
            storePartAvailabilityResponseDto.setOfficeId(storePartAvailability.getOfficeId());
            storePartAvailabilityResponseDto.setOfficeCode(storePartAvailability.getOffice().getCode());
        }
        if (nonNull(storePartAvailability.getRoom())) {
            storePartAvailabilityResponseDto.setRoomId(storePartAvailability.getRoomId());
            storePartAvailabilityResponseDto.setRoomCode(storePartAvailability.getRoom().getCode());
        }
        if (nonNull(storePartAvailability.getRack())) {
            storePartAvailabilityResponseDto.setRackId(storePartAvailability.getRackId());
            storePartAvailabilityResponseDto.setRackCode(storePartAvailability.getRack().getCode());
        }
        if (nonNull(storePartAvailability.getRackRow())) {
            storePartAvailabilityResponseDto.setRackRowId(storePartAvailability.getRackRowId());
            storePartAvailabilityResponseDto.setRackRowCode(storePartAvailability.getRackRow().getCode());
        }
        if (nonNull(storePartAvailability.getRackRowBin())) {
            storePartAvailabilityResponseDto.setRackRowBinId(storePartAvailability.getRackRowBinId());
            storePartAvailabilityResponseDto.setRackRowBinCode(storePartAvailability.getRackRowBin().getCode());
        }
        if (nonNull(storePartAvailability.getStockRoom())) {
            storePartAvailabilityResponseDto.setStockRoomId(storePartAvailability.getStockRoomId());
            storePartAvailabilityResponseDto.setStockRoomCode(storePartAvailability.getStockRoom().getCode());
        }
        storePartAvailabilityResponseDto.setOtherLocation(storePartAvailability.getOtherLocation());
        storePartAvailabilityResponseDto.setLocationTag(storePartAvailability.getLocationTag());
        storePartAvailabilityResponseDto.setQuantity(storePartAvailability.getQuantity());
        storePartAvailabilityResponseDto.setDemandQuantity(storePartAvailability.getDemandQuantity());
        storePartAvailabilityResponseDto.setIssuedQuantity(storePartAvailability.getIssuedQuantity());
        storePartAvailabilityResponseDto.setRequisitionQuantity(storePartAvailability.getRequisitionQuantity());
        storePartAvailabilityResponseDto.setMaxStock(storePartAvailability.getMaxStock());
        storePartAvailabilityResponseDto.setMinStock(storePartAvailability.getMinStock());
        return storePartAvailabilityResponseDto;
    }

    @Override
    protected StorePartAvailability convertToEntity(StorePartAvailabilityRequestDto storePartAvailabilityRequestDto) {
        validatePartIfAvailable(storePartAvailabilityRequestDto.getPartId());
        return populateEntity(storePartAvailabilityRequestDto, new StorePartAvailability());
    }


    @Override
    protected StorePartAvailability updateEntity(StorePartAvailabilityRequestDto dto, StorePartAvailability entity) {
        return populateEntity(dto, entity);
    }

    private StorePartAvailabilityResponseDto convertToStorePartAvailabilityResponseDto(StorePartAvailability storePartAvailability,
                                                                                       PartProjection partProjection,
                                                                                       StoreStockRoomProjection storeStockRoomProjection,
                                                                                       OfficeProjection officeProjection,
                                                                                       RoomResponseDto roomResponseDto,
                                                                                       RackResponseDto rackResponseDto,
                                                                                       RackRowResponseDto rackRowResponseDto,
                                                                                       StorePartAvailabilityProjection storePartAvailabilityProjection,
                                                                                       RackRowBinResponseDto rackRowBinResponseDto) {
        StorePartAvailabilityResponseDto responseDto = new StorePartAvailabilityResponseDto();
        storePartAvailabilityResponseDto(responseDto, partProjection,storeStockRoomProjection, storePartAvailability , storePartAvailabilityProjection);
        if (Objects.isNull(storePartAvailability.getLocationTag())) {
            return responseDto;
        }
        storePartAvailabilityResponseDto(responseDto, partProjection, storeStockRoomProjection, storePartAvailability, storePartAvailabilityProjection);
        if (Objects.isNull(storePartAvailability.getLocationTag())) {
            return responseDto;
        }
        switch (storePartAvailability.getLocationTag()) {
            case ROOM:
                return StorePartConverter.convertToStorePartAvailabilityResponseDto(responseDto, officeProjection, roomResponseDto, storePartAvailabilityProjection);
            case RACK:
                return StorePartConverter.convertToStorePartAvailabilityResponseDto(responseDto, roomResponseDto, rackResponseDto, storePartAvailabilityProjection);
            case RACKROW:
                return StorePartConverter.convertToStorePartAvailabilityResponseDto(responseDto, rackResponseDto, rackRowResponseDto, storePartAvailabilityProjection);
            case RACKROWBIN:
                return StorePartConverter.convertToStorePartAvailabilityResponseDto(responseDto, rackRowResponseDto, rackRowBinResponseDto, storePartAvailabilityProjection);
            default:
                return responseDto;
        }
    }

    private boolean isInvalidLocation(Long exactLoc, Long parentLoc, String otherLoc) {
        return (isNull(exactLoc) && isNull(parentLoc)) || (isNull(exactLoc) && StringUtils.isEmpty(otherLoc));
    }

    private StorePartAvailabilityResponseDto storePartAvailabilityResponseDto(StorePartAvailabilityResponseDto responseDto,
                                                                              PartProjection partProjection,
                                                                              StoreStockRoomProjection storeStockRoomProjection,
                                                                              StorePartAvailability storePartAvailability,
                                                                              StorePartAvailabilityProjection storePartAvailabilityProjection) {
        if (nonNull(partProjection)) {
            responseDto.setPartId(partProjection.getId());
            responseDto.setPartNo(partProjection.getPartNo());
        }
        if (nonNull(storeStockRoomProjection)) {
            responseDto.setStockRoomId(storeStockRoomProjection.getId());
            responseDto.setStockRoomCode(storeStockRoomProjection.getCode());
        }
        responseDto.setId(storePartAvailability.getId());
        responseDto.setOtherLocation(storePartAvailability.getOtherLocation());
        responseDto.setMaxStock(storePartAvailability.getMaxStock());
        responseDto.setMinStock(storePartAvailability.getMinStock());
        responseDto.setLocationTag(storePartAvailability.getLocationTag());
        responseDto.setQuantity(storePartAvailability.getQuantity());
        responseDto.setDemandQuantity(storePartAvailability.getDemandQuantity());
        responseDto.setIssuedQuantity(storePartAvailability.getIssuedQuantity());
        responseDto.setRequisitionQuantity(storePartAvailability.getRequisitionQuantity());
        responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
        responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        responseDto.setPartClassification(storePartAvailabilityProjection.getPartClassification());
        return responseDto;
    }

    private void validateAndGetLocationInfo(StorePartAvailabilityRequestDto dto, StorePartAvailability storePartAvailability) {
        if(Objects.isNull(dto.getLocationTag()))
        {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.LOCATION_TAG_IS_REQUIRED);
        }
        switch (dto.getLocationTag()) {
            case ROOM:
                storePartAvailability.setOffice(Objects.nonNull(officeService.findById(dto.getOfficeId())) ?
                        officeService.findById(dto.getOfficeId()) : null);
                storePartAvailability.setRoom(nonNull(dto.getRoomId()) ? roomService.findById(dto.getRoomId()) : null);
                break;
            case RACK:
                if (isInvalidLocation(dto.getRackId(), dto.getRoomId(), dto.getOtherLocation())) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_REQUEST);
                }
                storePartAvailability.setRoom(nonNull(dto.getRoomId()) ? roomService.findById(dto.getRoomId()) : null);
                storePartAvailability.setRack(nonNull(dto.getRackId()) ? rackService.findById(dto.getRackId()) : null);
                break;
            case RACKROW:
                if (isInvalidLocation(dto.getRackRowId(), dto.getRackId(), dto.getOtherLocation())) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_REQUEST);
                }
                storePartAvailability.setRoom(nonNull(dto.getRoomId()) ? roomService.findById(dto.getRoomId()) : null);
                storePartAvailability.setRack(nonNull(dto.getRackId()) ? rackService.findById(dto.getRackId()) : null);
                storePartAvailability.setRackRow(nonNull(dto.getRackRowId()) ? rackRowService.findById(dto.getRackRowId()) : null);
                break;
            case RACKROWBIN:
                if (isInvalidLocation(dto.getRackRowBinId(), dto.getRackRowId(), dto.getOtherLocation())) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_REQUEST);
                }
                storePartAvailability.setRoom(nonNull(dto.getRoomId()) ? roomService.findById(dto.getRoomId()) : null);
                storePartAvailability.setRack(nonNull(dto.getRackId()) ? rackService.findById(dto.getRackId()) : null);
                storePartAvailability.setRackRow(nonNull(dto.getRackRowId()) ? rackRowService.findById(dto.getRackRowId()) : null);
                storePartAvailability.setRackRowBin(nonNull(dto.getRackRowBinId()) ? rackRowBinService.findById(dto.getRackRowBinId()) : null);
                break;
            default:
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.REQUEST_TYPE_IS_NOT_VALID);
        }
    }

    public List<StorePartAvailability> findByPartIdIn(Set<Long> partIdSet) {
        return repository.findByPartIdInAndIsActiveTrue(partIdSet);
    }

    private StorePartAvailability populateEntity(StorePartAvailabilityRequestDto storePartAvailabilityRequestDto,
                                StorePartAvailability storePartAvailability) {
        validateAndGetLocationInfo(storePartAvailabilityRequestDto, storePartAvailability);
        if(Objects.nonNull(storePartAvailabilityRequestDto.getOfficeId())){
            storePartAvailability.setOffice(officeService.findById(storePartAvailabilityRequestDto.getOfficeId()));
        }
        if(Objects.nonNull(storePartAvailabilityRequestDto.getStoreStockRoomId())){
            storePartAvailability.setStockRoom(storeStockRoomService.findById(storePartAvailabilityRequestDto.getStoreStockRoomId()));
        }
        storePartAvailability.setLocationTag(storePartAvailabilityRequestDto.getLocationTag());
        storePartAvailability.setOtherLocation(storePartAvailabilityRequestDto.getOtherLocation());
        storePartAvailability.setMinStock(storePartAvailabilityRequestDto.getMinStock());
        storePartAvailability.setMaxStock(storePartAvailabilityRequestDto.getMaxStock());
        storePartAvailability.setPart(partService.findById(storePartAvailabilityRequestDto.getPartId()));

        return storePartAvailability;
    }


    //TODO - validation check - part will be unique for room
    private void validatePartIfAvailable(Long partId) {
        Optional<StorePartAvailability> storePartAvailability = repository.findByPartId(partId);
        if (storePartAvailability.isPresent()) {
            throw EngineeringManagementServerException.notFound(ErrorId.PART_AVAILABILITY_IS_ALREADY_EXISTS);
        }
    }

    public Optional<StorePartAvailability> findByPartIdUnfiltered(Long id) {
        return repository.findByPartId(id);
    }

    public StorePartAvailability findByPartId(Long id) {
        return repository.findByPartId(id).orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.STORE_PART_AVAILABILITY_IS_NOT_FOUND));
    }

    public void updatePartQuantity(StorePartAvailability storePartAvailability, TransactionType transactionType, Integer quantity) {
        int newQuantity = storePartAvailability.getQuantity() + (transactionType == TransactionType.RECEIVE ? quantity : -quantity);
        if (newQuantity < 0) {
            LOGGER.error("Quantity is getting negative value {}", newQuantity);
            LOGGER.error("Saving 0 in quantity for part id:{}", storePartAvailability.getPartId());
            newQuantity = 0;
        }
        storePartAvailability.setQuantity(newQuantity);
        saveItem(storePartAvailability);
    }

    public synchronized void updateDemandIssuedRequisitionQuantity(Integer newQuantity, Long partId, PartAvailabilityCountType partAvailabilityCountType) {
        Optional<StorePartAvailability> optionalStorePartAvailability = repository.findByPartId(partId);
        if (optionalStorePartAvailability.isEmpty()) {
            return;
        }
        StorePartAvailability storePartAvailability = optionalStorePartAvailability.get();
        switch (partAvailabilityCountType) {
            case DEMAND:
                if (Objects.nonNull(storePartAvailability.getDemandQuantity())) {
                    storePartAvailability.setDemandQuantity(storePartAvailability.getDemandQuantity() + newQuantity);
                } else {
                    storePartAvailability.setDemandQuantity(newQuantity);
                }
                break;
            case ISSUE:
                if (Objects.nonNull(storePartAvailability.getIssuedQuantity())) {
                    storePartAvailability.setIssuedQuantity(storePartAvailability.getIssuedQuantity() + newQuantity);
                } else {
                    storePartAvailability.setIssuedQuantity(newQuantity);
                }
                break;
            default:
                if (Objects.nonNull(storePartAvailability.getRequisitionQuantity())) {
                    storePartAvailability.setRequisitionQuantity(storePartAvailability.getRequisitionQuantity() + newQuantity);
                } else {
                    storePartAvailability.setRequisitionQuantity(newQuantity);
                }
        }
        saveItem(storePartAvailability);
    }

    public void updateAvailabilityFromInspection(PartAvailUpdateInternalDto dto, String voucherNo,
                                                 Long initialUser, Long finalUser, Long ownId) {
        StorePartSerial partSerial = dto.getPartSerial();

        StorePartAvailabilityLog storePartAvailabilityLog = StorePartAvailabilityLog.builder()
                .storePartSerial(partSerial)
                .partStatus(PartStatus.SERVICEABLE)
                .grnNo(dto.getGrnNo())
                .quantity(dto.getQuantity())
                .parentType(dto.getParentType())
                .receiveDate(LocalDate.now())
                .unitPrice(dto.getUnitPrice())
                .currencyId(dto.getCurrencyId())
                .transactionType(TransactionType.RECEIVE)
                .voucherNo(voucherNo)
                .parentId(ownId)
                .submittedBy(User.withId(initialUser))
                .workFlowAction(WorkFlowAction.withId(finalUser))
                .receivedQty(partSerial.getQuantity())
                .inStock(partSerial.getStorePartAvailability().getQuantity())
                .build();
        updateAvailabilityAndSerialFromReturnAndInspection(storePartAvailabilityLog, dto);
    }

    public void updateAvailabilityForNotUsedReturn(PartAvailUpdateInternalDto dto) {
        StorePartSerial partSerial = dto.getPartSerial();

        StorePartAvailabilityLog storePartAvailabilityLog = StorePartAvailabilityLog.builder()
                .storePartSerial(partSerial)
                .partStatus(PartStatus.SERVICEABLE)
                .grnNo(dto.getGrnNo())
                .quantity(ApplicationConstant.INT_ONE)
                .parentType(dto.getParentType())
                .parentId(dto.getParentId())
                .receiveDate(LocalDate.now())
                .unitPrice(partSerial.getPrice())
                .currencyId(partSerial.getCurrencyId())
                .transactionType(TransactionType.RECEIVE)
                .quantity(dto.getQuantity())
                .voucherNo(dto.getVoucherNo())
                .submittedBy(User.withId(dto.getSubmittedBy()))
                .workFlowAction(WorkFlowAction.withId(dto.getFinalUser()))
                .receivedQty(partSerial.getQuantity())
                .inStock(partSerial.getStorePartAvailability().getQuantity())
                .build();

        updateAvailabilityAndSerialFromReturnAndInspection(storePartAvailabilityLog, dto);
    }

    public void updateAvailabilityForUsed(PartAvailUpdateInternalDto dto) {
        StorePartSerial partSerial = dto.getPartSerial();

        StorePartAvailabilityLog storePartAvailabilityLog = StorePartAvailabilityLog.builder()
                .storePartSerial(partSerial)
                .partStatus(PartStatus.SERVICEABLE)
                .grnNo(dto.getGrnNo())
                .quantity(ApplicationConstant.INT_ONE)
                .parentType(dto.getParentType())
                .parentId(dto.getParentId())
                .receiveDate(LocalDate.now())
                .unitPrice(partSerial.getPrice())
                .currencyId(partSerial.getCurrencyId())
                .transactionType(TransactionType.RECEIVE)
                .quantity(dto.getQuantity())
                .voucherNo(dto.getVoucherNo())
                .submittedBy(User.withId(dto.getSubmittedBy()))
                .workFlowAction(WorkFlowAction.withId(dto.getFinalUser()))
                .receivedQty(partSerial.getQuantity())
                .inStock(partSerial.getStorePartAvailability().getQuantity())
                .build();
        partAvailabilityLogService.saveItem(storePartAvailabilityLog);
    }

    public void updateAvailabilityForUnserviceableReturn(PartAvailUpdateInternalDto dto) {
        StorePartSerial partSerial = dto.getPartSerial();
        partSerial.setPartStatus(PartStatus.UNSERVICEABLE);
        storePartSerialService.saveItem(partSerial);
        StorePartAvailabilityLog storePartAvailabilityLog = StorePartAvailabilityLog.builder()
                .storePartSerial(partSerial)
                .partStatus(PartStatus.UNSERVICEABLE)
                .quantity(dto.getQuantity())
                .grnNo(dto.getGrnNo())
                .quantity(ApplicationConstant.INT_ONE)
                .parentType(StorePartAvailabilityLogParentType.RETURN)
                .parentId(dto.getParentId())
                .receiveDate(LocalDate.now())
                .unitPrice(partSerial.getPrice())
                .currencyId(dto.getCurrencyId())
                .transactionType(TransactionType.RECEIVE)
                .voucherNo(dto.getVoucherNo())
                .submittedBy(User.withId(dto.getSubmittedBy()))
                .workFlowAction(WorkFlowAction.withId(dto.getFinalUser()))
                .receivedQty(dto.getQuantity())
                .build();
        partAvailabilityLogService.saveItem(storePartAvailabilityLog);
    }

    public void updateAvailabilityAndSerialFromReturnAndInspection(StorePartAvailabilityLog storePartAvailabilityLog, PartAvailUpdateInternalDto dto){
        partAvailabilityLogService.saveItem(storePartAvailabilityLog);
        StorePartSerial partSerial = dto.getPartSerial();
        StorePartAvailability storePartAvailability = partSerial.getStorePartAvailability();
        updatePartQuantity(storePartAvailability, TransactionType.RECEIVE, storePartAvailabilityLog.getQuantity());

        partSerial.setParentType(storePartAvailabilityLog.getParentType());
        if (storePartAvailability.getPart().getClassification() == PartClassification.CONSUMABLE) {
            partSerial.setQuantity(partSerial.getQuantity() + storePartAvailabilityLog.getQuantity());
        }
        partSerial.setPartStatus(storePartAvailabilityLog.getPartStatus());
        storePartSerialService.saveItem(partSerial);
    }

    public StorePartAvailability findOrCreateAvailability(Part part) {
        return repository.findByPartIdAndIsActiveTrue(part.getId()).orElseGet(() -> saveItem(StorePartAvailability.from(part)));
    }

    public List<DashboardViewProjection> findPartInfoAndIsActiveTrue(LocalDate startDate, LocalDate endDate) {
        return repository.findPartInfoAndIsActiveTrue(startDate, endDate);
    }

    public void insertAvailabilityFromInspection(Part part) {
        if(!repository.existsByPartIdAndIsActiveTrue(part.getId())){
            StorePartAvailability storePartAvailability = StorePartAvailability.from(part);
            repository.save(storePartAvailability);
        }
    }
}
