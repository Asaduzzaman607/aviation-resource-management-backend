package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.storemanagement.converter.IssueItemConverter;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueItemProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartAvailabilityProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnitMeasurementProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.GrnAndSerialDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueItemDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueItemResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreIssueItemRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.VALUE_ZERO;

@Service
public class StoreIssueItemDetailsServiceImpl implements StoreIssueItemDetailsService {

    private static final String STORE_ISSUE_ITEM = "StoreIssueItem";
    private final StoreIssueItemRepository repository;
    private final StoreIssueSerialService storeIssueSerialService;
    private final StoreDemandDetailsService storeDemandDetailsService;
    private final PartService partService;
    private final UnitMeasurementService unitMeasurementService;
    private final StorePartAvailabilityService storePartAvailabilityService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    public StoreIssueItemDetailsServiceImpl(StoreIssueItemRepository repository,
                                            StoreIssueSerialService storeIssueSerialService,
                                            StoreDemandDetailsService storeDemandDetailsService,
                                            PartService partService,
                                            UnitMeasurementService unitMeasurementService,
                                            StorePartAvailabilityService storePartAvailabilityService) {
        this.repository = repository;
        this.storeIssueSerialService = storeIssueSerialService;
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.partService = partService;
        this.unitMeasurementService = unitMeasurementService;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }
    @Override
    public StoreIssueItem create(StoreIssueItemDto dto,
                                 StoreDemandItem demandItem,
                                 StoreIssue issue,
                                 Set<GrnAndSerialDto> serialGrnDtos,
                                 Map<Long, StorePartSerial> storePartSerialMap,
                                 UnitMeasurement unitMeasurement) {
        StoreIssueItem issueItem = IssueItemConverter.convertToEntity(dto, demandItem, issue,unitMeasurement);
        this.saveItem(issueItem);
        serialGrnDtos.forEach(grnAndSerialDto -> {
            StorePartSerial storePartSerial = storePartSerialMap.get(grnAndSerialDto.getSerialId());
            storeIssueSerialService.convertAndSaveEntity(issueItem, demandItem.getPart(), storePartSerial, grnAndSerialDto);
        });
        this.saveItem(issueItem);
        return issueItem;
    }

    @Override
    public StoreIssueItem update(StoreIssueItemDto dto,
                                 StoreDemandItem demandItem, Set<GrnAndSerialDto> serialGrnDtos,
                                 Map<Long, StorePartSerial> storePartSerialMap,UnitMeasurement unitMeasurement) {
        StoreIssueItem issueItem = findById(dto.getId());
        StoreIssueItem updateItem = IssueItemConverter.updateEntity(issueItem, dto, unitMeasurement);
        this.saveItem(updateItem);
        serialGrnDtos.forEach(grnAndSerialDto -> {
            StorePartSerial storePartSerial = storePartSerialMap.get(grnAndSerialDto.getSerialId());
            storeIssueSerialService.convertAndSaveEntity(updateItem, demandItem.getPart(), storePartSerial, grnAndSerialDto);
        });
        return updateItem;
    }

