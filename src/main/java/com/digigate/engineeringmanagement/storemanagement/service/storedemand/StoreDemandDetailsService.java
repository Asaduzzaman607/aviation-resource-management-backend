package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemand;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.AlterPartDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ItemPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreDemandDetailsRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class StoreDemandDetailsService extends AbstractSearchService<StoreDemandItem, StoreDemandDetailsDto, IdQuerySearchDto> {
    private final StoreDemandDetailsRepository storeDemandDetailsRepository;
    private final PartService partService;
    private final PartWiseUomService partWiseUomService;
    private final UnitMeasurementService unitMeasurementService;
    private final PartRemarkService partRemarkService;
    private final StorePartAvailabilityService storePartAvailabilityService;

    public StoreDemandDetailsService(StoreDemandDetailsRepository storeDemandDetailsRepository,
                                     PartService partService, PartWiseUomService partWiseUomService,
                                     UnitMeasurementService unitMeasurementService,
                                     PartRemarkService partRemarkService,
                                     StorePartAvailabilityService storePartAvailabilityService) {
        super(storeDemandDetailsRepository);
        this.storeDemandDetailsRepository = storeDemandDetailsRepository;
        this.partService = partService;
        this.partWiseUomService = partWiseUomService;
        this.unitMeasurementService = unitMeasurementService;
        this.partRemarkService = partRemarkService;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }

    public List<StoreDemandItem> findByIdIn(Set<Long> ids) {
        return storeDemandDetailsRepository.findByIdIn(ids);
    }

    public List<StoreDemandItem> findByStoreDemandId(Long id) {
        return storeDemandDetailsRepository.findByStoreDemandId(id);
    }

    public Long findTotalAliveDemandCount() {
        return storeDemandDetailsRepository.findTotalAliveCountForDemand();
    }

    @Override
    protected StoreDemandDetailsDto convertToResponseDto(StoreDemandItem storeDemandItem) {

        Part part = storeDemandItem.getPart();
        StoreDemandDetailsDto demandDetailsDto = StoreDemandDetailsDto.builder()
                .id(storeDemandItem.getId())
                .isActive(storeDemandItem.getIsActive())
                .priorityType(storeDemandItem.getPriorityType())
                .ipcCmm(storeDemandItem.getIpcCmm())
                .quantityDemanded(storeDemandItem.getQuantityDemanded())
                .storeDemandId(storeDemandItem.getStoreDemandId())
                .build();

        if (Objects.nonNull(part)) {
            demandDetailsDto.setPartId(part.getId());
            demandDetailsDto.setPartNo(part.getPartNo());
        }
        return demandDetailsDto;
    }

    /**
     * Convert to entity method for custom save
     *
     * @param storeDemandDetailsDto {@link StoreDemandDetailsDto}
     * @param storeDemand           {@link StoreDemand}
     * @return Successfully created message
     */

    private StoreDemandItem convertToEntity(StoreDemandDetailsDto storeDemandDetailsDto, StoreDemandItem storeDemandItem,
                                            StoreDemand storeDemand, Long parentId) {
        storeDemandItem.setStoreDemand(storeDemand);
        storeDemandItem.setQuantityDemanded(storeDemandDetailsDto.getQuantityDemanded());
        storeDemandItem.setPriorityType(storeDemandDetailsDto.getPriorityType());
        storeDemandItem.setRemarks(storeDemandDetailsDto.getRemark());
        storeDemandItem.setIpcCmm(storeDemandDetailsDto.getIpcCmm());
        storeDemandItem.setParentPartId(parentId);

        if (Objects.nonNull(storeDemandDetailsDto.getUnitMeasurementId())) {
            storeDemandItem.setUnitMeasurement(unitMeasurementService.findById(storeDemandDetailsDto.getUnitMeasurementId()));
        }
        if (Objects.nonNull(storeDemandDetailsDto.getPartId())) {
            Part part = partService.findById(storeDemandDetailsDto.getPartId());
            storeDemandItem.setPart(part);
            partWiseUomService.updateAll(List.of(storeDemandDetailsDto.getUnitMeasurementId()), part, ApplicationConstant.OTHER);
        }


        return storeDemandItem;
    }

    @Override
    protected StoreDemandItem convertToEntity(StoreDemandDetailsDto storeDemandDetailsDto) {
        return null;
    }

    @Override
    protected StoreDemandItem updateEntity(StoreDemandDetailsDto dto, StoreDemandItem entity) {
        return null;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<StoreDemandItem> propellerSpecification = new CustomSpecification<StoreDemandItem>()
                .active(Objects.nonNull(searchDto.getIsActive()) ? searchDto.getIsActive() : true, IS_ACTIVE_FIELD).and(buildSpecification(searchDto));
        Page<StoreDemandItem> pagedData = storeDemandDetailsRepository.findAll(propellerSpecification, pageable);

        return PageData.builder()
                .model(getAllPart(pagedData.getContent()))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * Save all method
     *
     * @param storeDemandDetailsDtoList {@link StoreDemandDetailsDto}
     * @param storeDemand               {@link StoreDemand}
     */
    public List<StoreDemandItem> saveAll(List<StoreDemandDetailsDto> storeDemandDetailsDtoList, StoreDemand storeDemand) {
        List<StoreDemandItem> storeDemandItems = new ArrayList<>();
        storeDemandDetailsDtoList.forEach(storeDemandDetailsDto -> prepareEntity(storeDemandDetailsDto, new StoreDemandItem(), storeDemand, storeDemandItems));
        List<StoreDemandItem> storeDemandItemList = super.saveItemList(storeDemandItems);

        Map<Long, String> storeDemandItemMap = storeDemandItemList.stream()
                .collect(Collectors.toMap(StoreDemandItem::getId, StoreDemandItem::getRemarks));
        partRemarkService.saveOrUpdateRemarks(storeDemandItemMap, storeDemand.getId(), RemarkType.STORE_DEMAND);

        return storeDemandItemList;
    }

    private void prepareEntity(StoreDemandDetailsDto storeDemandDetailsDto, StoreDemandItem storeDemandItem, StoreDemand storeDemand, List<StoreDemandItem> storeDemandItems) {
        if (CollectionUtils.isNotEmpty(storeDemandDetailsDto.getAlterPartDtoList())) {
            Long parentId = storeDemandDetailsDto.getPartId();
            storeDemandItems.add(convertToEntity(storeDemandDetailsDto, storeDemandItem, storeDemand, null));
            storeDemandDetailsDto.getAlterPartDtoList().forEach(alterPartDto -> prepareConvertEntity(alterPartDto, new StoreDemandItem(), storeDemandDetailsDto, storeDemand, storeDemandItems, parentId));
        } else {
            storeDemandItems.add(convertToEntity(storeDemandDetailsDto, storeDemandItem, storeDemand, null));
        }
    }

    private void prepareConvertEntity(AlterPartDto alterPartDto, StoreDemandItem storeDemandItem, StoreDemandDetailsDto storeDemandDetailsDto, StoreDemand storeDemand, List<StoreDemandItem> storeDemandItems, Long parentId) {
        storeDemandDetailsDto.setPartId(alterPartDto.getPartId());
        storeDemandDetailsDto.setUnitMeasurementId(alterPartDto.getUomId());
        storeDemandDetailsDto.setQuantityDemanded(ApplicationConstant.VALUE_ZERO);
        storeDemandItems.add(convertToEntity(storeDemandDetailsDto, storeDemandItem, storeDemand, parentId));
    }

    /**
     * Update all method
     *
     * @param storeDemandDetailsDtoList {@link StoreDemandDetailsDto}
     * @param storeDemand               {@link StoreDemand}
     */
    public List<StoreDemandItem> updateAll(List<StoreDemandDetailsDto> storeDemandDetailsDtoList, StoreDemand storeDemand) {

        Map<Long, StoreDemandItem> itemDemandDetailsMap = storeDemandDetailsRepository.findByStoreDemandId(storeDemand.getId())
                .stream().collect(Collectors.toMap(StoreDemandItem::getId, Function.identity()));

        List<StoreDemandItem> storeDemandItems = new ArrayList<>();
        storeDemandDetailsDtoList.forEach(storeDemandDetailsDto -> prepareUpdateEntity(storeDemandDetailsDto, itemDemandDetailsMap,
                storeDemand, storeDemandItems));


        List<StoreDemandItem> storeDemandItemList = super.saveItemList(storeDemandItems);
        Map<Long, String> storeDemandItemMap = storeDemandItemList.stream()
                .collect(Collectors.toMap(StoreDemandItem::getId, StoreDemandItem::getRemarks));

        partRemarkService.saveOrUpdateRemarks(storeDemandItemMap, storeDemand.getId(), RemarkType.STORE_DEMAND);

        return storeDemandItemList;
    }

    private void prepareUpdateEntity(StoreDemandDetailsDto storeDemandDetailsDto, Map<Long, StoreDemandItem> itemDemandDetailsMap,
                                     StoreDemand storeDemand, List<StoreDemandItem> storeDemandItems) {
        if (CollectionUtils.isNotEmpty(storeDemandDetailsDto.getAlterPartDtoList())) {
            Long parentId = storeDemandDetailsDto.getPartId();
            storeDemandItems.add(convertToEntity(storeDemandDetailsDto, itemDemandDetailsMap.getOrDefault(storeDemandDetailsDto.getId(), new StoreDemandItem()), storeDemand, null));
            storeDemandDetailsDto.getAlterPartDtoList().forEach(alterPartDto -> prepareConvertEntity(alterPartDto,
                    itemDemandDetailsMap.getOrDefault(alterPartDto.getId(), new StoreDemandItem()), storeDemandDetailsDto, storeDemand, storeDemandItems, parentId));
        } else {
            storeDemandItems.add(convertToEntity(storeDemandDetailsDto, itemDemandDetailsMap.getOrDefault(storeDemandDetailsDto.getId(), new StoreDemandItem()), storeDemand, null));
        }
    }


    /**
     * Check dependency with parts
     */
    public boolean existByParts(Long partId) {
        return storeDemandDetailsRepository.existsByPartIdAndIsActiveTrue(partId);
    }

    public Set<StoreDemandItem> findByStoreDemandIdIn(Set<Long> demandIds) {
        return storeDemandDetailsRepository.findByStoreDemandIdInAndIsActiveTrue(demandIds);
    }

    public Optional<StoreDemandItem> findByIdAndIsActiveTrue(Long demandId) {
        return storeDemandDetailsRepository.findByIdAndIsActiveTrue(demandId);
    }

    public List<StoreDemandDetailsDto> getResponseWithAlternatePart(Set<StoreDemandItem> storeDemandItemList, RemarkType remarkType, Set<Long> parentIds) {
        Set<Long> collectionsOfPartIds = storeDemandItemList.stream().map(StoreDemandItem::getPartId).collect(Collectors.toSet());
        Set<Long> uomIds = storeDemandItemList.stream().map(StoreDemandItem::getUomId).collect(Collectors.toSet());

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionsOfPartIds).stream()
                .collect(Collectors.toMap(PartProjection::getId, Function.identity()));

        Map<Long, UnitMeasurementProjection> unitMeasurementProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(uomIds).stream()
                .collect(Collectors.toMap(UnitMeasurementProjection::getId, Function.identity()));

        Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap = storePartAvailabilityService.findPartQuantityByPartIdIn(collectionsOfPartIds)
                .stream().collect(Collectors.toMap(StorePartAvailabilityProjection::getPartId, Function.identity()));

        Set<Long> collectionItemIds = storeDemandItemList.stream()
                .map(StoreDemandItem::getId).collect(Collectors.toSet());

        Map<Long, Map<Long, String>> partRemarkMap = partRemarkService.findByItemIdInAndRemarkTypeAndParentIdIn(collectionItemIds, remarkType, parentIds)
                .stream().collect(Collectors.groupingBy(PartRemark::getItemId, Collectors.toMap(PartRemark::getParentId, PartRemark::getRemark))); // TODO: multiple issue/rq
        Map<Long, StoreDemandItemProjection> storeDemandItemProjectionMap = storeDemandDetailsRepository.findByIdInAndIsActiveTrue(collectionItemIds)
                .stream().collect(Collectors.toMap(StoreDemandItemProjection::getId, Function.identity()));
        List<StoreDemandItem> parentPartStoreDemandList = storeDemandItemList.stream().filter(e -> Objects.isNull(e.getParentPartId())).collect(Collectors.toList());
        return parentPartStoreDemandList.stream()
                .map(itemDemandDetails ->
                        convertToAlternatePartStoreDemandResponse(itemDemandDetails,
                                partProjectionMap.get(itemDemandDetails.getPartId()),
                                unitMeasurementProjectionMap.get(itemDemandDetails.getUomId()),
                                partRemarkMap.getOrDefault(itemDemandDetails.getId(), Collections.emptyMap()),
                                storePartAvailabilityProjectionMap.getOrDefault(itemDemandDetails.getPartId(), null),
                                storeDemandItemList, storeDemandItemProjectionMap.get(itemDemandDetails.getId())
                        )).collect(Collectors.toList());
    }

    private StoreDemandDetailsDto convertToAlternatePartStoreDemandResponse(StoreDemandItem itemDemandDetails,
                                                                            PartProjection partProjection,
                                                                            UnitMeasurementProjection unitMeasurementProjection,
                                                                            Map<Long, String> remarkMap,
                                                                            StorePartAvailabilityProjection storePartAvailabilityProjection,
                                                                            Set<StoreDemandItem> storeDemandItemList,
                                                                            StoreDemandItemProjection storeDemandItemProjection) {

        StoreDemandDetailsDto storeDemandDetailsDto = convertToResponseDto(itemDemandDetails, partProjection, unitMeasurementProjection,
                remarkMap, storePartAvailabilityProjection, storeDemandItemProjection);
        List<StoreDemandItem> alterNatePartDemandItemList = storeDemandItemList.stream().filter(e -> Objects.nonNull(e.getParentPartId()))
                .filter(e -> e.getParentPartId().equals(itemDemandDetails.getPartId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(alterNatePartDemandItemList)) {
            List<AlterPartDto> alterPartDtoList = alterNatePartDemandItemList.stream().map(this::populateResponse).collect(Collectors.toList());
            storeDemandDetailsDto.setAlterPartDtoList(alterPartDtoList);
        }
        return storeDemandDetailsDto;
    }

    private AlterPartDto populateResponse(StoreDemandItem storeDemandItem) {
        return AlterPartDto.builder()
                .id(storeDemandItem.getId())
                .partId(storeDemandItem.getPartId())
                .partNo(storeDemandItem.getPart().getPartNo())
                .partDescription(storeDemandItem.getPart().getDescription())
                .uomCode(storeDemandItem.getUnitMeasurement().getCode())
                .uomId(storeDemandItem.getUomId())
                .build();
    }

    public List<StoreDemandDetailsDto> getResponse(Set<StoreDemandItem> storeDemandItemList, RemarkType remarkType, Set<Long> parentIds) {

        Set<Long> collectionsOfPartIds = storeDemandItemList.stream().map(StoreDemandItem::getPartId).collect(Collectors.toSet());
        Set<Long> uomIds = storeDemandItemList.stream().map(StoreDemandItem::getUomId).collect(Collectors.toSet());

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionsOfPartIds).stream()
                .collect(Collectors.toMap(PartProjection::getId, Function.identity()));

        Map<Long, UnitMeasurementProjection> unitMeasurementProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(uomIds).stream()
                .collect(Collectors.toMap(UnitMeasurementProjection::getId, Function.identity()));

        Map<Long, StorePartAvailabilityProjection> storePartAvailabilityProjectionMap = storePartAvailabilityService.findPartQuantityByPartIdIn(collectionsOfPartIds)
                .stream().collect(Collectors.toMap(StorePartAvailabilityProjection::getPartId, Function.identity()));

        Set<Long> collectionItemIds = storeDemandItemList.stream()
                .map(StoreDemandItem::getId).collect(Collectors.toSet());

        Map<Long, Map<Long, String>> partRemarkMap = partRemarkService.findByItemIdInAndRemarkTypeAndParentIdIn(collectionItemIds, remarkType, parentIds)
                .stream().collect(Collectors.groupingBy(PartRemark::getItemId, Collectors.toMap(PartRemark::getParentId, PartRemark::getRemark))); // TODO: multiple issue/rq


        Map<Long, StoreDemandItemProjection> storeDemandItemProjectionMap = storeDemandDetailsRepository.findByIdInAndIsActiveTrue(collectionItemIds)
                .stream().collect(Collectors.toMap(StoreDemandItemProjection::getId, Function.identity()));

        return storeDemandItemList
                .stream()
                .map(itemDemandDetails ->
                        convertToResponseDto(itemDemandDetails,
                                partProjectionMap.get(itemDemandDetails.getPartId()),
                                unitMeasurementProjectionMap.get(itemDemandDetails.getUomId()),
                                partRemarkMap.getOrDefault(itemDemandDetails.getId(), Collections.emptyMap()),
                                storePartAvailabilityProjectionMap.getOrDefault(itemDemandDetails.getPartId(), null),
                                storeDemandItemProjectionMap.get(itemDemandDetails.getId())))
                .collect(Collectors.toList());
    }

    private StoreDemandDetailsDto convertToResponseDto(StoreDemandItem storeDemandItem,
                                                       PartProjection partProjection,
                                                       UnitMeasurementProjection unitMeasurementProjection,
                                                       Map<Long, String> remarkMap,
                                                       StorePartAvailabilityProjection storePartAvailabilityProjection,
                                                       StoreDemandItemProjection storeDemandItemProjection) {

        StoreDemandDetailsDto demandDetailsDto = StoreDemandDetailsDto.builder()
                .id(storeDemandItem.getId())
                .isActive(storeDemandItem.getIsActive())
                .remark(storeDemandItem.getRemarks())
                .ipcCmm(storeDemandItem.getIpcCmm())
                .parentPartId(storeDemandItem.getParentPartId())
                .totalIssuedQty(storeDemandItem.getIssuedQty())
                .quantityDemanded(storeDemandItem.getQuantityDemanded())
                .storeDemandId(storeDemandItem.getStoreDemandId())
                .priorityType(storeDemandItem.getPriorityType())
                .availablePart(Objects.nonNull(storePartAvailabilityProjection) ?
                        storePartAvailabilityProjection.getQuantity() : 0)
                .build();

        demandDetailsDto.setParentWiseRemarks(remarkMap);

        if (Objects.nonNull(partProjection)) {
            demandDetailsDto.setPartNo(partProjection.getPartNo());
            demandDetailsDto.setPartClassification(partProjection.getClassification());
            demandDetailsDto.setPartId(partProjection.getId());
            demandDetailsDto.setPartDescription(partProjection.getDescription());
        }
        if (Objects.nonNull(unitMeasurementProjection)) {
            demandDetailsDto.setUnitMeasurementId(unitMeasurementProjection.getId());
            demandDetailsDto.setUnitMeasurementCode(unitMeasurementProjection.getCode());
        }

        if (Objects.nonNull(storeDemandItemProjection)) {
            if (Objects.nonNull(storeDemandItemProjection.getStoreDemandVendorName())) {
                demandDetailsDto.setDepartment(storeDemandItemProjection.getStoreDemandVendorName());
            } else {
                demandDetailsDto.setDepartment(storeDemandItemProjection.getStoreDemandInternalDepartmentCode());
            }
            demandDetailsDto.setAirCraftName(storeDemandItemProjection.getStoreDemandAircraftAircraftName());
        }
        return demandDetailsDto;
    }

    @Override
    protected Specification<StoreDemandItem> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    private List<ItemPartViewModel> getAllPart(List<StoreDemandItem> storeDemandItemList) {
        Set<Long> partIdList = storeDemandItemList
                .stream()
                .map(StoreDemandItem::getPartId)
                .collect(Collectors.toSet());
        return partService.findPartByIdIn(partIdList)
                .stream()
                .map(part -> ItemPartViewModel.of(
                        part.getId(),
                        part.getPartNo(),
                        part.getDescription()))
                .collect(Collectors.toList());
    }

    public boolean existsByUomIdAndPartIdAndIsActiveTrue(Long uomId, Long partId) {
        return storeDemandDetailsRepository.existsByUomIdAndPartIdAndIsActiveTrue(uomId, partId);
    }
}
