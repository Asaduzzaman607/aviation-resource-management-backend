package com.digigate.engineeringmanagement.planning.service;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.PartWiseUom;
import com.digigate.engineeringmanagement.planning.payload.request.PartWiseUomRequestDto;
import com.digigate.engineeringmanagement.planning.payload.response.PartWiseUomResponseDto;
import com.digigate.engineeringmanagement.planning.repository.PartWiseUomRepository;
import com.digigate.engineeringmanagement.planning.service.impl.PartWiseUomFacadeService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartWiseUomProjection;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PartWiseUomService extends AbstractService<PartWiseUom, PartWiseUomRequestDto> {
    private final PartWiseUomRepository partWiseUomRepository;
    private final UnitMeasurementService unitMeasurementService;
    private final PartWiseUomFacadeService partWiseUomFacadeService;


    public PartWiseUomService(AbstractRepository<PartWiseUom> repository, PartWiseUomRepository partWiseUomRepository,
                              UnitMeasurementService unitMeasurementService, @Lazy PartWiseUomFacadeService partWiseUomFacadeService) {
        super(repository);
        this.partWiseUomRepository = partWiseUomRepository;
        this.unitMeasurementService = unitMeasurementService;
        this.partWiseUomFacadeService = partWiseUomFacadeService;

    }

    @Override
    protected PartWiseUomResponseDto convertToResponseDto(PartWiseUom partWiseUom) {
        return null;
    }

    @Override
    protected PartWiseUom convertToEntity(PartWiseUomRequestDto partWiseUomRequestDto) {
        return null;
    }

    @Override
    protected PartWiseUom updateEntity(PartWiseUomRequestDto partWiseUomRequestDto, PartWiseUom partWiseUom) {
        return null;
    }

    public void saveAll(Part part, List<Long> partWiseUomIds) {
        Map<Long, UnitMeasurement> unitMeasurements = unitMeasurementService.findAllByUnitMeasurementIdIn(partWiseUomIds)
                .stream().collect(Collectors.toMap(UnitMeasurement::getId, Function.identity()));
        List<PartWiseUom> partWiseUom = partWiseUomIds.stream()
                .map(partUom -> convertToSaveEntity(part, unitMeasurements.get(partUom)))
                .collect(Collectors.toList());
        try {
            partWiseUomRepository.saveAll(partWiseUom);
        } catch (Exception e) {
            throw EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_SAVED);
        }
    }

    private PartWiseUom convertToSaveEntity(Part part, UnitMeasurement unitMeasurement) {
        PartWiseUom partWiseUom = new PartWiseUom();
        partWiseUom.setPart(part);
        partWiseUom.setUnitMeasurement(unitMeasurement);
        return partWiseUom;
    }

    public List<PartWiseUomProjection> getAllByPartIdIn(Set<Long> id) {
        return partWiseUomRepository.findAllByPartIdInAndIsActiveTrue(id);
    }

    public void updateAll(List<Long> partWiseUomIds, Part part, String module) {
        List<PartWiseUom> partWiseUomList = partWiseUomRepository.findAllByPartId(part.getId());
        List<PartWiseUom> finalPartWiseUomList = new ArrayList<>();
        //save new uom
        Set<Long> uomIds = partWiseUomList.stream().map(PartWiseUom::getUomId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (Objects.nonNull(partWiseUomIds)) {
            List<Long> latestUomIds = partWiseUomIds.stream().filter(uomId -> !uomIds.contains(uomId)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(latestUomIds)) {
                saveAll(part, latestUomIds);
            }
            //active old inactive partWiseUom
            List<PartWiseUom> activeOldUom = partWiseUomList.stream().filter(uomId -> partWiseUomIds.contains(uomId.getUnitMeasurement().getId())
                    && uomId.getIsActive().equals(Boolean.FALSE)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(activeOldUom)) {
                activeOldUom.forEach(uom -> finalPartWiseUomList.add(convertReActiveEntity(uom)));

            }
            //inActive active partWiseUom
            if (module.equals(ApplicationConstant.PART)) {
                List<PartWiseUom> deletedUom = partWiseUomList.stream().filter(uomId -> !partWiseUomIds.contains(uomId.getUnitMeasurement().getId())
                        && uomId.getIsActive().equals(Boolean.TRUE)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deletedUom)) {
                    deletedUom.forEach(uom -> finalPartWiseUomList.add(convertDeleteEntity(uom)));
                }

            }
            saveItemList(finalPartWiseUomList);
        }
    }

    private PartWiseUom convertReActiveEntity(PartWiseUom partWiseUom) {
        partWiseUom.setIsActive(true);
        return partWiseUom;
    }

    private PartWiseUom convertDeleteEntity(PartWiseUom partWiseUom) {
        if (partWiseUomFacadeService.existsByUomIdAndIsActiveTrueInStoreDemandItem(partWiseUom.getUomId(), partWiseUom.getPartId())
                || partWiseUomFacadeService.existsByInstalledUomIdAndIsActiveTrueInStoreReturnPart(partWiseUom.getUomId(), partWiseUom.getPartId())
                || partWiseUomFacadeService.existsByRemovedUomIdAndIsActiveTrueInStoreReturnPart(partWiseUom.getUomId(), partWiseUom.getPartId())
                || partWiseUomFacadeService.existsByUomIdAndIsActiveTrueInVendorQuotation(partWiseUom.getUomId(), partWiseUom.getPartId()))
        {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        partWiseUom.setIsActive(false);
        return partWiseUom;
    }

    public PartWiseUomResponseDto convertPartWiseUomResponse(PartWiseUomProjection partWiseUomProjection) {
        PartWiseUomResponseDto partWiseUomResponseDto = new PartWiseUomResponseDto();
        partWiseUomResponseDto.setId(partWiseUomProjection.getId());
        partWiseUomResponseDto.setUomCode(partWiseUomProjection.getUnitMeasurementCode());
        partWiseUomResponseDto.setUomId(partWiseUomProjection.getUnitMeasurementId());
        partWiseUomResponseDto.setPartId(partWiseUomProjection.getPartId());
        return partWiseUomResponseDto;
    }

    public void saveExcelPartWiseUom(List<Part> parts, Map<String, List<UnitMeasurement>> partWiseUomMap) {
        List<PartWiseUom> partWiseUomList = new ArrayList<>();
        parts.forEach(part -> {
            List<UnitMeasurement> unitMeasurementList = partWiseUomMap.get(part.getPartNo() + part.getModel().getModelName());
            unitMeasurementList.forEach(uom -> {
                PartWiseUom partWiseUom = new PartWiseUom();
                partWiseUom.setPart(part);
                partWiseUom.setUnitMeasurement(uom);
                partWiseUomList.add(partWiseUom);
            });
        });
        saveItemList(partWiseUomList);
    }

    public List<PartWiseUom> getAllByPartId(Long id) {
        return partWiseUomRepository.findAllByPartId(id);
    }
}

