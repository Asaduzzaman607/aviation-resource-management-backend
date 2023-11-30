package com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.AircraftSearchViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.AircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ApuAvailableAircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.AircraftInfoViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftCheckDoneRepository;
import com.digigate.engineeringmanagement.planning.repository.AmlFlightDataRepository;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AircraftProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class AircraftService
        extends AbstractSearchService<Aircraft, AircraftDto, IdQuerySearchDto> implements AircraftIService {

    private final AircraftRepository aircraftRepository;

    private final AmlFlightDataRepository amlFlightDataRepository;
    private final AircraftCheckDoneRepository aircraftCheckDoneRepository;
    private final AircraftModelService aircraftModelService;
    private final StoreDemandService itemDemandDetailsService;
    private final ReturnPartsDetailService returnPartsDetailService;

    /**
     * Constructor parameterized
     *
     * @param aircraftRepository          {@link AircraftRepository}
     * @param amlFlightDataRepository     {@link AmlFlightDataRepository}
     * @param aircraftCheckDoneRepository {@link AircraftCheckDoneRepository}
     * @param aircraftModelService        {@link AircraftModelService}
     * @param itemDemandDetailsService    {@link StoreDemandService}
     * @param returnPartsDetailService    {@link ReturnPartsDetailService}
     */
    public AircraftService(AircraftRepository aircraftRepository, AmlFlightDataRepository amlFlightDataRepository,
                           AircraftCheckDoneRepository aircraftCheckDoneRepository, AircraftModelService aircraftModelService,
                           @Lazy StoreDemandService itemDemandDetailsService,
                           @Lazy ReturnPartsDetailService returnPartsDetailService) {
        super(aircraftRepository);
        this.aircraftRepository = aircraftRepository;
        this.amlFlightDataRepository = amlFlightDataRepository;
        this.aircraftCheckDoneRepository = aircraftCheckDoneRepository;
        this.aircraftModelService = aircraftModelService;
        this.itemDemandDetailsService = itemDemandDetailsService;
        this.returnPartsDetailService = returnPartsDetailService;
    }

    /**
     * This method is responsible for checking existence of parent
     *
     * @param aircraftModelId {@link AircraftModel}
     * @return responding primitive boolean
     */
    public boolean isPossibleInActiveForAircraftModel(Long aircraftModelId) {
        return aircraftRepository.existsByAircraftModelIdAndIsActiveTrue(aircraftModelId);
    }

    public List<AircraftProjection> findByIdIn(Set<Long> collect) {
        return aircraftRepository.findAircraftByIdIn(collect);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
//        if (isActive == FALSE && (itemDemandDetailsService.existByAircraft(id) ||
//                returnPartsDetailService.existByAircraft(id))) {
//            throw EngineeringManagementServerException
//                    .badRequest(ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY);
//        }
        super.updateActiveStatus(id, isActive);
    }

    /**
     * This method is responsible for converting entity to dto
     *
     * @param aircraft {@link Aircraft}
     * @return responding aircraft view model {@link AircraftViewModel}
     */
    @Override
    protected AircraftViewModel convertToResponseDto(Aircraft aircraft) {

        AircraftViewModel aircraftViewModel = new AircraftViewModel();

        aircraftViewModel.setId(aircraft.getId());
        aircraftViewModel.setAircraftName(aircraft.getAircraftName());
        aircraftViewModel.setManufactureDate(aircraft.getManufactureDate());
        aircraftViewModel.setAirframeSerial(aircraft.getAirframeSerial());
        aircraftViewModel.setAirFrameTotalTime(aircraft.getAirFrameTotalTime());
        aircraftViewModel.setAirframeTotalCycle(aircraft.getAirframeTotalCycle());
        aircraftViewModel.setBdTotalTime(aircraft.getBdTotalTime());
        aircraftViewModel.setBdTotalCycle(aircraft.getBdTotalCycle());
        aircraftViewModel.setDailyAverageHours((aircraft.getDailyAverageHours()));
        aircraftViewModel.setDailyAverageCycle((aircraft.getDailyAverageCycle()));

        aircraftViewModel.setDailyAverageApuCycle(aircraft.getDailyAverageApuCycle());
        aircraftViewModel.setDailyAverageApuHours(aircraft.getDailyAverageApuHours());
        aircraftViewModel.setTotalApuCycle(aircraft.getTotalApuCycle());
        aircraftViewModel.setTotalApuHours(aircraft.getTotalApuHours());

        aircraftViewModel.setEngineType(aircraft.getEngineType());
        aircraftViewModel.setPropellerType(aircraft.getPropellerType());

        if (Objects.nonNull(aircraft.getAircraftModel())) {
            aircraftViewModel.setAircraftModelId(aircraft.getAircraftModel().getId());
            aircraftViewModel.setAircraftModelName(aircraft.getAircraftModel().getAircraftModelName());
        }

        aircraftViewModel.setAircraftCheckDoneHour(aircraft.getAircraftCheckDoneHour());
        aircraftViewModel.setAircraftCheckDoneDate(aircraft.getAircraftCheckDoneDate());
        aircraftViewModel.setInductionDate(aircraft.getInductionDate());

        return aircraftViewModel;
    }

    /**
     * This method is responsible for converting dto to entity
     *
     * @param aircraftDto {@link AircraftDto}
     * @return responding aircraft {@link Aircraft}
     */
    @Override
    protected Aircraft convertToEntity(AircraftDto aircraftDto) {
        return populateEntity(aircraftDto, new Aircraft());
    }

    /**
     * This method is responsible to convert dto to entity for updating
     *
     * @param aircraftDto {@link AircraftDto}
     * @param aircraft    {@link Aircraft}
     * @return responding aircraft {@link Aircraft}
     */
    @Override
    protected Aircraft updateEntity(AircraftDto aircraftDto, Aircraft aircraft) {
        return populateEntity(aircraftDto, aircraft);
    }

    /**
     * This method is responsible for searching aircraft by name
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @return responding aircraft specification
     */
    @Override
    protected Specification<Aircraft> buildSpecification(IdQuerySearchDto searchDto) {
        return new CustomSpecification<Aircraft>()
                .likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.AIRCRAFT_NAME);
    }

    @Override
    public Boolean validateClientData(AircraftDto aircraftDto, Long id) {


        if (Objects.equals(aircraftDto.getDailyAverageHours(), ApplicationConstant.DOUBLE_VALUE_ZERO) ||
                Objects.equals(aircraftDto.getDailyAverageCycle(),ApplicationConstant.VALUE_ZERO)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_AIRCRAFT_AVERAGE_HOUR_CYCLE);
        }

        if (Objects.equals(aircraftDto.getDailyAverageApuHours(), ApplicationConstant.DOUBLE_VALUE_ZERO) ||
                Objects.equals(aircraftDto.getDailyAverageApuCycle(),ApplicationConstant.VALUE_ZERO)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_AIRCRAFT_AVERAGE_APU_HOUR_CYCLE);
        }

        Optional<Long> optionalLongName = aircraftRepository.findAircraftIdByAircraftName(aircraftDto.getAircraftName());
        if (optionalLongName.isPresent() && (Objects.isNull(id) || !optionalLongName.get().equals(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_NAME_ALREADY_EXISTS);
        }
        Optional<Long> optionalLongSerial =
                aircraftRepository.findAircraftIdByAircraftSerial(aircraftDto.getAirframeSerial());
        if (optionalLongSerial.isPresent() && (Objects.isNull(id) || !optionalLongSerial.get().equals(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRFRAME_SERIAL_ALREADY_EXISTS);
        }

        if (aircraftDto.getBdTotalTime() > aircraftDto.getAirFrameTotalTime()) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.BANGLADESH_TOTAL_TIME_MUST_BE_SMALLER_OR_EQUAL_TO_AIRCRAFT_TOTAL_TIME);
        }

        if (aircraftDto.getBdTotalCycle() > aircraftDto.getAirframeTotalCycle()) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.BANGLADESH_TOTAL_CYCLE_MUST_BE_SMALLER_OR_EQUAL_TO_AIRCRAFT_TOTAL_CYCLE);
        }

        if (Objects.nonNull(aircraftDto.getAircraftCheckDoneHour()) &&
                aircraftDto.getAircraftCheckDoneHour() > aircraftDto.getAirFrameTotalTime()) {
            throw EngineeringManagementServerException
                    .badRequest(ErrorId.INVALID_A_CHECK_DONE_HOUR);
        }
        return Boolean.TRUE;
    }

    /**
     * This method is responsible for populating dto to entity
     *
     * @param aircraftDto {@link AircraftDto}
     * @param aircraft    {@link Aircraft}
     * @return responding entity {@link Aircraft}
     */
    private Aircraft populateEntity(AircraftDto aircraftDto, Aircraft aircraft) {
        aircraft.setAircraftName(aircraftDto.getAircraftName());
        aircraft.setManufactureDate(aircraftDto.getManufactureDate());
        aircraft.setAirframeSerial(aircraftDto.getAirframeSerial());
        aircraft.setDailyAverageHours(aircraftDto.getDailyAverageHours());
        aircraft.setDailyAverageCycle(aircraftDto.getDailyAverageCycle());

        aircraft.setDailyAverageApuCycle(aircraftDto.getDailyAverageApuCycle());
        aircraft.setDailyAverageApuHours(aircraftDto.getDailyAverageApuHours());
        aircraft.setTotalApuCycle(aircraftDto.getTotalApuCycle());
        aircraft.setTotalApuHours(aircraftDto.getTotalApuHours());

        aircraft.setEngineType(aircraftDto.getEngineType());
        aircraft.setPropellerType(aircraftDto.getPropellerType());

        if (!NumberUtil.checkValidAirTime(aircraftDto.getAirFrameTotalTime())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_TOTAL_AIRFRAME_TIME);
        }
        if (!NumberUtil.checkValidAirTime(aircraftDto.getBdTotalTime())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_TOTAL_BD_AIRFRAME_TIME);
        }
        aircraft.setAirFrameTotalTime(
                NumberUtil.getDefaultIfNull(aircraftDto.getAirFrameTotalTime(), 0.0));
        aircraft.setAirframeTotalCycle(
                NumberUtil.getDefaultIfNull(aircraftDto.getAirframeTotalCycle(), 0));
        aircraft.setBdTotalTime(
                NumberUtil.getDefaultIfNull(aircraftDto.getBdTotalTime(), 0.0));
        aircraft.setBdTotalCycle(
                NumberUtil.getDefaultIfNull(aircraftDto.getBdTotalCycle(), 0));
        if (Objects.isNull(aircraft.getAircraftModel()) ||
                !aircraft.getAircraftModel().getId().equals(aircraftDto.getAircraftModelId())) {
            aircraft.setAircraftModel(aircraftModelService.findById(aircraftDto.getAircraftModelId()));
        }

        aircraft.setAircraftCheckDoneHour(aircraftDto.getAircraftCheckDoneHour());
        aircraft.setAircraftCheckDoneDate(aircraftDto.getAircraftCheckDoneDate());
        aircraft.setInductionDate(aircraftDto.getInductionDate());

        return aircraft;
    }

    @Override
    public List<AircraftViewModel> getAllAircraft() {
        List<Aircraft> aircraftList = aircraftRepository.findAllByIsActive(true);
        List<AircraftViewModel> aircraftViewModels = new LinkedList<>();
        aircraftList.forEach(aircraft -> {
            aircraftViewModels.add(convertToResponseDto(aircraft));
        });
        return aircraftViewModels;
    }

    /**
     * This method is responsible for return aircraft list by aircraft model id
     *
     * @param acModelId {@link Long}
     * @return {@link AircraftEffectivityTypeViewModel}
     */
    @Override
    public List<AircraftEffectivityTypeViewModel> getAllAircraftByAcModelId(Long acModelId) {
        List<Aircraft> aircraftList = aircraftRepository.findAllByAircraftModelIdAndIsActive(acModelId, true);
        List<AircraftEffectivityTypeViewModel> aircraftEffectivityTypeViewModels = new ArrayList<>();
        aircraftList.forEach(
                aircraft -> {
                    AircraftEffectivityTypeViewModel aircraftEffectivityTypeViewModel
                            = new AircraftEffectivityTypeViewModel();
                    aircraftEffectivityTypeViewModel.setAircraftId(aircraft.getId());
                    aircraftEffectivityTypeViewModel.setAircraftName(aircraft.getAircraftName());
                    aircraftEffectivityTypeViewModels.add(aircraftEffectivityTypeViewModel);
                }
        );
        return aircraftEffectivityTypeViewModels;
    }

    /**
     * This Method generates header data of Daily hrs report
     *
     * @param aircraftId {@link Long}
     * @param amlDate    {@link LocalDate}
     * @param total      {@link DailyHrsReportTotalModel}
     * @return DailyHrsReportAircraftModel
     */
    @Override
    public DailyHrsReportAircraftModel findDailyHrsReportAircraftModelByAircraftById(Long aircraftId, LocalDate amlDate,
                                                                                     DailyHrsReportTotalModel total) {
        DailyHrsReportAircraftModel dailyHrsReportAircraftModel = aircraftRepository.findByAircraftId(aircraftId);

        if (Objects.nonNull(total)) {
            Double difHour = DateUtil.subtractTimes(dailyHrsReportAircraftModel.getAirFrameTotalTime(),
                    dailyHrsReportAircraftModel.getBdTotalTime());
            Double finalBdHour = DateUtil.subtractTimes(total.getGrandTotalAirTime(), difHour);

            Integer difCycle = dailyHrsReportAircraftModel.getAirframeTotalCycle() - dailyHrsReportAircraftModel.getBdTotalCycle();
            Integer finalBdCycle = total.getGrandTotalLanding() - difCycle;

            dailyHrsReportAircraftModel.setAirFrameTotalTime(total.getGrandTotalAirTime());
            dailyHrsReportAircraftModel.setAirframeTotalCycle(total.getGrandTotalLanding());
            dailyHrsReportAircraftModel.setBdTotalTime(finalBdHour);
            dailyHrsReportAircraftModel.setBdTotalCycle(finalBdCycle);
        }

        Optional<AircraftModel> aircraftModelOptional =
                aircraftModelService.findOptionalById(dailyHrsReportAircraftModel.getAircraftModelId(),
                        true);

        if (Objects.nonNull(dailyHrsReportAircraftModel.getAircraftCheckDoneDate())
                && amlDate.isBefore(dailyHrsReportAircraftModel.getAircraftCheckDoneDate())) {
            Page<LatestAirTimeResponse> acCheckDoneResponseList = aircraftCheckDoneRepository.findCloseAirTimeByAircraftId(
                    aircraftId, amlDate, PageRequest.of(0, 1));

            if (CollectionUtils.isNotEmpty(acCheckDoneResponseList.getContent())) {
                LatestAirTimeResponse acCheckDoneResponse = acCheckDoneResponseList.getContent().get(0);
                dailyHrsReportAircraftModel.setAircraftCheckDoneHour(acCheckDoneResponse.getAircraftCheckDoneHour());
                dailyHrsReportAircraftModel.setAircraftCheckDoneDate(acCheckDoneResponse.getAircraftCheckDoneDate());
            } else {
                dailyHrsReportAircraftModel.setAircraftCheckDoneHour(0.0);
                dailyHrsReportAircraftModel.setAircraftCheckDoneDate(null);
            }
        }

        if (aircraftModelOptional.isPresent()) {
            AircraftModel aircraftModel = aircraftModelOptional.get();

            if (Objects.nonNull(aircraftModel.getCheckHourForA())) {
                dailyHrsReportAircraftModel.setACheckTimeRemainHours(
                        PlanningUtil.calculateRemainingHour(aircraftModel.getCheckHourForA(),
                                dailyHrsReportAircraftModel.getAircraftCheckDoneHour(),
                                dailyHrsReportAircraftModel.getAirFrameTotalTime()));
            }

            if (Objects.nonNull(aircraftModel.getCheckDaysForA())
                    && Objects.nonNull(dailyHrsReportAircraftModel.getAircraftCheckDoneDate())) {
                dailyHrsReportAircraftModel.setACheckTimeRemainDays(
                        PlanningUtil.calculateRemainingDays(aircraftModel.getCheckDaysForA(),
                                dailyHrsReportAircraftModel.getAircraftCheckDoneDate(), amlDate));
            }
        }

        return dailyHrsReportAircraftModel;
    }

    private AircraftSearchViewModel convertToSearchResponse(Aircraft aircraft) {
        AircraftSearchViewModel aircraftSearchViewModel = new AircraftSearchViewModel();
        aircraftSearchViewModel.setId(aircraft.getId());
        aircraftSearchViewModel.setAircraftModelName(aircraft.getAircraftModel().getAircraftModelName());
        aircraftSearchViewModel.setAircraftName(aircraft.getAircraftName());
        aircraftSearchViewModel.setAirframeSerial(aircraft.getAirframeSerial());
        return aircraftSearchViewModel;
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        Specification<Aircraft> propellerSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<Aircraft>()
                        .active(searchDto.getIsActive(), IS_ACTIVE_FIELD));
        Page<Aircraft> pagedData = aircraftRepository.findAll(propellerSpecification, pageable);
        List<Object> models = pagedData.getContent()
                .stream().map(this::convertToSearchResponse).collect(Collectors.toList());
        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method will prepare header data for Sector wise Utilization report
     *
     * @param aircraftId            {@link Long}
     * @param utilizationReportData {@link UtilizationReportResponse}
     * @return
     */
    @Override
    public UtilizationReportResponse utilizationReportHeader(Long aircraftId,
                                                             UtilizationReportResponse utilizationReportData) {
        Optional<UtilizationReportResponse> utilizationReportResponse =
                aircraftRepository.findUtilizationReportHeaderByAircraftId(aircraftId);
        if (utilizationReportResponse.isPresent()) {
            utilizationReportData.setAircraftName(utilizationReportResponse.get().getAircraftName());
            utilizationReportData.setAirframeSerial(utilizationReportResponse.get().getAirframeSerial());
        }
        return utilizationReportData;
    }


    /**
     * This Method will find the Aircraft model of aircraft
     *
     * @param aircraftId {@link Long}
     * @return aircraftModelId  {@link Long}
     */
    public Long getAircraftModelIdByAircraftId(Long aircraftId) {
        return aircraftRepository.findAircraftModelIdByAircraftId(aircraftId);
    }

    /**
     * This method is responsible for finding aircraft and aml last page no by aircraft
     *
     * @param aircraftId aircraft id
     * @return aircraft and aml last page no as view model
     */
    @Override
    public AmlLastPageAndAircraftInfo findAircraftInfo(Long aircraftId) {
        return aircraftRepository.findAircraftInfo(aircraftId);
    }

    /**
     * This Method will find all active aircraft by ids
     *
     * @param aircraftIds {@link Set<Long>}
     * @return {@link List<AircraftProjection>}
     */
    public List<AircraftProjection> findAircraftByIdInAndIsActiveTrue(Set<Long> aircraftIds) {
        if (CollectionUtils.isEmpty(aircraftIds)) {
            return Collections.emptyList();
        }
        return aircraftRepository.findAircraftByIdInAndIsActiveTrue(aircraftIds);
    }

    @Override
    public AircraftInfoViewModel findAircraftInfoData(Long aircraftId) {
        Optional<Aircraft> aircraft = aircraftRepository.findById(aircraftId);
        if (aircraft.isPresent()) {
            return convertToAircraftInfo(aircraft.get());
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
    }

    @Override
    public List<Aircraft> findAllActiveAircraftByAircraftModel(Long aircraftModelId) {
        return aircraftRepository.findAllByAircraftModelIdAndIsActive(aircraftModelId, true);
    }

    @Override
    public List<AircraftDropdownViewModel> getAllActiveAircraft() {
        return aircraftRepository.findAllActiveAircraft();
    }

    @Override
    public List<ApuAvailableAircraftViewModel> getAllApuAvailableAircraft() {
        return aircraftRepository.findAvailableApuAircraft();
    }

    protected AircraftInfoViewModel convertToAircraftInfo(Aircraft aircraft) {
        AircraftInfoViewModel aircraftViewModel = new AircraftInfoViewModel();

        aircraftViewModel.setId(aircraft.getId());
        aircraftViewModel.setAircraftName(aircraft.getAircraftName());
        aircraftViewModel.setManufactureDate(aircraft.getManufactureDate());
        aircraftViewModel.setAirframeSerial(aircraft.getAirframeSerial());
        aircraftViewModel.setAcHour(aircraft.getAirFrameTotalTime());
        aircraftViewModel.setAcCycle(aircraft.getAirframeTotalCycle());
        aircraftViewModel.setAverageHours((aircraft.getDailyAverageHours()));
        aircraftViewModel.setAverageCycle((aircraft.getDailyAverageCycle()));

        aircraftViewModel.setEngineType(aircraft.getEngineType());
        aircraftViewModel.setPropellerType(aircraft.getPropellerType());

        aircraftViewModel.setApuCycle(aircraft.getTotalApuCycle());
        aircraftViewModel.setApuHours(aircraft.getTotalApuHours());
        aircraftViewModel.setUpdatedTime(aircraft.getUpdatedAt());
        aircraftViewModel.setInductionDate(aircraft.getInductionDate());
        if (Objects.nonNull(aircraft.getAircraftModel())) {
            aircraftViewModel.setAircraftModelName(aircraft.getAircraftModel().getAircraftModelName());
        }

        return aircraftViewModel;
    }

    /**
     * This method is responsible for finding ad report title by aircraft
     *
     * @param aircraftId {@link Long}
     * @return adReportTitleDataViewModel {@link AdReportTitleDataViewModel}
     */
    public AdReportTitleDataViewModel getAdReportTitleData(Long aircraftId) {
        return aircraftRepository.fimdAdReportTitleDataByAircraft(aircraftId);
    }
}