    @Override
    public StoreIssueItem findById(Long id) {
        if (Objects.isNull(id)) {
            throw EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.ID_IS_REQUIRED_DYNAMIC,
                    STORE_ISSUE_ITEM));
        }
        return repository.findById(id).orElseThrow(() ->
                EngineeringManagementServerException.notFound(Helper.createDynamicCode(ErrorId.DATA_NOT_FOUND_DYNAMIC,
                        STORE_ISSUE_ITEM)));
    }

    public StoreIssueItem saveItem(StoreIssueItem entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    public List<StoreIssueItemProjection> findByStoreIssueId(Long issueId) {
        return repository.findByStoreIssueId(issueId);
    }

    public StoreIssueItemProjection findByStoreDemandItemId(Long demandId) {
        return repository.findByStoreDemandItemId(demandId);
    }

    public List<StoreIssueItem> getAllStoreIssueItemByStoreIssueId(Long issueId) {
        return repository.getAllStoreIssueItemByStoreIssueId(issueId);
    }

    public Set<StoreIssueItem> getAllStoreIssueItemByStoreIssueIdIn(Set<Long> ids)
    {
       return repository.getAllStoreIssueItemByStoreIssueIdIn(ids);
    }

    public List<StoreIssueItemResponseDto> getResponse(Set<StoreIssueItem> storeIssueItems, Set<Long> parentIds) {

        Set<Long> collectionsOfStoreDemandItemIds = storeIssueItems.stream().map(StoreIssueItem::getStoreDemandItemId).collect(Collectors.toSet());
        List<StoreDemandItem>  storeDemandItemList = storeDemandDetailsService.findByIdIn(collectionsOfStoreDemandItemIds);
        Map<Long, StoreDemandItem> storeDemandItemMap = storeDemandItemList.stream().collect(Collectors.toMap(StoreDemandItem::getId, Function.identity()));

        Map<Long, StoreIssueItem> storeIssueItemMap = storeIssueItems.stream().collect(Collectors.toMap(StoreIssueItem::getId, Function.identity()));

        Set<Long> collectionOfPartIds = storeDemandItemList.stream().map(StoreDemandItem::getPartId).collect(Collectors.toSet());

        Set<Long> uomIds = storeIssueItems.stream().map(StoreIssueItem::getUomId).collect(Collectors.toSet());
        Map<Long, UnitMeasurementProjection> unitMeasurementProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(uomIds).
                stream().collect(Collectors.toMap(UnitMeasurementProjection::getId, Function.identity()));

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionOfPartIds).stream()
                .collect(Collectors.toMap(PartProjection::getId, Function.identity()));

        Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap = storePartAvailabilityService.findPartQuantityByPartIdIn(collectionOfPartIds)
                .stream().collect(Collectors.toMap(StorePartAvailabilityProjection::getPartId, Function.identity()));

        return storeIssueItems.stream().map(itemDetails -> {
                    StoreDemandItem storeDemandItem = storeDemandItemMap.get(itemDetails.getStoreDemandItemId());
                    return convertToResponseDto(storeDemandItem,
                            storeIssueItemMap.get(itemDetails.getId()),
                            partProjectionMap.get(storeDemandItem.getPartId()),
                            storePartAvailabilityProjectionMap.getOrDefault(storeDemandItem.getPartId(), null),
                            unitMeasurementProjectionMap.get(itemDetails.getUomId()));
                })
                .collect(Collectors.toList());
    }

    private StoreIssueItemResponseDto convertToResponseDto(StoreDemandItem storeDemandItem,
                                                       StoreIssueItem storeIssueItem,
                                                       PartProjection partProjection,
                                                       StorePartAvailabilityProjection storePartAvailabilityProjection,
                                                           UnitMeasurementProjection unitMeasurementProjection) {

        StoreIssueItemResponseDto issuedemandDetailsDto = StoreIssueItemResponseDto.builder()
                .id(storeIssueItem.getId())
                .issueId(storeIssueItem.getStoreIssueId())
                .totalIssuedQty(storeDemandItem.getIssuedQty())
                .demandItemId(storeDemandItem.getId())
                .isActive(storeIssueItem.getIsActive())
                .remark(storeIssueItem.getRemark())
                .quantityDemanded(storeDemandItem.getQuantityDemanded())
                .storeDemandId(storeDemandItem.getStoreDemandId())
                .priorityType(storeDemandItem.getPriorityType())
                .parentPartId(storeDemandItem.getParentPartId())
                .issuedQuantity(storeIssueItem.getIssuedQuantity())
                .cardLineNo(storeIssueItem.getCardLineNo())
                .availablePart(Objects.nonNull(storePartAvailabilityProjection) ?
                        storePartAvailabilityProjection.getQuantity() : 0)
                .build();

        if (Objects.nonNull(partProjection)) {
            issuedemandDetailsDto.setPartNo(partProjection.getPartNo());
            issuedemandDetailsDto.setPartClassification(partProjection.getClassification());
            issuedemandDetailsDto.setPartId(partProjection.getId());
            issuedemandDetailsDto.setPartDescription(partProjection.getDescription());

        }
        if(Objects.nonNull(unitMeasurementProjection)){
            issuedemandDetailsDto.setUnitMeasurementId(unitMeasurementProjection.getId());
            issuedemandDetailsDto.setUnitMeasurementCode(unitMeasurementProjection.getCode());
        }
        return issuedemandDetailsDto;
    }
}
