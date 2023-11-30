package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CountryProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CountryDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.CountryResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.CountryRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

@Service
public class CountryService extends AbstractSearchService<Country, CountryDto, IdQuerySearchDto> {

    private final CountryRepository countryRepository;
    private final CityService cityService;
    private final CompanyService companyService;

    public CountryService(CountryRepository countryRepository, CityService cityService,
                          @Lazy CompanyService companyService) {
        super(countryRepository);
        this.countryRepository = countryRepository;
        this.cityService = cityService;
        this.companyService = companyService;
    }

    @Override
    public CountryResponseDto getSingle(Long id) {
        Country country = findByIdUnfiltered(id);
        CountryResponseDto countryResponseDto = convertToResponseDto(country);
        Set<IdNameResponse> IdNameResponses = country.getCities().stream().map(
                city -> IdNameResponse.of(city.getId(), city.getName())
        ).collect(Collectors.toSet());
        countryResponseDto.setCities(IdNameResponses);
        return countryResponseDto;
    }

    @Override
    public Country create(CountryDto dto) {
        validate(dto, null);
        return super.create(dto);
    }

    @Override
    public Country update(CountryDto countryDto, Long id) {
        Country country = findByIdUnfiltered(id);
        validate(countryDto, country);
        final Country entity = updateEntity(countryDto, country);
        return super.saveItem(entity);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && (cityService.existsByCountryIdAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        super.updateActiveStatus(id, isActive);
    }

    public Set<CountryProjection> findByIdIn(Set<Long> collect) {
        return countryRepository.findByIdIn(collect);
    }

    @Override
    protected CountryResponseDto convertToResponseDto(Country country) {
        return CountryResponseDto.builder().id(country.getId())
                .name(country.getName())
                .code(country.getCode())
                .dialingCode(country.getDialingCode())
                .build();
    }

    @Override
    protected Country convertToEntity(CountryDto countryDto) {
        validate(countryDto, null);
        return Country.builder()
                .code(countryDto.getCode())
                .name(countryDto.getName())
                .dialingCode(countryDto.getDialingCode())
                .build();
    }

    @Override
    protected Country updateEntity(CountryDto dto, Country entity) {
        entity.setCode(dto.getCode());
        entity.setDialingCode(dto.getDialingCode());
        entity.setName(dto.getName());
        return entity;
    }


    private void validate(CountryDto dto, Country old) {
        List<Country> countries = countryRepository.findByNameIgnoreCaseOrCodeIgnoreCaseOrDialingCode(
                dto.getName(), dto.getCode(), dto.getDialingCode());
        if (!CollectionUtils.isEmpty(countries)) {
            countries.forEach(country -> {
                if (Objects.nonNull(old) && country.equals(old)) {
                    return;
                }
                if (country.getDialingCode().equals(dto.getDialingCode())) {
                    throw EngineeringManagementServerException.badRequest(
                            ErrorId.DIALING_CODE_EXISTS);
                }
                if (country.getCode().equalsIgnoreCase(dto.getCode())) {
                    throw EngineeringManagementServerException.badRequest(
                            ErrorId.COUNTRY_CODE_EXISTS);
                }
                if (country.getName().equalsIgnoreCase(dto.getName())) {
                    throw EngineeringManagementServerException.badRequest(
                            ErrorId.COUNTRY_NAME_EXISTS);
                }
            });
        }
    }

    @Override
    protected Specification<Country> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Country> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.COUNTRY_NAME)
                        .or(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE)));
    }
}
