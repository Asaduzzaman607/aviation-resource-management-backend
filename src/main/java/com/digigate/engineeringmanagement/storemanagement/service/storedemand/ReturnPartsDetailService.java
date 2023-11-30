package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.service.AirportService;
import com.digigate.engineeringmanagement.planning.service.impl.PositionServiceImpl;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionRequestDto;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ReturnPartsDetailDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreReturnPartRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreReturnDetailsViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ReturnPartsDetailViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.RpdiViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.WorkOrderComponent;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.ReturnPartsDetailRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class ReturnPartsDetailService extends AbstractSearchService<ReturnPartsDetail,
        ReturnPartsDetailDto, IdQuerySearchDto> {
    private final ReturnPartsDetailRepository returnPartsDetailRepository;
    private final AircraftService aircraftService;
    private final AirportService airportService;
    private final PositionServiceImpl positionIService;
    private final StorePartSerialService storePartSerialService;

    /**
     * Constructors parameterized
     *
     * @param returnPartsDetailRepository {@link ReturnPartsDetailRepository}
     * @param airportService              {@link AirportService}
     * @param positionService             {@link PositionServiceImpl}
     * @param storePartSerialService      {@link StorePartSerialService}
     */
    public ReturnPartsDetailService(ReturnPartsDetailRepository returnPartsDetailRepository,
                                    AircraftService aircraftService,
                                    AirportService airportService,
                                    PositionServiceImpl positionService,
                                    StorePartSerialService storePartSerialService) {
        super(returnPartsDetailRepository);
        this.returnPartsDetailRepository = returnPartsDetailRepository;
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.positionIService = positionService;
        this.storePartSerialService = storePartSerialService;
    }

    public ReturnPartsDetail findByRemovedPartIdAndSerialId(Long removePartId, Long planningSerialId) {
        return returnPartsDetailRepository.findByRemovedPartIdAndSerialId(removePartId, planningSerialId);
    }

    public RpdiViewModel findInspectionByPartSerialId(Long storePartSerialId){
        RpdProjection projection = returnPartsDetailRepository.findByRemovedPartSerialIdOrderByCreatedAtDesc(storePartSerialId)
                .stream().findFirst().orElse(null);

        return populateToRpdiViewModel(projection, storePartSerialId);
    }

    private RpdiViewModel populateToRpdiViewModel(RpdProjection projection, Long storePartSerialId) {
        if(Objects.isNull(projection)){
            RpdiViewModel rpdiViewModel = new RpdiViewModel();
            rpdiViewModel.setPartSerialId(storePartSerialId);
            return rpdiViewModel;
        }

        return RpdiViewModel.builder()
                .returnPartsDetailId(projection.getId())
                .tso(projection.getTso())
                .cso(projection.getCso())
                .tsn(projection.getTsn())
                .csn(projection.getCsn())
                .csr(projection.getCsr())
                .tsr(projection.getTsr())
                .PartSerialId(storePartSerialId)
                .build();
    }

    public boolean existByAircraft(Long id) {
        return returnPartsDetailRepository.existsByRemovedFromAircraftIdAndIsActiveTrue(id);
    }

    public List<StoreReturnPartDetailsProjection> findReturnPartsDetailByIdIn(Set<Long> ids) {
        return returnPartsDetailRepository.findReturnPartsDetailByIdIn(ids);
    }

    /**
     * This method is responsible for get single data by id
     *
     * @param id long type value
     * @return responsive data
     */
    @Override
    public ReturnPartsDetailViewModel getSingle(Long id) {
        return getResponseData(Collections.singletonList(findByIdUnfiltered(id))).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    /**
     * THis method will generate work order Component
     *
     * @param unserviceableId {@link Long}
     * @return {@link WorkOrderComponent}
     */
    public WorkOrderComponent getWorkOrderComponent(Long unserviceableId) {
        Optional<WorkOrderComponent> workOrderComponentOptional =
                returnPartsDetailRepository.findByUnserviceableId(unserviceableId);
        if (workOrderComponentOptional.isPresent()) {
            return workOrderComponentOptional.get();
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
    }

    public List<ReturnPartsDetail> findByStoreReturnPartIdInAndIsActiveTrue(Set<Long> ids) {
        return returnPartsDetailRepository.findByStoreReturnPartIdInAndIsActiveTrue(ids);
    }

    public ReturnPartsDetail create(StoreReturnPartRequestDto returnPartRequestDto) {
        return saveItem(populateEntity(returnPartRequestDto, new ReturnPartsDetail()));
    }

    public ReturnPartsDetail update(StoreReturnPartRequestDto dto) {
        ReturnPartsDetail returnPartsDetail = returnPartsDetailRepository.findByStoreReturnPartId(dto.getId()).orElse(new ReturnPartsDetail());
        return super.saveItem(populateEntity(dto, returnPartsDetail));
    }

    public ReturnPartsDetail saveFromInspection(StoreInspectionRequestDto dto) {
        ReturnPartsDetail entity = new ReturnPartsDetail();
        entity.setCsn(dto.getCsn());
        entity.setCso(dto.getCso());
        entity.setCsr(dto.getCsr());
        entity.setTsn(dto.getTsn());
        entity.setTso(dto.getTso());
        entity.setTsr(dto.getTsr());
        return super.saveItem(entity);
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<ReturnPartsDetail> returnUnserviceablePartSpecification = buildSpecification(searchDto);
        Page<ReturnPartsDetail> pagedData = returnPartsDetailRepository
                .findAll(returnUnserviceablePartSpecification, pageable);
        List<ReturnPartsDetail> content = pagedData.getContent();
        return PageData.builder()
                .model(getResponseData(content))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected ReturnPartsDetailViewModel convertToResponseDto(ReturnPartsDetail entity) {
        Airport airport = entity.getAirport();
        Aircraft aircraft = entity.getRemovedFromAircraft();
        Position position = entity.getPosition();

        ReturnPartsDetailViewModel viewModel = new ReturnPartsDetailViewModel();
        viewModel.setId(entity.getId());
        viewModel.setCsn(entity.getCsn());
        viewModel.setCso(entity.getCso());
        viewModel.setCsr(entity.getCsr());
        viewModel.setTsn(entity.getTsn());
        viewModel.setTso(entity.getTso());
        viewModel.setTsr(entity.getTsr());
        viewModel.setAuthCodeNo(entity.getAuthCodeNo());
        setSerialIds(entity, viewModel);
        if (Objects.nonNull(airport)) {
            viewModel.setAirportId(airport.getId());
            viewModel.setAirportName(airport.getName());
        }
        if (Objects.nonNull(aircraft)) {
            viewModel.setAircraftId(aircraft.getId());
            viewModel.setAircraftName(aircraft.getAircraftName());
        }
        if (Objects.nonNull(position)) {
            viewModel.setPositionId(position.getId());
            viewModel.setPosition(position.getName());
        }
        return viewModel;
    }

    private void setSerialIds(ReturnPartsDetail entity, ReturnPartsDetailViewModel viewModel) {
        List<StoreSerialIdNoDto> serialNoList = returnPartsDetailRepository.findRemovedAndInstalledSerialNo(entity.getId());

        if (CollectionUtils.isEmpty(serialNoList)) {
            return;
        }
        if (entity.getRemovedPartSerialId() == null) {
            viewModel.setInstalledPartSerialNo(serialNoList.get(FIRST_INDEX));
            return;
        } else if (entity.getInstalledPartSerialId() == null) {
            viewModel.setRemovedPartSerialNo(serialNoList.get(FIRST_INDEX));
            return;
        }
        boolean isRemovedSerial = entity.getRemovedPartSerialId() < entity.getInstalledPartSerialId();
        viewModel.setInstalledPartSerialNo(serialNoList.get(!isRemovedSerial ? FIRST_INDEX : ApplicationConstant.SECOND_INDEX));
        viewModel.setRemovedPartSerialNo(serialNoList.get(isRemovedSerial ? FIRST_INDEX : ApplicationConstant.SECOND_INDEX));
    }

    @Override
    protected Specification<ReturnPartsDetail> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<ReturnPartsDetail> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.AUTH_CODE)));
    }

    @Override
    protected ReturnPartsDetail convertToEntity(ReturnPartsDetailDto dto) {
        return null;
    }

    @Override
    protected ReturnPartsDetail updateEntity(ReturnPartsDetailDto dto, ReturnPartsDetail entity) {
        return null;
    }

    private ReturnPartsDetail populateEntity(StoreReturnPartRequestDto returnPartRequestDto, ReturnPartsDetail entity) {
        ReturnPartsDetailDto dto = returnPartRequestDto.getReturnPartsDetailDto();
        entity.setCsn(dto.getCsn());
        entity.setCso(dto.getCso());
        entity.setCsr(dto.getCsr());
        entity.setTsn(dto.getTsn());
        entity.setTso(dto.getTso());
        entity.setTsr(dto.getTsr());
        entity.setIsUsed(dto.getIsUsed());
        entity.setAuthCodeNo(dto.getAuthCodeNo());
        entity.setReasonRemoved(dto.getReasonRemoved());
        entity.setStoreReturnPart(returnPartRequestDto.getStoreReturnPart());
        entity.setRemovalDate(dto.getRemovalDate());
        entity.setAuthNo(dto.getAuthNo());
        entity.setSign(dto.getSign());
        entity.setCreatedDate(dto.getCreatedDate());
        if (Objects.nonNull(dto.getAircraftId())) {
            entity.setRemovedFromAircraft(aircraftService.findByIdUnfiltered(dto.getAircraftId()));
        }
        if (Objects.nonNull(dto.getPositionId())) {
            entity.setPosition(positionIService.findByIdUnfiltered(dto.getPositionId()));
        }
        if (Objects.nonNull(dto.getAirportId())) {
            entity.setAirport(airportService.findById(dto.getAirportId()));
        }
        if (Objects.nonNull(dto.getInstalledPlanningSerialId())) {
            StorePartSerial installedSerial = storePartSerialService.findOrCreateStoreSerial(dto.getInstalledPlanningSerialId(),
                    returnPartRequestDto.getInstalledPartUomId(),
                    returnPartRequestDto.getInstalledPartId(), returnPartRequestDto.getQuantityReturn());
            entity.setInstalledPartSerial(installedSerial);
        }
        if (Objects.nonNull(dto.getRemovedPlanningSerialId())) {
            StorePartSerial removedSerial = storePartSerialService.
                    findOrCreateStoreSerial(dto.getRemovedPlanningSerialId(),
                    returnPartRequestDto.getRemovedPartUomId(),
                    returnPartRequestDto.getPartId(), returnPartRequestDto.getQuantityReturn());
            entity.setRemovedPartSerial(removedSerial);
        }
        entity.setUpdateDate(LocalDate.now());

        entity.setCaabEnabled(returnPartRequestDto.getCaabEnabled());
        entity.setCaabStatus(returnPartRequestDto.getCaabStatus());
        entity.setCaabRemarks(returnPartRequestDto.getCaabRemarks());
        entity.setCaabCheckbox(returnPartRequestDto.getCaabCheckbox());
        entity.setApprovalAuthNo(returnPartRequestDto.getApprovalAuthNo());
        entity.setAuthorizedDate(returnPartRequestDto.getAuthorizedDate());
        entity.setAuthorizesDate(returnPartRequestDto.getAuthorizesDate());
        entity.setCertApprovalRef(returnPartRequestDto.getCertApprovalRef());
        if (Objects.nonNull(returnPartRequestDto.getAuthorizedUserId())) {
            entity.setAuthorizedUser(Employee.withId(returnPartRequestDto.getAuthorizedUserId()));
        }
        if (Objects.nonNull(returnPartRequestDto.getAuthorizesUserId())) {
            entity.setAuthorizesUser(Employee.withId(returnPartRequestDto.getAuthorizesUserId()));
        }
        return entity;
    }

    public List<ReturnPartsDetailViewModel> getResponseData(List<ReturnPartsDetail> returnPartsDetails) {
        Set<Long> collectionsOfAircraftIds = returnPartsDetails.stream()
                .map(ReturnPartsDetail::getRemovedFromAircraftId).collect(Collectors.toSet());
        Map<Long, AircraftProjection> aircraftProjectionMap = aircraftService.findByIdIn(collectionsOfAircraftIds)
                .stream().collect(Collectors.toMap(AircraftProjection::getId, Function.identity()));

        Set<Long> collectionsOfSerialIds = returnPartsDetails.stream()
                .map(ReturnPartsDetail::getInstalledPartSerialId).collect(Collectors.toSet());
        Map<Long, StoreReturnPartDetailsProjection> storeReturnPartDetailsProjectionMap = findReturnPartsDetailByIdIn(collectionsOfSerialIds)
                .stream().collect(Collectors.toMap(StoreReturnPartDetailsProjection::getId, Function.identity()));

        Set<Long> collectionsOfAirportIds = returnPartsDetails.stream().map(ReturnPartsDetail::getAirportId)
                .collect(Collectors.toSet());
        Map<Long, AirportProjection> airportProjectionMap = airportService.findByIdIn(collectionsOfAirportIds)
                .stream().collect(Collectors.toMap(AirportProjection::getId, Function.identity()));

        Set<Long> collectionsOfPositionIds = returnPartsDetails.stream().map(ReturnPartsDetail::getPositionId)
                .collect(Collectors.toSet());
        Map<Long, PositionProjection> positionProjectionMap = positionIService.findByIdIn(collectionsOfPositionIds)
                .stream().collect(Collectors.toMap(PositionProjection::getId, Function.identity()));

        return returnPartsDetails.stream().map(returnPartsDetail ->
                        convertToViewModel(returnPartsDetail,
                                aircraftProjectionMap.get(returnPartsDetail.getRemovedFromAircraftId()),
                                positionProjectionMap.get(returnPartsDetail.getPositionId()),
                                airportProjectionMap.get(returnPartsDetail.getAirportId()),
                                storeReturnPartDetailsProjectionMap.get(returnPartsDetail.getId())
                        ))
                .collect(Collectors.toList());
    }

    private ReturnPartsDetailViewModel convertToViewModel(ReturnPartsDetail entity,
                                                          AircraftProjection aircraftProjection,
                                                          PositionProjection positionProjection,
                                                          AirportProjection airportProjection,
                                                          StoreReturnPartDetailsProjection storeReturnPartDetailsProjection) {
        ReturnPartsDetailViewModel viewModel = new ReturnPartsDetailViewModel();
        viewModel.setId(entity.getId());
        viewModel.setTsr(entity.getTsr());
        viewModel.setTsn(entity.getTsn());
        viewModel.setTso(entity.getTso());
        viewModel.setCsr(entity.getCsr());
        viewModel.setCsn(entity.getCsn());
        viewModel.setCso(entity.getCso());
        viewModel.setIsUsed(entity.getIsUsed());
        viewModel.setRemovalDate(entity.getRemovalDate());
        viewModel.setAuthCodeNo(entity.getAuthCodeNo());
        viewModel.setReasonRemoved(entity.getReasonRemoved());
        setSerialIds(entity, viewModel);
        if (Objects.nonNull(aircraftProjection)) {
            viewModel.setAircraftId(aircraftProjection.getId());
            viewModel.setAircraftName(aircraftProjection.getAircraftName());
        }
        if (Objects.nonNull(airportProjection)) {
            viewModel.setAirportId(airportProjection.getId());
            viewModel.setAirportName(airportProjection.getName());
        }
        if (Objects.nonNull(positionProjection)) {
            viewModel.setPositionId(positionProjection.getId());
            viewModel.setPosition(positionProjection.getName());
        }
        if (Objects.nonNull(storeReturnPartDetailsProjection)) {
            viewModel.setPartName(storeReturnPartDetailsProjection.getInstalledPartSerialStorePartAvailabilityPartDescription());
        }
        viewModel.setCaabEnabled(entity.getCaabEnabled());
        viewModel.setCaabStatus(entity.getCaabStatus());
        viewModel.setCaabRemarks(entity.getCaabStatus());
        viewModel.setCaabCheckbox(entity.getCaabCheckbox());
        viewModel.setApprovalAuthNo(entity.getApprovalAuthNo());
        viewModel.setAuthorizedDate(entity.getAuthorizedDate());
        viewModel.setAuthorizesDate(entity.getAuthorizesDate());
        viewModel.setCertApprovalRef(entity.getCertApprovalRef());
        viewModel.setAuthNo(entity.getAuthNo());
        viewModel.setSign(entity.getSign());
        viewModel.setCreatedDate(entity.getCreatedDate());
        if (Objects.nonNull(entity.getAuthorizedUser())) {
            viewModel.setAuthorizedUserId(entity.getAuthorizedUser().getId());
            viewModel.setAuthorizedUserName((entity.getAuthorizedUser().getName()));
        }
        if (Objects.nonNull(entity.getAuthorizesUser())) {
            viewModel.setAuthorizesUserId(entity.getAuthorizesUser().getId());
            viewModel.setAuthorizesUserName((entity.getAuthorizesUser().getName()));
        }
        return viewModel;
    }

    public List<ReturnPartsDetail> saveAll(List<StoreReturnPartRequestDto> storeReturnPartRequestDtoList) {
        List<ReturnPartsDetail> returnPartsDetailList = storeReturnPartRequestDtoList.stream()
                .map(storeReturnPartRequestDto -> populateEntity(storeReturnPartRequestDto, new ReturnPartsDetail()))
            .collect(Collectors.toList());
        return super.saveItemList(returnPartsDetailList);
    }

    public StoreReturnDetailsViewModel findReturnById(Long id) {
        return StoreReturnDetailsViewModel.from(returnPartsDetailRepository.findReturnPartsDetailById(id));
    }

    public List<StoreReturnPartDetailsProjection> findReturnPartsDetailByRemovedPartSerialIdIn(Set<Long> ids) {
        return returnPartsDetailRepository.findReturnPartsDetailByRemovedPartSerialIdIn(ids);
    }
}
