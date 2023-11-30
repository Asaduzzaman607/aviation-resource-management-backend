package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CityDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.CityResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.CityRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.LocationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Boolean.FALSE;

@Service
public class CityService extends AbstractSearchService<City, CityDto, IdQuerySearchDto> {
    private final CountryService countryService;
    private final CityRepository repository;
    private final LocationService locationService;
    private final CompanyService companyService;
    private final ShipmentProviderService shipmentProviderService;

    public CityService(CityRepository repository,
                       @Lazy CountryService countryService,
                       @Lazy LocationService locationService,
                       @Lazy CompanyService companyService,
                       @Lazy ShipmentProviderService shipmentProviderService) {
        super(repository);
        this.repository = repository;
        this.countryService = countryService;
        this.locationService = locationService;
        this.companyService = companyService;
        this.shipmentProviderService = shipmentProviderService;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && (locationService.existsByCityIdAndIsActiveTrue(id)
                || companyService.existsByCityAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        super.updateActiveStatus(id, isActive);
    }

    public boolean existsByCountryIdAndIsActiveTrue(Long cityId) {
        return repository.existsByCountryIdAndIsActiveTrue(cityId);
    }

    public Set<CityProjection> findByIdIn(Set<Long> collect) {
        return repository.findByIdIn(collect);
    }

    public CityProjection findCityById(Long id) {
        return repository.findCitiesById(id);
    }

    @Override
    protected CityResponseDto convertToResponseDto(City city) {
        Country country = city.getCountry();
        return CityResponseDto.builder()
                .id(city.getId())
                .name(city.getName())
                .zipCode(city.getZipCode())
                .countryId(country.getId())
                .countryName(country.getName())
                .dialingCode(country.getDialingCode())
                .build();
    }

    @Override
    protected City convertToEntity(CityDto cityDto) {
        return populateEntity(cityDto, new City());
    }

    @Override
    protected City updateEntity(CityDto dto, City entity) {
        return populateEntity(dto, entity);
    }

    @Override
    protected Specification<City> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<City> customSpecification = new CustomSpecification<>();

        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getId(), ApplicationConstant.COUNTRY_ID)
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.CITY_NAME)));
    }

    private City populateEntity(CityDto cityDto, City city) {
        validate(cityDto, city);
        Country country = countryService.findById(cityDto.getCountryId());
        city.setName(cityDto.getName());
        city.setZipCode(cityDto.getZipCode());
        city.setCountry(country);
        return city;
    }

    private void validate(CityDto dto, City old) {
        List<City> cities = repository.findByCountryIdAndNameAndIsActiveTrue(dto.getCountryId(), dto.getName());
        if (!CollectionUtils.isEmpty(cities)) {
            cities.forEach(city -> {
                if (Objects.nonNull(old) && city.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.CITY_ALREADY_EXITS);
            });
        }
    }
}
