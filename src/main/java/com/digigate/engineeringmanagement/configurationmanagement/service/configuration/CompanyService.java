package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.CompanyDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.CompanyViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Company;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.CompanyRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class CompanyService extends AbstractSearchService<Company, CompanyDto, IdQuerySearchDto> {

    private final CompanyRepository companyRepository;
    private final CityService cityService;
    private final CurrencyService currencyService;

    /**
     * Constructor parameterized
     *
     * @param companyRepository   {@link CompanyRepository}
     * @param cityService         {@link CityService}
     */
    public CompanyService(CompanyRepository companyRepository,
                          CityService cityService, CurrencyService currencyService) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.cityService = cityService;
        this.currencyService = currencyService;
    }

    /**
     * Check Dependency for Making Active or Inactive
     */
    public boolean existsByCityAndIsActiveTrue(Long cityId) {
        return companyRepository.existsByCityIdAndIsActiveTrue(cityId);
    }

    @Override
    public Company create(CompanyDto companyDto) {
        if (companyRepository.existsByCompanyName(companyDto.getCompanyName())) {
            LOGGER.error("Company name already exist with name: {}", companyDto.getCompanyName());
            throw EngineeringManagementServerException.badRequest(ErrorId.COMPANY_NAME_ALREADY_EXIST);
        }

        return super.create(companyDto);
    }

    /**
     * This method is responsible for activation/inactivation company
     *
     * @param id       {@link Company}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        super.updateActiveStatus(id, isActive);
    }

    /**
     * This method is responsible for converting company to view model
     *
     * @param company {@link Company}
     * @return at this phase returning company full info {@link Company}
     */
    @Override
    protected CompanyViewModel convertToResponseDto(Company company) {
        CityProjection cityProjection = cityService.findCityById(company.getCityId());
        CompanyViewModel companyViewModel = new CompanyViewModel();
        Currency baseCurrency = Objects.nonNull(company.getBaseCurrencyId()) ? currencyService.findById(company.getBaseCurrencyId()) : null;
        Currency localCurrency = Objects.nonNull(company.getBaseCurrencyId()) ? currencyService.findById(company.getLocalCurrencyId()): null;
        companyViewModel.setId(company.getId());
        companyViewModel.setCompanyName(company.getCompanyName());
        companyViewModel.setAddressLineOne(company.getAddressLineOne());
        companyViewModel.setAddressLineTwo(company.getAddressLineTwo());
        companyViewModel.setAddressLineThree(company.getAddressLineThree());
        companyViewModel.setPhone(company.getPhone());
        companyViewModel.setFax(company.getFax());
        companyViewModel.setEmail(company.getEmail());
        companyViewModel.setContactPerson(company.getContactPerson());
        companyViewModel.setShipmentAddressOne(company.getShipmentAddressOne());
        companyViewModel.setShipmentAddressTwo(company.getShipmentAddressTwo());
        companyViewModel.setShipmentAddressThree(company.getShipmentAddressThree());
        companyViewModel.setCompanyUrl(company.getCompanyUrl());
        companyViewModel.setCompanyLogo(company.getCompanyLogo());
        if (Objects.nonNull(baseCurrency)) {
            companyViewModel.setBaseCurrencyId(baseCurrency.getId());
            companyViewModel.setBaseCurrency(baseCurrency.getCode());
        }
        if (Objects.nonNull(localCurrency)) {
            companyViewModel.setLocalCurrencyId(localCurrency.getId());
            companyViewModel.setLocalCurrency(localCurrency.getCode());
        }
        if (Objects.nonNull(cityProjection)) {
            companyViewModel.setCityId(cityProjection.getId());
            companyViewModel.setCityName(cityProjection.getName());
            companyViewModel.setCountryId(cityProjection.getCountryId());
            companyViewModel.setCountryName(cityProjection.getCountryName());
        }
        return companyViewModel;
    }

    /**
     * This method is responsible for converting dto to entity
     *
     * @param companyDto {@link CompanyDto}
     * @return Company entity {@link Company}
     */
    @Override
    protected Company convertToEntity(CompanyDto companyDto) {
        return populateEntity(companyDto, new Company());
    }

    /**
     * This method is responsible for updating entity to dto
     *
     * @param companyDto {@link CompanyDto}
     * @param companies  {@link Company}
     * @return responding company entity
     */
    @Override
    protected Company updateEntity(CompanyDto companyDto, Company companies) {
        return populateEntity(companyDto, companies);
    }

    @Override
    protected Specification<Company> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Company> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), COMPANY_NAME));
    }

    /**
     * This method is responsible for setting data dto to entity
     *
     * @param companyDto {@link CompanyDto}
     * @param company    {@link Company}
     */
    private Company populateEntity(CompanyDto companyDto, Company company) {

        /*This validation always would be first! Please be aware of validate function!*/
        validate(companyDto, company);

        if (Objects.nonNull(companyDto.getCityId())) {
            company.setCity(cityService
                    .findByIdUnfiltered(companyDto.getCityId()));
        }
        company.setCompanyName(companyDto.getCompanyName());
        company.setAddressLineOne(companyDto.getAddressLineOne());
        company.setAddressLineTwo(companyDto.getAddressLineTwo());
        company.setAddressLineThree(companyDto.getAddressLineThree());
        company.setPhone(companyDto.getPhone());
        company.setFax(companyDto.getFax());
        company.setEmail(companyDto.getEmail());
        company.setContactPerson(companyDto.getContactPerson());
        if (Objects.nonNull(companyDto.getBaseCurrencyId())) {
            company.setBaseCurrency(currencyService.findById(companyDto.getBaseCurrencyId()));
        }
        if (Objects.nonNull(companyDto.getLocalCurrencyId())) {
            company.setLocalCurrency(currencyService.findById(companyDto.getLocalCurrencyId()));
        }
        company.setShipmentAddressOne(companyDto.getShipmentAddressOne());
        company.setShipmentAddressTwo(companyDto.getShipmentAddressTwo());
        company.setShipmentAddressThree(companyDto.getShipmentAddressThree());
        company.setCompanyUrl(companyDto.getCompanyUrl());
        company.setCompanyLogo(companyDto.getCompanyLogo());

        return company;
    }

    /**
     * This method is responsible for validating unique company name
     *
     * @param companyDto {@link CompanyDto}
     * @param old        {@link Company}
     */
    private void validate(CompanyDto companyDto, Company old) {
        List<Company> companyList =
                companyRepository.findByCompanyName(companyDto.getCompanyName());
        if (CollectionUtils.isNotEmpty(companyList) && (
                Objects.isNull(old) ||
                        companyList.size() > VALUE_ONE ||
                        !companyList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.COMPANY_NAME_ALREADY_EXIST);
        }
    }
}
