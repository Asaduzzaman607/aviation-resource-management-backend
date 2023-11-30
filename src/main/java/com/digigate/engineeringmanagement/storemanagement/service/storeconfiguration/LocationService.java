package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.CityService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Location;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.LocationProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.LocationDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.LocationResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.LocationRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

@Service
public class LocationService extends AbstractSearchService<Location, LocationDto,IdQuerySearchDto> {

    private final CityService cityService;
    private final LocationRepository repository;
    private final OfficeService officeService;
    private final StoreReturnService storeReturnService;

    @Autowired
    public LocationService(LocationRepository repository,
                           CityService cityService,
                           @Lazy OfficeService officeService,
                           @Lazy StoreReturnService storeReturnService) {
        super(repository);
        this.cityService = cityService;
        this.officeService = officeService;
        this.repository = repository;
        this.storeReturnService = storeReturnService;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Location> locationSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Location>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<Location> locationsPage = repository.findAll(locationSpecification, pageable);
        List<Location> locationList = locationsPage.getContent();
        return PageData.builder()
                .model(getDataFromParent(locationList))
                .totalPages(locationsPage.getTotalPages())
                .totalElements(locationsPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public boolean existsByCityIdAndIsActiveTrue(Long cityId) {
        return repository.existsByCityIdAndIsActiveTrue(cityId);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && officeService.existsByLocationsIdAndIsActiveTrue(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        if (isActive == FALSE && storeReturnService.existsByLocationIdAndIsActiveTrue(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        super.updateActiveStatus(id, isActive);
    }

    @Override
    public LocationResponseDto getSingle(Long id) {
        Location location = findByIdUnfiltered(id);
        Set<Long> cityId = Collections.singleton(location.getCityId());
        Map<Long, CityProjection> cityProjectionMap = cityService.findByIdIn(cityId).stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));
        LocationResponseDto locationResponseDto = convertToResponseDto(location, cityProjectionMap.get(location.getCityId()));
        return locationResponseDto;
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<Location> locationsPage = repository.findAllByIsActive(isActive, pageable);
        List<Location> locationList = locationsPage.getContent();
        Set<Long> cityIds = locationList.stream()
                .map(Location::getCityId).collect(Collectors.toSet());
        Map<Long, CityProjection> cityProjectionMap = cityService.findByIdIn(cityIds)
                .stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));
        List<Object> models = locationList.stream().map(location ->
                        convertToResponseDto(location, cityProjectionMap.get(location.getCityId())))
                .collect(Collectors.toList());
        return PageData.builder()
                .model(models)
                .totalPages(locationsPage.getTotalPages())
                .totalElements(locationsPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public Location update(LocationDto locationDto, Long id) {
        Location location = findByIdUnfiltered(id);
        validate(locationDto, location);
        final Location entity = updateEntity(locationDto, location);
        return super.saveItem(entity);
    }

    @Override
    public Location create(LocationDto locationDto) {
        validate(locationDto, null);
        return super.create(locationDto);
    }

    public Set<LocationProjection> findByIdIn(Set<Long> collect) {
        return repository.findByIdIn(collect);
    }

    @Override
    protected Specification<Location> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Location> customSpecification = new CustomSpecification<>();
        return Specification.where(
                (customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE))
                        .or(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.LOCATION_ADDRESS)));
    }

    @Override
    protected Location updateEntity(LocationDto dto, Location entity) {
        entity.setAddress(dto.getAddress());
        entity.setCode(dto.getCode());
        City city = cityService.findById(dto.getCityId());
        entity.setCity(city);
        return entity;
    }

    @Override
    protected LocationResponseDto convertToResponseDto(Location entity) {
        City city = entity.getCity();
        Country country = entity.getCity().getCountry();
        return LocationResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .address(entity.getAddress())
                .cityId(entity.getCityId())
                .countryId(city.getCountryId())
                .address(entity.getAddress())
                .cityName(city.getName())
                .countryName(country.getName())
                .build();
    }

    @Override
    protected Location convertToEntity(LocationDto locationDto) {
        City city = cityService.findById(locationDto.getCityId());
        return Location.builder()
                .address(locationDto.getAddress())
                .code(locationDto.getCode())
                .city(city)
                .build();
    }


    private LocationResponseDto convertToResponseDto(Location entity, CityProjection cityProjection) {
        LocationResponseDto responseDto = new LocationResponseDto();
        responseDto.setId(entity.getId());
        if (!Objects.isNull(cityProjection)) {
            responseDto.setCityId(cityProjection.getId());
            responseDto.setCityName(cityProjection.getName());
            responseDto.setCountryName(cityProjection.getCountryName());
            responseDto.setCountryId(cityProjection.getCountryId());
        }
        responseDto.setCode(entity.getCode());
        responseDto.setAddress(entity.getAddress());
        return responseDto;

    }

    private void validate(LocationDto dto, Location old) {
        List<Location> locations = repository.findByCode(dto.getCode());
        if (!CollectionUtils.isEmpty(locations)) {
            locations.forEach(location -> {
                if (Objects.nonNull(old) && location.equals(old)) {
                    return;
                }
                    throw EngineeringManagementServerException.badRequest(
                            ErrorId.LOCATION_CODE_EXISTS);
            });
        }
    }

    private List<Object> getDataFromParent(List<Location> locationList) {
        Set<Long> cityIds = locationList.stream()
                .map(Location::getCityId).collect(Collectors.toSet());
        Map<Long, CityProjection> cityProjectionMap = cityService.findByIdIn(cityIds)
                .stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));
        List<Object> models = locationList.stream().map(location ->
                        convertToResponseDto(location, cityProjectionMap.get(location.getCityId())))
                .collect(Collectors.toList());
        return models;
    }
}
