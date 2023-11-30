package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.ExternalDepartmentDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ExternalDepartmentResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.ExternalDepartmentRepository;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.ExternalDepartmentProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExternalDepartmentService extends AbstractSearchService<Vendor, ExternalDepartmentDto, IdQuerySearchDto> {
    private final CityService cityService;
    private final ExternalDepartmentRepository repository;
    private final CountryService countryService;
    private final GenericAttachmentService genericAttachmentService;

    public ExternalDepartmentService(ExternalDepartmentRepository repository, CityService cityService, CountryService countryService, GenericAttachmentService genericAttachmentService) {
        super(repository);
        this.repository = repository;
        this.cityService = cityService;
        this.countryService = countryService;
        this.genericAttachmentService = genericAttachmentService;
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<Vendor> vendorDepartmentPage = repository.findAllByIsActive(isActive, pageable);
        return getResponseData(vendorDepartmentPage, pageable);
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Vendor> vendorSpecification = buildSpecification(searchDto);
        Page<Vendor> pagedData = repository.findAll(vendorSpecification, pageable);
        return getResponseData(pagedData, pageable);
    }

    public List<ExternalDepartmentProjection> findByIdIn(Set<Long> collect) {
        return repository.findExternalDepartmentByIdIn(collect);
    }

    @Override
    protected ExternalDepartmentResponseDto convertToResponseDto(Vendor entity) {
        City city = entity.getCity();
        Country country = city.getCountry();
        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.VENDOR, entity.getId())
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));
        Set<String> attachment = attachmentLinksMap.get(entity.getId());
        return ExternalDepartmentResponseDto.builder()
                .id(entity.getId())
                .cityId(entity.getCityId())
                .cityName(city.getName())
                .countryId(city.getCountryId())
                .countryOriginId(entity.getCountryOriginId())
                .attachments(attachment)
                .countryName(country.getName())
                .address(entity.getAddress())
                .name(entity.getName())
                .contactPerson(entity.getContactPerson())
                .officePhone(entity.getOfficePhone())
                .website(entity.getWebsite())
                .skype(entity.getSkype())
                .contactSkype(entity.getContactSkype())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .itemsBuild(entity.getItemsBuild())
                .loadingPort(entity.getLoadingPort())
                .validTill(entity.getValidTill())
                .emergencyContact(entity.getEmergencyContact())
                .vendorType(entity.getVendorType())
                .build();
    }

    @Override
    public  Vendor create(ExternalDepartmentDto dto) {
        validateClientData(dto, null);
        Vendor entity = convertToEntity(dto);
        Vendor vendor = saveItem(entity);
        if (!CollectionUtils.isEmpty(dto.getAttachments())) {
            genericAttachmentService.saveAllAttachments(dto.getAttachments(), FeatureName.VENDOR, vendor.getId());
        }
        return vendor;
    }

    @Override
    protected Vendor convertToEntity(ExternalDepartmentDto dto) {
        Vendor vendor = saveEntity(dto, new Vendor());
        return vendor;
    }

    @Override
    public Vendor update(ExternalDepartmentDto dto, Long id) {
        validateClientData(dto, id);
        final Vendor entity = updateEntity(dto, findByIdUnfiltered(id));
        Vendor vendor = saveItem(entity);
        genericAttachmentService.updateByRecordId(FeatureName.VENDOR, vendor.getId(), dto.getAttachments());
        return vendor;
    }

    @Override
    protected Vendor updateEntity(ExternalDepartmentDto dto, Vendor vendor) {
        Vendor vendorUpdate = saveEntity(dto, vendor);
        return vendorUpdate;
    }

    @Override
    protected Specification<Vendor> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Vendor> customSpecification = new CustomSpecification<>();
        return Specification.where(
                        customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.VENDOR_NAME)
                ).and(customSpecification.equalSpecificationAtRoot(VendorType.OPERATOR, ApplicationConstant.VENDOR_TYPE_OPERATOR))
                .and(new CustomSpecification<Vendor>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
    }

    private PageData getResponseData(Page<Vendor> pagedData, Pageable pageable) {
        List<Vendor> vendorList = pagedData.getContent();
        Set<Long> vendorIds = vendorList.stream()
                .map(Vendor::getId).collect(Collectors.toSet());
        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.VENDOR, vendorIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));
        Set<Long> cityIds = vendorList.stream()
                .map(Vendor::getCityId).collect(Collectors.toSet());
        Map<Long, CityProjection> cityProjectionMap = cityService.findByIdIn(cityIds).stream()
                .collect(Collectors.toMap(CityProjection::getId, Function.identity()));

        List<Object> models = vendorList.stream().map(vendor -> {
                    CityProjection city = cityProjectionMap.get(vendor.getCityId());
                    Set<String> attachmentLinks = attachmentLinksMap.get(vendor.getId());
                    ExternalDepartmentResponseDto externalDepartmentResponseDto = ExternalDepartmentResponseDto.builder()
                            .id(vendor.getId())
                            .cityId(vendor.getCityId())
                            .cityName(city.getName())
                            .countryId(city.getCountryId())
                            .countryName(city.getCountryName())
                            .countryOriginId(vendor.getCountryOriginId())
                            .address(vendor.getAddress())
                            .name(vendor.getName())
                            .attachments(attachmentLinks)
                            .contactPerson(vendor.getContactPerson())
                            .officePhone(vendor.getOfficePhone())
                            .skype(vendor.getSkype())
                            .website(vendor.getWebsite())
                            .contactSkype(vendor.getContactSkype())
                            .itemsBuild(vendor.getItemsBuild())
                            .vendorType(vendor.getVendorType())
                            .emergencyContact(vendor.getEmergencyContact())
                            .validTill(vendor.getValidTill())
                            .loadingPort(vendor.getLoadingPort())
                            .status(vendor.getStatus())
                            .address(vendor.getAddress())
                            .officePhone(vendor.getOfficePhone())
                            .build();

                    return externalDepartmentResponseDto;
                })
                .collect(Collectors.toList());

        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private Vendor saveEntity(ExternalDepartmentDto dto, Vendor vendor) {

        City city = cityService.findById(dto.getCityId());
        vendor.setVendorType(VendorType.OPERATOR);
        vendor.setName(dto.getName());
        vendor.setAddress(dto.getAddress());
        vendor.setOfficePhone(dto.getOfficePhone());
        vendor.setEmail(dto.getEmail());
        vendor.setWebsite(dto.getWebsite());
        vendor.setSkype(dto.getSkype());
        vendor.setItemsBuild(dto.getItemsBuild());
        vendor.setLoadingPort(dto.getLoadingPort());
        vendor.setValidTill(dto.getValidTill());
        vendor.setUpdateDate(LocalDate.now());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setContactSkype(dto.getContactSkype());
        vendor.setEmergencyContact(dto.getEmergencyContact());
        vendor.setSubmittedById(User.withId(Helper.getAuthUserId()));
        if (!dto.getCityId().equals(vendor.getCityId())) {
            vendor.setCity(city);
        }
        if (Objects.nonNull(dto.getCountryOriginId())) {
            vendor.setCountryOrigin(countryService.findById(dto.getCountryOriginId()));
        }
        vendor.setWebsite(dto.getWebsite());
        return vendor;
    }
}
