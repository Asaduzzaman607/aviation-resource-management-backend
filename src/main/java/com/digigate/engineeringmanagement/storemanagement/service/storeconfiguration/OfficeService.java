package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Location;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Office;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.LocationProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.OfficeDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.OfficeRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
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

import static java.lang.Boolean.FALSE;

@Service
public class OfficeService extends AbstractSearchService<Office, OfficeDto, IdQuerySearchDto> {
    private final LocationService locationService;
    private final OfficeRepository officeRepository;
    private final RoomService roomService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    public OfficeService(LocationService locationService,
                         OfficeRepository officeRepository,
                         @Lazy RoomService roomService,
                         @Lazy StorePartAvailabilityService storePartAvailabilityService) {
        super(officeRepository);
        this.locationService = locationService;
        this.officeRepository = officeRepository;
        this.roomService = roomService;
        this.storePartAvailabilityService = storePartAvailabilityService;
    }

    /**
     * Custom save office
     *
     * @param officeDto data transfer object
     * @return store save response
     */
    @Override
    public Office create(OfficeDto officeDto) {
        validate(officeDto, null);
        return super.create(officeDto);
    }

    /**
     * Custom update
     *
     * @param officeDto data transfer object
     * @param id        store id
     * @return store update response
     */
    @Override
    public Office update(OfficeDto officeDto, Long id) {
        Office office = findByIdUnfiltered(id);
        validate(officeDto, office);
        final Office entity = updateEntity(officeDto, office);
        return super.saveItem(entity);
    }

    public boolean existsByLocationsIdAndIsActiveTrue(Long locationId) {
        return officeRepository.existsByLocationsIdAndIsActiveTrue(locationId);
    }

    public Set<OfficeProjection> findByIdIn(Set<Long> officeIds) {
        return officeRepository.findByIdIn(officeIds);
    }

    public List<OfficeProjection> findByIdIn(List<Long> officeIds) {
        return officeRepository.findByIdIn(officeIds);
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<Office> pageData = officeRepository.findAllByIsActive(isActive, pageable);
        return getResponseData(pageData, pageable);
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Office> officeSpecification = buildSpecification(searchDto);
        Page<Office> pageData = officeRepository.findAll(officeSpecification, pageable);
        return getResponseData(pageData, pageable);
    }

    /**
     * Custom update status with dependency
     *
     * @param id for update status
     * @param isActive boolean
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE &&
                (roomService.existByStore(id)
                ||storePartAvailabilityService.existsByOfficeIdAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
        }
        super.updateActiveStatus(id, isActive);
    }

    private OfficeDto convertToResponseDto(Office office, LocationProjection locationProjection) {
        return OfficeDto.builder()
                .id(office.getId())
                .code(office.getCode())
                .address(office.getAddress())
                .locationId(locationProjection.getId())
                .locationCode(locationProjection.getCode())
                .build();
    }

    private PageData getResponseData(Page<Office> pageData, Pageable pageable) {
        List<Office> offices = pageData.getContent();

        Set<Long> officeIds = offices.stream().map(Office::getLocationId).collect(Collectors.toSet());

        Map<Long, LocationProjection> locationProjectionMap = locationService.findByIdIn(officeIds)
                .stream()
                .collect(Collectors.toMap(LocationProjection::getId, Function.identity()));

        List<Object> models = offices.stream()
                .map(office ->
                        convertToResponseDto(office, locationProjectionMap.get(office.getLocationId())))
                .collect(Collectors.toList());

        return PageData.builder()
                .model(models)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private void validate(OfficeDto dto, Office old) {
        List<Office> offices = officeRepository.findByCodeIgnoreCase(dto.getCode());
        if (!CollectionUtils.isEmpty(offices)) {
            offices.forEach(store -> {
                if (Objects.nonNull(old) && store.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.CODE_ALREADY_EXIST);
            });
        }
    }

    @Override
    protected OfficeDto convertToResponseDto(Office office) {
        Location location = office.getLocations();
        OfficeDto officeDto = new OfficeDto();

        if (Objects.nonNull(location)) {
            officeDto.setLocationId(location.getId());
            officeDto.setLocationCode(location.getCode());
        }
        officeDto.setId(office.getId());
        officeDto.setCode(office.getCode());
        officeDto.setAddress(office.getAddress());
        return officeDto;
    }

    @Override
    protected Office convertToEntity(OfficeDto officeDto) {
        Location location = locationService.findById(officeDto.getLocationId());
        Office office = new Office();
        office.setAddress(officeDto.getAddress());
        office.setCode(officeDto.getCode());
        office.setLocations(location);
        return office;
    }

    @Override
    protected Office updateEntity(OfficeDto dto, Office entity) {
        entity.setCode(dto.getCode());
        entity.setAddress(dto.getAddress());
        entity.setLocations(locationService.findById(dto.getLocationId()));
        return entity;
    }

    @Override
    protected Specification<Office> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Office> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getId(), ApplicationConstant.LOCATION_ID))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE)));

    }
}
