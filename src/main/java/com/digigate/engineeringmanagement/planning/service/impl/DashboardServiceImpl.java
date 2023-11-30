package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.CheckType;
import com.digigate.engineeringmanagement.planning.constant.DashboardItemType;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckDone;
import com.digigate.engineeringmanagement.planning.entity.DashboardItem;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftData;
import com.digigate.engineeringmanagement.planning.payload.response.DashboardAcModelView;
import com.digigate.engineeringmanagement.planning.payload.response.DueResponse;
import com.digigate.engineeringmanagement.planning.repository.AircraftCheckDoneRepository;
import com.digigate.engineeringmanagement.planning.repository.DashboardItemRepository;
import com.digigate.engineeringmanagement.planning.service.DashboardService;
import com.digigate.engineeringmanagement.planning.service.MelIService;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final AircraftRepository aircraftRepository;
    private final AircraftCheckDoneRepository aircraftCheckDoneRepository;
    private final DashboardItemRepository dashboardItemRepository;
    private final AircraftModelService aircraftModelService;
    private final MelIService melIService;
    private static final Integer CHECK_2Y = 730;
    private static final Integer CHECK_4Y = 1460;
    private static final Integer CHECK_8Y = 2920;

    private static final Integer MEL_MAX_DUE_LIMIT = 7;

    private static final String INITIAL_DELAY_VAL = "60000";
    private static final String FIXED_DELAY_VAL = "86400000";

    @Autowired
    public DashboardServiceImpl(AircraftRepository aircraftRepository,
                                DashboardItemRepository dashboardItemRepository, AircraftModelService aircraftModelService,
                                MelIService melIService, AircraftCheckDoneRepository aircraftCheckDoneRepository) {
        this.aircraftRepository = aircraftRepository;
        this.dashboardItemRepository = dashboardItemRepository;
        this.aircraftModelService = aircraftModelService;
        this.melIService = melIService;
        this.aircraftCheckDoneRepository = aircraftCheckDoneRepository;
    }

    @Override
    public PageData getAircraftDashboard(Long aircraftIdModelId, Pageable pageable) {
        Optional<AircraftModel> aircraftModelOptional =
                aircraftModelService.findOptionalById(aircraftIdModelId, true);

        if (aircraftModelOptional.isEmpty()) {
            throw new EngineeringManagementServerException(ErrorId.INVALID_AIRCRAFT_MODEL, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        Page<Aircraft> aircraftPage =
                aircraftRepository.findAllByAircraftModelIdAndIsActive(aircraftIdModelId, true, pageable);

        List<DashboardAcModelView> dashBoardModels = new ArrayList<>();
        for (Aircraft aircraft : aircraftPage.getContent()) {
            DashboardAcModelView dashboardAcModelView = new DashboardAcModelView();
            prepareAircraftData(aircraft, dashboardAcModelView, aircraftModelOptional.get());

            List<DueResponse> dashboardItems = dashboardItemRepository.findDueListByAircraftId(aircraft.getId());
            List<DueResponse> melList = new ArrayList<>();

            dashboardItems.forEach(d -> {
                if (d.getItemType().equals(DashboardItemType.MEL)) {
                    melList.add(d);

                } else if (d.getItemType().equals(DashboardItemType.A_CHECK)) {
                    setRemainCalculation(d, aircraft);
                    dashboardAcModelView.setCheckA(d);

                } else if (d.getItemType().equals(DashboardItemType.C_CHECK)) {
                    setRemainCalculation(d, aircraft);
                    dashboardAcModelView.setCheckC(d);

                } else if (d.getItemType().equals(DashboardItemType.CHECK_2Y)) {
                    setRemainCalculationForYearCheck(d);
                    dashboardAcModelView.setCheck2Y(d);

                } else if (d.getItemType().equals(DashboardItemType.CHECK_4Y)) {
                    setRemainCalculationForYearCheck(d);
                    dashboardAcModelView.setCheck4Y(d);

                } else if (d.getItemType().equals(DashboardItemType.CHECK_8Y)) {
                    setRemainCalculationForYearCheck(d);
                    dashboardAcModelView.setCheck8Y(d);
                }
            });
            dashboardAcModelView.setMelDueList(melList);
            dashBoardModels.add(dashboardAcModelView);
        }

        return new PageData(dashBoardModels, aircraftPage.getTotalPages(), aircraftPage.getNumber() + 1,
                aircraftPage.getTotalElements());
    }

    private void setRemainCalculation(DueResponse data, Aircraft aircraft){
        if(Objects.nonNull(data.getCalenderDueDate())){
            data.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), data.getCalenderDueDate()));
        }

        if(Objects.nonNull(data.getNextDueHour())){
            data.setRemainingHour(DateUtil.subtractTimes(data.getNextDueHour(), aircraft.getAirFrameTotalTime()));
        }
    }

    private void setRemainCalculationForYearCheck(DueResponse data){
        if(Objects.nonNull(data.getEstimatedDueDate())){
            data.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), data.getEstimatedDueDate()));
        }
    }

    private void prepareAircraftData(Aircraft aircraft, DashboardAcModelView dashboardAcModelView,
                                     AircraftModel aircraftModel) {
        AircraftData aircraftData = new AircraftData();
        aircraftData.setAircraftName(aircraft.getAircraftName());
        aircraftData.setAircraftSerial(aircraft.getAirframeSerial());
        aircraftData.setAsOfDate(aircraft.getUpdatedAt());
        aircraftData.setAircraftId(aircraft.getId());
        aircraftData.setAircraftTotalHour(aircraft.getAirFrameTotalTime());
        aircraftData.setAircraftTotalCycle(aircraft.getAirframeTotalCycle());
        prepareAcCheckRemaining(aircraft, aircraftData, aircraftModel);
        dashboardAcModelView.setAircraftData(aircraftData);
    }

    private void prepareAcCheckRemaining(Aircraft aircraft, AircraftData aircraftData, AircraftModel aircraftModel) {
        if (Objects.nonNull(aircraftModel.getCheckHourForA())) {
            aircraftData.setACheckRemainHour(
                    PlanningUtil.calculateRemainingHour(aircraftModel.getCheckHourForA(),
                            aircraft.getAircraftCheckDoneHour(), aircraft.getAirFrameTotalTime()));
        }

        if (Objects.nonNull(aircraftModel.getCheckDaysForA())
                && Objects.nonNull(aircraft.getAircraftCheckDoneDate())) {
            aircraftData.setACheckRemainDays(PlanningUtil.calculateRemainingDays(aircraftModel.getCheckDaysForA(),
                    aircraft.getAircraftCheckDoneDate(), LocalDate.now()));
        }
    }

    private DashboardItem mapToEntity(DueResponse response, Aircraft aircraft, DashboardItemType itemType) {
        DashboardItem entity = new DashboardItem();
        entity.setAircraft(aircraft);
        entity.setItemType(itemType);
        entity.setDueDate(response.getEstimatedDueDate());
        return entity;
    }

    private List<DashboardItem> getMelDueDate(Aircraft aircraft) {
        List<DueResponse> melDueDataList = melIService.findOpenClosestMel(aircraft.getId());
        return prepareDashboardItemEntity(aircraft, melDueDataList, DashboardItemType.MEL, MEL_MAX_DUE_LIMIT);
    }

    private List<DashboardItem> prepareDashboardItemEntity(Aircraft aircraft, List<DueResponse> dueResponseList,
                                                           DashboardItemType itemType, Integer limit) {
        List<DashboardItem> dashboardItemList = new ArrayList<>();
        dueResponseList.forEach(d -> {
            dashboardItemList.add(mapToEntity(d, aircraft, itemType));
        });
        return dashboardItemList;
    }

    @Scheduled(initialDelayString = INITIAL_DELAY_VAL, fixedDelayString = FIXED_DELAY_VAL)
    public void processAndSaveDashboardLdndDueDate() {
        List<Aircraft> aircraftList = aircraftRepository.findAllByIsActive(true);
        List<DashboardItem> dashboardItemList = new ArrayList<>();
        aircraftList.forEach(aircraft -> {
            dashboardItemList.addAll(getMelDueDate(aircraft));
            processCheckDoneForCheckForAOrCType(aircraft, dashboardItemList, CheckType.A);
            processCheckDoneForCheckForAOrCType(aircraft, dashboardItemList, CheckType.C);
            processCheckDoneY(aircraft, CheckType.TWO_YEAR, dashboardItemList);
            processCheckDoneY(aircraft, CheckType.FOUR_YEAR, dashboardItemList);
            processCheckDoneY(aircraft, CheckType.EIGHT_YEAR, dashboardItemList);
        });
        dashboardItemRepository.deleteAllInBatch();
        saveItem(dashboardItemList);
    }

    private void processCheckDoneForCheckForAOrCType(Aircraft aircraft, List<DashboardItem> dashboardItemList,
                                                     CheckType checkType) {

        Optional<AircraftCheckDone> aircraftCheckDone =
                aircraftCheckDoneRepository.findTopByAircraftIdAndCheckTypeAndIsActiveTrue(aircraft.getId(), checkType);

        double intervalHour = 0.0;
        long intervalDays = 0;

        if (checkType.equals(CheckType.A)) {
            if (Objects.nonNull(aircraft.getAircraftModel().getCheckHourForA())) {
                intervalHour = aircraft.getAircraftModel().getCheckHourForA();
            }

            if (Objects.nonNull(aircraft.getAircraftModel().getCheckDaysForA())) {
                intervalDays = aircraft.getAircraftModel().getCheckDaysForA().longValue();
            }

        } else {
            if (Objects.nonNull(aircraft.getAircraftModel().getCheckHourForC())) {
                intervalHour = aircraft.getAircraftModel().getCheckHourForC();
            }

            if (Objects.nonNull(aircraft.getAircraftModel().getCheckDaysForC())) {
                intervalDays = aircraft.getAircraftModel().getCheckDaysForC().longValue();
            }
        }

        Double avgHour = aircraft.getDailyAverageHours();
        LocalDate calenderDueDate = null;

        if (aircraftCheckDone.isPresent()) {
            Double nextDueHour = DateUtil.addTimes(aircraftCheckDone.get().getAircraftCheckDoneHour(), intervalHour);
            Double remainingHour = DateUtil.subtractTimes(nextDueHour, aircraft.getAirFrameTotalTime());
            LocalDate nextDueDate = aircraftCheckDone.get().getAircraftCheckDoneDate().plusDays((long) (remainingHour / avgHour));

            if (Objects.nonNull(aircraftCheckDone.get().getAircraftCheckDoneDate())) {
                calenderDueDate = aircraftCheckDone.get().getAircraftCheckDoneDate().plusDays(intervalDays);
            }

            if (checkType.equals(CheckType.A)) {
                dashboardItemList.add(mapToEntity(aircraft, DashboardItemType.A_CHECK, nextDueHour, nextDueDate, calenderDueDate));
            } else {
                dashboardItemList.add(mapToEntity(aircraft, DashboardItemType.C_CHECK, nextDueHour, nextDueDate, calenderDueDate));
            }
        }
    }


    private void processCheckDoneY(Aircraft aircraft, CheckType checkType, List<DashboardItem> dashboardItemList) {

        Optional<AircraftCheckDone> aircraftCheckDone =
                aircraftCheckDoneRepository.findTopByAircraftIdAndCheckTypeAndIsActiveTrue(aircraft.getId(), checkType);

        DashboardItemType dashboardItemType = null;
        if (aircraftCheckDone.isPresent()) {
            long intervalDay = 0;
            if (Objects.equals(checkType, CheckType.TWO_YEAR)) {
                intervalDay = CHECK_2Y;
                dashboardItemType = DashboardItemType.CHECK_2Y;
            } else if (Objects.equals(checkType, CheckType.FOUR_YEAR)) {
                intervalDay = CHECK_4Y;
                dashboardItemType = DashboardItemType.CHECK_4Y;
            } else if (Objects.equals(checkType, CheckType.EIGHT_YEAR)) {
                intervalDay = CHECK_8Y;
                dashboardItemType = DashboardItemType.CHECK_8Y;
            }

            LocalDate nexDueDate = aircraftCheckDone.get().getAircraftCheckDoneDate().plusDays(intervalDay);
            
            dashboardItemList.add(mapToEntity(aircraft, dashboardItemType, null, nexDueDate,
                    null));
        }
    }

    private DashboardItem mapToEntity(Aircraft aircraft, DashboardItemType itemType, Double nextDueHour,
                                      LocalDate dueDate, LocalDate calenderDueDate) {
        DashboardItem entity = new DashboardItem();
        entity.setAircraft(aircraft);
        entity.setItemType(itemType);
        entity.setNextDueHour(nextDueHour);

        if (Objects.nonNull(calenderDueDate)) {
            if (calenderDueDate.isBefore(dueDate)) {
                entity.setDueDate(calenderDueDate);
            } else {
                entity.setDueDate(dueDate);
            }
            entity.setCalenderDueDate(calenderDueDate);
        } else {
            entity.setDueDate(dueDate);
        }
        return entity;
    }

    private void saveItem(List<DashboardItem> entityList) {
        try {
            dashboardItemRepository.saveAll(entityList);
        } catch (Exception e) {
            String name = entityList.getClass().getSimpleName();
            throw EngineeringManagementServerException.dataSaveException(
                    Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                            name));
        }
    }
}
