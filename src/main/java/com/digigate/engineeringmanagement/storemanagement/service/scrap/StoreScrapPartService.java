package com.digigate.engineeringmanagement.storemanagement.service.scrap;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.util.MapUtil;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrap;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnitMeasurementProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.ScrapPartSerialDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapPartDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.scrap.ScrapPartSerialViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.scrap.StoreScrapPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.scrap.ScrapPartRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StoreScrapPartService extends AbstractSearchService<StoreScrapPart, StoreScrapPartDto, IdQuerySearchDto> {
    private final ScrapPartRepository scrapPartRepository;
    private final StorePartSerialService storePartSerialService;
    private final StoreScrapPartSerialService storeScrapPartSerialService;
    private final PartService partService;
    private final StoreScrapService storeScrapService;
    private final UnitMeasurementService unitMeasurementService;
    private final PartWiseUomService partWiseUomService;

    public StoreScrapPartService(ScrapPartRepository scrapPartRepository,
                                 StorePartSerialService storePartSerialService,
                                 StoreScrapPartSerialService storeScrapPartSerialService, PartService partService,
                                 @Lazy StoreScrapService storeScrapService, UnitMeasurementService unitMeasurementService, PartWiseUomService partWiseUomService) {
        super(scrapPartRepository);
        this.scrapPartRepository = scrapPartRepository;
        this.storePartSerialService = storePartSerialService;
        this.storeScrapPartSerialService = storeScrapPartSerialService;
        this.partService = partService;
        this.storeScrapService = storeScrapService;
        this.unitMeasurementService = unitMeasurementService;
        this.partWiseUomService = partWiseUomService;
    }

    /**
     * This method is responsible for Create & Update
     *
     * @param storeScrapPartDtoList {@link StoreScrapPartDto}
     * @param storeScrap            {@link StoreScrap}
     */
    public void createOrUpdate(List<StoreScrapPartDto> storeScrapPartDtoList, StoreScrap storeScrap, Long scrapId) {

        Set<Long> scrapPartIds = storeScrapPartDtoList.stream().map(StoreScrapPartDto::getId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> uomIds = storeScrapPartDtoList.stream().map(StoreScrapPartDto::getUomId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UnitMeasurement> unitMeasurementMap= unitMeasurementService.getAllByDomainIdIn(uomIds,true)
                .stream().collect(Collectors.toMap(UnitMeasurement::getId,Function.identity()));

        List<StoreScrapPart> storeScrapPartList = getAllByDomainIdInUnfiltered(scrapPartIds);

        if(storeScrapPartList.stream().anyMatch(storeScrapPart -> !Objects.equals(storeScrapPart.getStoreScrapId(), scrapId))){
            throw EngineeringManagementServerException.badRequest(ErrorId.SCRAP_PART_DOES_NOT_EXIST_UNDER_THE_SCRAP);
        }

        Map<Long, StoreScrapPart> storeScrapPartMap = storeScrapPartList.stream().collect(
                Collectors.toMap(StoreScrapPart::getId, Function.identity()));

        Set<Long> partIds = storeScrapPartDtoList.stream().map(StoreScrapPartDto::getPartId).collect(Collectors.toSet());

        Map<Long, Part> partMap = partService.getAllByDomainIdIn(partIds, true).stream().collect(Collectors.toMap(
                Part::getId, Function.identity()));

        List<StoreScrapPart> storeScrapParts = storeScrapPartDtoList.stream().map(storeScrapPartDto -> populateToEntity(
                storeScrapPartDto, storeScrapPartMap.getOrDefault(storeScrapPartDto.getId(), new StoreScrapPart()),
                partMap, storeScrap,unitMeasurementMap.get(storeScrapPartDto.getUomId()))).collect(Collectors.toList());

        super.saveItemList(storeScrapParts);
    }

    public List<StoreScrapPart> findByStoreScrapIdIn(Set<Long> ids) {
        return scrapPartRepository.findByStoreScrapIdInAndIsActiveTrue(ids);
    }

    @Override
    protected StoreScrapPartViewModel convertToResponseDto(StoreScrapPart storeScrapPart) {
        Optional<PartProjection> partProjection = partService.findPartById(storeScrapPart.getPartId());
        StoreScrap storeScrap = storeScrapService.findById(storeScrapPart.getStoreScrapId());
        UnitMeasurementProjection unitMeasurementProjection= unitMeasurementService.findUnitMeasurementById(storeScrapPart.getUomId());
        StoreScrapPartViewModel storeScrapPartViewModel = convertToResponseDto(storeScrapPart, partProjection,unitMeasurementProjection);
        storeScrapPartViewModel.setScrapId(storeScrap.getId());
        storeScrapPartViewModel.setScrapVoucherNo(storeScrap.getVoucherNo());
        return storeScrapPartViewModel;
    }

    public StoreScrapPartViewModel convertToResponseDto(StoreScrapPart storeScrapPart,
                                                        Optional<PartProjection> partProjectionOpt,
                                                        UnitMeasurementProjection unitMeasurementProjection){

        StoreScrapPartViewModel storeScrapPartViewModel = new StoreScrapPartViewModel();
        storeScrapPartViewModel.setId(storeScrapPart.getId());
        storeScrapPartViewModel.setScrapId(storeScrapPart.getStoreScrapId());

        if(partProjectionOpt.isPresent()) {
            PartProjection partProjection = partProjectionOpt.get();
            storeScrapPartViewModel.setPartId(partProjection.getId());
            storeScrapPartViewModel.setPartNo(partProjection.getPartNo());
            storeScrapPartViewModel.setPartDescription(partProjection.getDescription());
            storeScrapPartViewModel.setPartClassification(partProjection.getClassification());
        }
        if(Objects.nonNull(unitMeasurementProjection)){
            storeScrapPartViewModel.setUomId(unitMeasurementProjection.getId());
            storeScrapPartViewModel.setUomCode(unitMeasurementProjection.getCode());
        }

        storeScrapPartViewModel.setPartSerialViewModelList(findSerialById(storeScrapPart));
        storeScrapPartViewModel.setIsAlive(storeScrapPart.getIsAlive());
        storeScrapPartViewModel.setIsActive(storeScrapPart.getIsActive());
        return storeScrapPartViewModel;
    }

    public List<StoreScrapPartViewModel> convertToResponse(List<StoreScrapPart> storeScrapParts) {
        Set<Long> partIds = storeScrapParts.stream().map(StoreScrapPart::getPartId).collect(Collectors.toSet());
        Set<Long> uomIds = storeScrapParts.stream().map(StoreScrapPart::getUomId).collect(Collectors.toSet());
        Map<Long, UnitMeasurementProjection> unitMeasurementProjectionMap= unitMeasurementService.findByUnitMeasurementIdIn(uomIds)
                .stream().collect(Collectors.toMap(UnitMeasurementProjection::getId,Function.identity()));
        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(partIds).stream().collect(
                Collectors.toMap(PartProjection::getId, Function.identity()));

        return storeScrapParts.stream().map(storeScrapPart -> convertToResponseDto(storeScrapPart,
                Optional.ofNullable(partProjectionMap.get(storeScrapPart.getPartId())),
                unitMeasurementProjectionMap.get(storeScrapPart.getUomId()))).collect(Collectors.toList());
    }

    @Override
    protected StoreScrapPart convertToEntity(StoreScrapPartDto storeScrapPartDto) {
        return null;
    }

    @Override
    protected Specification<StoreScrapPart> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected StoreScrapPart updateEntity(StoreScrapPartDto dto, StoreScrapPart entity) {
        return null;
    }

    private StoreScrapPart populateToEntity(StoreScrapPartDto dto,
                                            StoreScrapPart storeScrapPart,
                                            Map<Long, Part> partMap,
                                            StoreScrap storeScrap,
                                            UnitMeasurement unitMeasurement) {


        storeScrapPart.setStoreScrap(storeScrap);
        Part part = MapUtil.getOrElseThrow(partMap, dto.getPartId(), EngineeringManagementServerException.notFound(ErrorId.PART_NOT_FOUND));
        storeScrapPart.setUnitMeasurement(unitMeasurement);
        storeScrapPart.setPart(part);
        partWiseUomService.updateAll(List.of(dto.getUomId()), part, ApplicationConstant.OTHER);
        addStorePartSerial(dto, storeScrapPart);
        return storeScrapPart;
    }

    private void addStorePartSerial(StoreScrapPartDto dto, StoreScrapPart entity) {
        List<ScrapPartSerialDto> scrapPartSerialDtos = dto.getScrapPartSerialDtos();
        List<StoreScrapPartSerial> storeScrapPartSerialList = Objects.nonNull(dto.getId()) ? storeScrapPartSerialService
                .findAllByStoreScrapPartId(dto.getId()) : Collections.emptyList();
        storeScrapPartSerialService.deleteAll(storeScrapPartSerialList);

        Set<Long> storeSerialIds = scrapPartSerialDtos
                .stream().map(ScrapPartSerialDto::getStoreSerialId).collect(Collectors.toSet());

        List<StorePartSerial> serialToBeScrapped = storePartSerialService.getAllByDomainIdIn(storeSerialIds, true);

        if (serialToBeScrapped.stream().anyMatch(StorePartSerial::notExistsInStore)) {
            throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_SERIAL_IS_NOT_FOUND);
        }

        Map<Long, StorePartSerial> storePartSerialMap = serialToBeScrapped.stream().
                collect(Collectors.toMap(StorePartSerial::getId,
                Function.identity()));

        scrapPartSerialDtos.forEach(scrapPartSerialDto -> {
            storeScrapPartSerialService.convertAndSaveEntity(entity, storePartSerialMap.get(scrapPartSerialDto.getStoreSerialId()),
                    scrapPartSerialDto.getQuantity());
        });

    }

    private Set<ScrapPartSerialViewModel> findSerialById(StoreScrapPart storeScrapPart) {
        List<StoreScrapPartSerial> storeScrapPartSerials = storeScrapPartSerialService.findAllByStoreScrapPartId(storeScrapPart.getId());
        Set<Long> partSerialIds = storeScrapPartSerials.stream().map(StoreScrapPartSerial::getStorePartSerialId).collect(Collectors.toSet());
        Map<Long, StorePartSerialProjection> storePartSerialProjectionMap = storePartSerialService.findStorePartSerialByIdIn(partSerialIds).stream()
                .collect(Collectors.toMap(StorePartSerialProjection::getId, Function.identity()));
        return storeScrapPartSerials.stream().map(entity -> populateToViewModels(entity, storePartSerialProjectionMap.get(entity.getStorePartSerialId())))
                .collect(Collectors.toSet());
    }

    private ScrapPartSerialViewModel populateToViewModels(StoreScrapPartSerial entity, StorePartSerialProjection serialProjection) {
        ScrapPartSerialViewModel scrapPartSerialViewModel = new ScrapPartSerialViewModel();
        if (Objects.nonNull(serialProjection)) {
            scrapPartSerialViewModel.setSerialId(serialProjection.getId());
            scrapPartSerialViewModel.setSerialNo(serialProjection.getSerialSerialNumber());

        }
        scrapPartSerialViewModel.setQuantity(entity.getQuantity());
        scrapPartSerialViewModel.setIsActive(true);
        scrapPartSerialViewModel.id(entity.getId());
        return scrapPartSerialViewModel;
    }

    public List<DashboardProjection> getPartInfoForLastOneMonth(Integer month) {
        return scrapPartRepository.getPartInfoForLastOneMonth(month);
    }
}
