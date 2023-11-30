package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnitMeasurementProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.UnitMeasurementDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.UnitMeasurementResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.UnitMeasurementRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnPartService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UnitMeasurementService extends AbstractSearchService<UnitMeasurement, UnitMeasurementDto, IdQuerySearchDto> {
    private final UnitMeasurementRepository repository;
    private final StoreDemandDetailsService storeDemandDetailsService;
    private StoreReturnPartService storeReturnPartService;
    private final StorePartAvailabilityService storePartAvailabilityService;

    public UnitMeasurementService(UnitMeasurementRepository repository,
                                  @Lazy StoreDemandDetailsService storeDemandDetailsService,
                                  @Lazy StoreReturnPartService storeReturnPartService,
                                  @Lazy StorePartAvailabilityService storePartAvailabilityService) {
        super(repository);
        this.repository = repository;
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.storeReturnPartService = storeReturnPartService;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }
    public Set<UnitMeasurementProjection> findByUnitMeasurementIdIn(Set<Long> collect) {
        return repository.findUnitMeasurementByIdIn(collect);
    }
    public List<UnitMeasurement> findAllByUnitMeasurementIdIn(List<Long> collect) {
        return repository.findUnitMeasurementByIdInAndIsActiveTrue(collect);
    }
    public UnitMeasurementProjection findUnitMeasurementById(Long id) {
        return repository.findUnitMeasurementById(id);
    }

    @Override
    public UnitMeasurement create(UnitMeasurementDto unitMeasurementDto) {
        validate(unitMeasurementDto, null);
        return super.create(unitMeasurementDto);
    }

    public List<UnitMeasurementProjection> findByIdIn(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public UnitMeasurement update(UnitMeasurementDto unitMeasurementDto, Long id) {
        UnitMeasurement unitMeasurement = findByIdUnfiltered(id);
        validate(unitMeasurementDto, id);
        final UnitMeasurement entity = updateEntity(unitMeasurementDto, unitMeasurement);
        return super.saveItem(entity);
    }

    @Override
    protected UnitMeasurementResponseDto convertToResponseDto(UnitMeasurement unitMeasurement) {
        return UnitMeasurementResponseDto.builder()
                .id(unitMeasurement.getId())
                .code(unitMeasurement.getCode())
                .build();
    }

    @Override
    protected UnitMeasurement convertToEntity(UnitMeasurementDto unitMeasurementDto) {
        return UnitMeasurement.builder()
                .code(unitMeasurementDto.getCode())
                .build();
    }

    @Override
    protected UnitMeasurement updateEntity(UnitMeasurementDto dto, UnitMeasurement entity) {
        entity.setCode(dto.getCode());
        return entity;
    }

    private void validate(UnitMeasurementDto dto, Long unitOfMeasureId) {
        UnitMeasurement unitMeasurement = repository.findByCodeIgnoreCase(dto.getCode());
        if (Objects.nonNull(unitMeasurement) && (Objects.isNull(unitOfMeasureId)
                || !unitOfMeasureId.equals(unitMeasurement.getId()))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.UNIT_MEASUREMENT_CODE_EXISTS);
        }
    }

    @Override
    protected Specification<UnitMeasurement> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<UnitMeasurement> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }

    public Set<UnitMeasurement> findAllUnitOfMeasures() {
        return repository.findAllUnitOfMeasures();
    }

    public List<UnitMeasurement> findAllByCode(Set<String> codeList){
        return repository.findAllByCodeIn(codeList);
    }

}
