package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturn;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartAvailabilityProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnitMeasurementProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreReturnPartRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ReturnPartsDetailViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreReturnPartResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreReturnPartRepository;
import com.digigate.engineeringmanagement.storemanagement.service.planning.PartInactiveInfoGetService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StoreReturnPartService extends AbstractSearchService<StoreReturnPart, StoreReturnPartRequestDto, IdQuerySearchDto> {

    private final StoreReturnPartRepository storeReturnPartRepository;
    private final ReturnPartsDetailService returnPartsDetailService;
    private final PartService partService;
    private final StorePartAvailabilityService storePartAvailableService;
    private final UnitMeasurementService unitMeasurementService;
    private final PartWiseUomService partWiseUomService;
    private final PartInactiveInfoGetService partInactiveInfoGetService;

    public StoreReturnPartService(AbstractRepository<StoreReturnPart> repository,
                                  @Lazy StoreReturnPartRepository storeReturnPartRepository,
                                  @Lazy ReturnPartsDetailService returnPartsDetailService,
                                  PartInactiveInfoGetService partInactiveInfoGetService,
                                  PartService partService, StorePartAvailabilityService storePartAvailableService,
                                  UnitMeasurementService unitMeasurementService, PartWiseUomService partWiseUomService) {
        super(repository);
        this.storeReturnPartRepository = storeReturnPartRepository;
        this.returnPartsDetailService = returnPartsDetailService;
        this.partService = partService;
        this.storePartAvailableService = storePartAvailableService;
        this.unitMeasurementService = unitMeasurementService;
        this.partWiseUomService = partWiseUomService;
        this.partInactiveInfoGetService = partInactiveInfoGetService;
    }

    public List<StoreReturnPartResponseDto> getResponse(List<StoreReturnPart> storeReturnParts) {
        Set<Long> collectionOfPartIds = storeReturnParts.stream().map(StoreReturnPart::getPartId).collect(Collectors.toSet());
        Set<Long> installedPartUomIds = storeReturnParts.stream().map(StoreReturnPart::getInstallPartUomId).collect(Collectors.toSet());
        Set<Long> removedPartUomIds = storeReturnParts.stream().map(StoreReturnPart::getRemovedPartUomId).collect(Collectors.toSet());
        collectionOfPartIds.addAll(storeReturnParts.stream().map(StoreReturnPart::getInstalledPartId).collect(Collectors.toSet()));
        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionOfPartIds)
                .stream().collect(Collectors.toMap(PartProjection::getId, Function.identity()));

       Map<Long, UnitMeasurementProjection> installedPartUomProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(installedPartUomIds)
               .stream().collect(Collectors.toMap(UnitMeasurementProjection::getId,Function.identity()));

       Map<Long, UnitMeasurementProjection> removedPartUomProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(removedPartUomIds)
               .stream().collect(Collectors.toMap(UnitMeasurementProjection::getId,Function.identity()));

        Set<Long> collectionOfUnserviceablePartIds = storeReturnParts.stream()
                .map(StoreReturnPart::getId).collect(Collectors.toSet());

        List<ReturnPartsDetail> unserviceablePartList = returnPartsDetailService
                .findByStoreReturnPartIdInAndIsActiveTrue(collectionOfUnserviceablePartIds);
        Map<Long,Integer> availablePartMap = storePartAvailableService.findPartQuantityByPartIdIn(storeReturnParts.stream()
                        .map(StoreReturnPart::getPartId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(StorePartAvailabilityProjection::getPartId, StorePartAvailabilityProjection::getQuantity));

        return storeReturnParts.stream()
                .map(storeReturnPart ->
                        convertToResponseDto(storeReturnPart,
                                partProjectionMap.get(storeReturnPart.getPartId()),
                                partProjectionMap.get(storeReturnPart.getInstalledPartId()),
                                availablePartMap,
                                unserviceablePartList,
                                installedPartUomProjectionMap.get(storeReturnPart.getInstallPartUomId()),
                                removedPartUomProjectionMap.get(storeReturnPart.getRemovedPartUomId())
                                ))
                .collect(Collectors.toList());
    }

    private StoreReturnPartResponseDto convertToResponseDto(StoreReturnPart storeReturnPart,
                                                            PartProjection partProjection,
                                                            PartProjection installedPartProjection,
                                                            Map<Long, Integer> availablePartMap,
                                                            List<ReturnPartsDetail> returnPartsDetail,
                                                            UnitMeasurementProjection installedPartUoMProjection,
                                                            UnitMeasurementProjection removedPartUomProjection) {

        List<ReturnPartsDetail> returnPartsDetailList = returnPartsDetail.stream()
                .filter(returnUnserviceablePartKey ->
                        returnUnserviceablePartKey.getStoreReturnPartId().equals(storeReturnPart.getId()))
                .collect(Collectors.toList());

        List<ReturnPartsDetailViewModel> returnServiceableDetailViewModelList = returnPartsDetailService
                .getResponseData(returnPartsDetailList);

        StoreReturnPartResponseDto dto = StoreReturnPartResponseDto.builder()
                .id(storeReturnPart.getId())
                .storeReturnId(storeReturnPart.getStoreReturnId())
                .cardLineNo(storeReturnPart.getCardLineNo())
                .description(storeReturnPart.getDescription())
                .releaseNo(storeReturnPart.getReleaseNo())
                .quantityReturn(storeReturnPart.getQuantityReturn())
                .build();
        dto.setAvailableQuantity(availablePartMap.get(storeReturnPart.getPartId()));
        dto.setPartsDetailViewModels(returnServiceableDetailViewModelList);
        dto.setSerialViewModelLite(storeReturnPartRepository.findGrnShelfAndRackLife(storeReturnPart.getId()));
        dto.setLotNumber(storeReturnPartRepository.findLotNumber(storeReturnPart.getId()));
        dto.setOtherLocation(storeReturnPartRepository.findOtherLocationById(storeReturnPart.getId()));
        dto.setPartOrderNo(storeReturnPartRepository.findPartOrderNoById(storeReturnPart.getId()));
        dto.setAlternatePart(storeReturnPartRepository.findAlternatePartNameById(storeReturnPart.getId()));
        if (Objects.nonNull(partProjection)) {
            dto.setPartNo(partProjection.getPartNo());
            dto.setPartId(partProjection.getId());
            dto.setPartDescription(partProjection.getDescription());
        }
        if (Objects.nonNull(installedPartProjection)) {
            dto.setInstalledPartNo(installedPartProjection.getPartNo());
            dto.setInstalledPartId(installedPartProjection.getId());
            dto.setInstalledPartDescription(installedPartProjection.getDescription());
        }
        if(Objects.nonNull(removedPartUomProjection)){
            dto.setRemovedPartUomId(removedPartUomProjection.getId());
            dto.setRemovedPartUomCode(removedPartUomProjection.getCode());
        }
        if(Objects.nonNull(installedPartUoMProjection)){
            dto.setInstalledPartUomId(installedPartUoMProjection.getId());
            dto.setInstalledPartUomCode(installedPartUoMProjection.getCode());
        }

        /** inactive status setting */
        dto.setIsInactive(storeReturnPart.getIsInactive());
        return dto;
    }

    @Override
    protected <T> T convertToResponseDto(StoreReturnPart storeReturnPart) {
        return null;
    }

    @Override
    protected StoreReturnPart convertToEntity(StoreReturnPartRequestDto storeReturnPartRequestDto) {
        return null;
    }

    @Override
    protected StoreReturnPart updateEntity(StoreReturnPartRequestDto dto, StoreReturnPart entity) {
        return null;
    }

    @Override
    protected Specification<StoreReturnPart> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StoreReturnPart> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.RELEASE_NO)
        );
    }

    public List<StoreReturnPart> findByStoreReturnIdIn(Set<Long> id) {
        return storeReturnPartRepository.findByStoreReturnIdInAndIsActiveTrue(id);
    }

    public Set<StoreReturnPart> findByIdIn(Set<Long> ids) {
        return storeReturnPartRepository.findByIdIn(ids);
    }

    public void saveAll(List<StoreReturnPartRequestDto> returnPartRequestDtoList, StoreReturn storeReturn) {
        /** populate inactive info from planning */
        partInactiveInfoGetService.populateWithPartInfo(returnPartRequestDtoList);
        populateDtoAndSavePartReturn(returnPartRequestDtoList, storeReturn);
        returnPartsDetailService.saveAll(returnPartRequestDtoList);

        /** Reset active part count */
        storeReturn.setActivePartCount(Math.toIntExact(
                returnPartRequestDtoList.stream().filter(r->r.getIsInactive() == Boolean.FALSE).count()));
    }

    public void updateAll(List<StoreReturnPartRequestDto> returnPartRequestDtoList, StoreReturn storeReturn) {
        populateDtoAndSavePartReturn(returnPartRequestDtoList, storeReturn);
        returnPartRequestDtoList.stream()
                .filter(storeReturnPartRequestDto -> Objects.nonNull(storeReturnPartRequestDto.getReturnPartsDetailDto()))
                .forEach(returnPartsDetailService::update);
    }

    private void populateDtoAndSavePartReturn(List<StoreReturnPartRequestDto> dtos, StoreReturn storeReturn) {
        Set<Long> partIdList = dtos.stream().map(StoreReturnPartRequestDto::getPartId).collect(Collectors.toSet());
        Set<Long> installedPartUomIds = dtos.stream().map(StoreReturnPartRequestDto::getInstalledPartUomId).collect(Collectors.toSet());
        Set<Long> removedPartUomIds = dtos.stream().map(StoreReturnPartRequestDto::getRemovedPartUomId).collect(Collectors.toSet());

        Map<Long, Part> partMap = partService.getAllByDomainIdIn(partIdList, true)
                .stream().collect(Collectors.toMap(Part::getId, Function.identity()));

        Map<Long, UnitMeasurement> installedPartUomMap = unitMeasurementService.getAllByDomainIdIn(installedPartUomIds,true)
                .stream().collect(Collectors.toMap(UnitMeasurement::getId , Function.identity()));

        Map<Long, UnitMeasurement> removedPartUomMap = unitMeasurementService.getAllByDomainIdIn(removedPartUomIds,true)
                .stream().collect(Collectors.toMap(UnitMeasurement::getId , Function.identity()));

        Set<Long> updateIdList = dtos.stream().map(StoreReturnPartRequestDto::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, StoreReturnPart> storeReturnPartMap = storeReturnPartRepository.findAllByIdIn(updateIdList)
                .stream().collect(Collectors.toMap(StoreReturnPart::getId, Function.identity()));

        dtos.forEach(storeReturnPartRequestDto -> {
            Part part = partMap.get(storeReturnPartRequestDto.getPartId());
            if (Objects.isNull(part)) {
                throw EngineeringManagementServerException.notFound(ErrorId.PART_NOT_FOUND);
            }

            StoreReturnPart returnPart = storeReturnPartMap.getOrDefault(storeReturnPartRequestDto.getId(), new StoreReturnPart());
            StoreReturnPart storeReturnPart = populateToEntity(storeReturnPartRequestDto, returnPart, part, storeReturn,
                    removedPartUomMap.get(storeReturnPartRequestDto.getRemovedPartUomId()),
                    installedPartUomMap.get(storeReturnPartRequestDto.getInstalledPartUomId()));
            super.saveItem(storeReturnPart);
            storeReturnPartRequestDto.setStoreReturnPart(storeReturnPart);
        });
    }

    private StoreReturnPart populateToEntity(StoreReturnPartRequestDto storeReturnPartRequestDto,
                                             StoreReturnPart storeReturnPart,
                                             Part part,
                                             StoreReturn storeReturn,
                                             UnitMeasurement removedPartUnitMeasurement,
                                             UnitMeasurement installedPartUnitMeasurement) {

        if (Objects.nonNull(part)) {
            storeReturnPart.setPart(part);
            partWiseUomService.updateAll(List.of(removedPartUnitMeasurement.getId()), part, ApplicationConstant.OTHER);
        }
        if (Objects.nonNull(storeReturnPartRequestDto.getInstalledPartId())) {
            Part installedPart = partService.findById(storeReturnPartRequestDto.getInstalledPartId());
            if (Objects.nonNull(installedPart)) {
                storeReturnPart.setInstalledPart(installedPart);
                partWiseUomService.updateAll(List.of(installedPartUnitMeasurement.getId()), installedPart, ApplicationConstant.OTHER);
            }
        }
        if (Objects.nonNull(installedPartUnitMeasurement)) {
            storeReturnPart.setInstalledPartUom(installedPartUnitMeasurement);
        }
        if (Objects.nonNull(removedPartUnitMeasurement)) {
            storeReturnPart.setRemovedPartUom(removedPartUnitMeasurement);
        }
        storeReturnPart.setDescription(storeReturnPartRequestDto.getDescription());
        storeReturnPart.setStoreReturn(storeReturn);
        validateQuantity(storeReturn, storeReturnPart, storeReturnPartRequestDto);
        storeReturnPart.setQuantityReturn(storeReturnPartRequestDto.getQuantityReturn());
        storeReturnPart.setCardLineNo(storeReturnPartRequestDto.getCardLineNo());
        storeReturnPart.setReleaseNo(storeReturnPartRequestDto.getReleaseNo());
        storeReturnPart.setIsInactive(storeReturnPartRequestDto.getIsInactive());
        return storeReturnPart;
    }

    private void validateQuantity(StoreReturn storeReturn, StoreReturnPart storeReturnPart, StoreReturnPartRequestDto storeReturnPartRequestDto) {
        if (storeReturn.getStockRoomType() == StockRoomType.STORE_RETURN_COMPONENT) {
            if (storeReturnPartRequestDto.getQuantityReturn() > ApplicationConstant.INT_ONE) {
                throw EngineeringManagementServerException.dataSaveException(ErrorId.RETURN_PART_QUANTITY_MUST_BE_ONE);
            }
            storeReturnPart.setQuantityReturn(storeReturnPartRequestDto.getQuantityReturn());
        }
    }

    public List<DashboardProjection> getStoreReturnPartDataForLastOneMonth(Integer month) {
        return storeReturnPartRepository.getStoreReturnPartDataForLastOneMonth(month);
    }

    public boolean existsByInstallPartUomIdAndIsActiveTrue(Long uomId, Long installPartId) {
        return storeReturnPartRepository.existsByInstallPartUomIdAndInstalledPartIdAndIsActiveTrue(uomId, installPartId);
    }

    public boolean existsByRemovedPartUomIdAndIsActiveTrue(Long uomId, Long removePartId) {
        return storeReturnPartRepository.existsByRemovedPartUomIdAndPartIdAndIsActiveTrue(uomId, removePartId);
    }
}
