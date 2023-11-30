package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.WorkShopRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.WorkShopResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.digigate.engineeringmanagement.configurationmanagement.entity.WorkShop;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.WorkShopRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkShopService extends AbstractSearchService<WorkShop, WorkShopRequestDto, IdQuerySearchDto> {

    private final CityService cityService;
    private final WorkShopRepository repository;

    public WorkShopService(WorkShopRepository repository, CityService cityService) {
        super(repository);
        this.cityService = cityService;
        this.repository = repository;
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<WorkShop> pagedData = repository.findAllByIsActive(isActive, pageable);
        List<WorkShop> workShop = pagedData.getContent();

        Set<Long> collect = workShop.stream()
                .map(WorkShop::getCityId).collect(Collectors.toSet());

        Map<Long, CityProjection> cityProjectionMap =cityService.findByIdIn(collect)
                .stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));

        List<Object> models = pagedData.getContent().stream().map(workShops ->
                convertToResponseDto(workShops, cityProjectionMap.get(workShops.getCityId())))
                .collect(Collectors.toList());

        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected WorkShopResponseDto convertToResponseDto(WorkShop workShop) {
        City city = workShop.getCity();
        IdNameResponse cityIdNameResponse = IdNameResponse.of(city.getId(), city.getName());
        return WorkShopResponseDto.builder()
                .id(workShop.getId())
                .code(workShop.getCode())
                .address(workShop.getAddress())
                .city(cityIdNameResponse)
                .build();
    }

    @Override
    protected WorkShop convertToEntity(WorkShopRequestDto workShopRequestDto) {
        validate(workShopRequestDto, null);
        WorkShop workShop = new WorkShop();
        City city = cityService.findById(workShopRequestDto.getCityId());
        workShop.setCity(city);
        workShop.setCode(workShopRequestDto.getCode());
        workShop.setAddress(workShopRequestDto.getAddress());
        return workShop;
    }

    @Override
    protected WorkShop updateEntity(WorkShopRequestDto dto, WorkShop entity) {
        validate(dto, entity);
        City city = cityService.findById(dto.getCityId());
        entity.setCity(city);
        entity.setCode(dto.getCode());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    @Override
    protected Specification<WorkShop> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<WorkShop> customSpecification = new CustomSpecification<>();
        return Specification.where(
                 customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE)
        );
    }

    private WorkShopResponseDto convertToResponseDto(WorkShop workShop, CityProjection cityProjection) {
        WorkShopResponseDto workShopResponseDto = new WorkShopResponseDto();
        City city = workShop.getCity();
        IdNameResponse cityIdNameResponse = IdNameResponse.of(city.getId(), city.getName());
        workShopResponseDto.setCode(workShop.getCode());
        workShopResponseDto.setAddress(workShop.getAddress());
        workShopResponseDto.setId(workShop.getId());
        workShopResponseDto.setCity(cityIdNameResponse);
        return workShopResponseDto;
    }

    private void validate(WorkShopRequestDto dto, WorkShop old) {
        List<WorkShop> workShops = repository.findByCodeIgnoreCase(dto.getCode());
        if (CollectionUtils.isEmpty(workShops)) {
            return;
        }
        workShops.forEach(workShop -> {
            if (Objects.nonNull(old) && workShop.equals(old)) {
                return;
            }
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.WORK_SHOPS_ALREADY_EXISTS);
        });

    }
}
