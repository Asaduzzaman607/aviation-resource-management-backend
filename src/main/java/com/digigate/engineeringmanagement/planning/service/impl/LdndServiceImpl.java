package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.HourCalculationType;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.ManHourReportDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDto;
import com.digigate.engineeringmanagement.planning.payload.response.LdndForTaskViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LdndViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LdndViewModelForForecast;
import com.digigate.engineeringmanagement.planning.repository.LdndRepository;
import com.digigate.engineeringmanagement.planning.repository.TaskProcedureRepository;
import com.digigate.engineeringmanagement.planning.service.LdndService;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.SerialService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LdndServiceImpl implements LdndService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final LdndRepository ldndRepository;

    private final IService<Task, TaskDto> taskService;
    private final AircraftService aircraftService;
    private final TaskProcedureRepository taskProcedureRepository;

    private final AircraftRepository aircraftRepository;
    private final PartService partService;

    private final SerialService serialService;

    private static final String INITIAL_DELAY_VAL = "300000";
    private static final String FIXED_DELAY_VAL = "21600000";

    @Autowired
    public LdndServiceImpl(LdndRepository ldndRepository, IService<Task, TaskDto> taskService,
                           AircraftService aircraftService,
                           TaskProcedureRepository taskProcedureRepository,
                           AircraftRepository aircraftRepository, PartService partService, SerialService serialService) {
        this.ldndRepository = ldndRepository;
        this.taskService = taskService;
        this.aircraftService = aircraftService;
        this.taskProcedureRepository = taskProcedureRepository;
        this.aircraftRepository = aircraftRepository;
        this.partService = partService;
        this.serialService = serialService;
    }

    @Override
    public Ldnd findExistingLdnd(Long taskId, Long partId, Long serialId) {
        Optional<Ldnd> optionalLdnd = ldndRepository.findByTaskIdAndPartIdAndSerialIdAndIsActiveTrue(
                taskId, partId, serialId);
        return optionalLdnd.orElse(new Ldnd());
    }

    @Override
    public Ldnd save(Ldnd ldndEntity) {
        try {
            return ldndRepository.save(ldndEntity);
        } catch (Exception e) {
            String name = ldndEntity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                    Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, name));
        }
    }

    @Override
    public Ldnd convertToLdndEntity(TaskDoneDto TaskDoneDto, Ldnd ldnd) {
        return mapToLdndEntity(TaskDoneDto, ldnd);
    }

    /**
     * convert dto to entity for save/update purpose
     *
     * @param dto {@link  TaskDoneDto}
     * @return entity  {@link Ldnd}
     */
    private Ldnd mapToLdndEntity(TaskDoneDto dto, Ldnd entity) {
        Task task = taskService.findById(dto.getTaskId());
        entity.setTask(task);

        Aircraft aircraft = aircraftService.findById(dto.getAircraftId());
        entity.setAircraft(aircraft);

        if (Objects.nonNull(dto.getDoneHour()) && dto.getDoneHour() > aircraft.getAirFrameTotalTime()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.DONE_HOUR_EXCEED_THAN_TOTAL_AIRCRAFT_HOUR);
        }

        if (Objects.nonNull(dto.getDoneCycle()) && dto.getDoneCycle() > aircraft.getAirframeTotalCycle()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.DONE_CYCLE_EXCEED_THAN_TOTAL_AIRCRAFT_CYCLE);
        }

        Part part = partService.findById(dto.getPartId());
        entity.setPart(part);

        Serial serial = serialService.findById(dto.getSerialId());

        entity.setSerial(serial);

        Optional<TaskProcedure> taskProcedure = taskProcedureRepository.findByTaskIdAndPositionId(dto.getTaskId(),
                dto.getPositionId());
        taskProcedure.ifPresent(entity::setTaskProcedure);
        entity.setIsApuControl(dto.getIsApuControl());
        entity.setDoneDate(dto.getDoneDate());

        if (Objects.nonNull(dto.getDoneHour()) && !NumberUtil.checkValidAirTime(dto.getDoneHour())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DONE_HOUR_TIME);
        }
        entity.setDoneHour(dto.getDoneHour());

        entity.setDoneCycle(dto.getDoneCycle());
        entity.setInitialCycle(dto.getInitialCycle());

        if (Objects.nonNull(dto.getInitialHour()) && !NumberUtil.checkValidAirTime(dto.getInitialHour())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_INITIAL_HOUR_TIME);
        }
        entity.setInitialHour(dto.getInitialHour());
        entity.setIntervalType(dto.getIntervalType());
        entity.setDueCycle(dto.getDueCycle());
        entity.setDueHour(dto.getDueHour());
        entity.setDueDate(dto.getDueDate());
        entity.setRemainingCycle(dto.getRemainingCycle());
        entity.setRemainingHour(dto.getRemainingHour());
        entity.setRemainingDay(dto.getRemainingDay());
        entity.setEstimatedDueDate(dto.getEstimatedDueDate());
        entity.setTaskStatus(dto.getTaskStatus());
        return entity;
    }


    public Ldnd calculateLdnd(Ldnd ldnd) {
        ldnd = calculateNextDue(ldnd);
        if (ldnd.getIsApuControl()) {
            ldnd = calculateRemainForApu(ldnd, ldnd.getAircraft());
            ldnd = calculateEstimatedDateForApu(ldnd, ldnd.getAircraft(), null);
        } else {
            ldnd = calculateRemainForFlight(ldnd, ldnd.getAircraft());
            ldnd = calculateEstimatedDateForFlight(ldnd, ldnd.getAircraft(), null);
        }

        return ldnd;
    }

    @Override
    public Ldnd calculateNextDue(Ldnd ldnd) {
        Task task = ldnd.getTask();
//        Due Calculation
        if (ldnd.getIntervalType().equals(IntervalType.INTERVAL)) {

            if (Objects.isNull(task.getIntervalCycle())
                    && Objects.isNull(task.getIntervalHour())
                    && Objects.isNull(task.getIntervalDay())) {
                throw EngineeringManagementServerException.notFound(ErrorId.NO_INTERVAL_EXIST);
            }

            if (Objects.nonNull(task.getIntervalCycle()) && Objects.nonNull(ldnd.getDoneCycle())) {

                if (Objects.nonNull(ldnd.getInitialCycle())) {
                    ldnd.setDueCycle(ldnd.getDoneCycle() + task.getIntervalCycle() - ldnd.getInitialCycle());
                } else {
                    ldnd.setDueCycle(ldnd.getDoneCycle() + task.getIntervalCycle());
                }

            }

            if (Objects.nonNull(task.getIntervalHour()) && Objects.nonNull(ldnd.getDoneHour())) {
                Double dueHour = DateUtil.calculateHour(ldnd.getDoneHour(), task.getIntervalHour(), HourCalculationType.ADD);
                if (Objects.nonNull(ldnd.getInitialHour())) {
                    ldnd.setDueHour(DateUtil.calculateHour(dueHour, ldnd.getInitialHour(), HourCalculationType.SUBTRACT));
                } else {
                    ldnd.setDueHour(dueHour);
                }
            }

            if (Objects.nonNull(task.getIntervalDay()) && Objects.nonNull(ldnd.getDoneDate())) {
                ldnd.setDueDate(ldnd.getDoneDate().plusDays(task.getIntervalDay().longValue()));
            }
        } else if (ldnd.getIntervalType().equals(IntervalType.THRESHOLD)) {
            if (Objects.isNull(task.getThresholdDay())
                    && Objects.isNull(task.getThresholdCycle())
                    && Objects.isNull(task.getThresholdHour())) {
                throw EngineeringManagementServerException.notFound(ErrorId.NO_THRESHOLD_EXIST);
            }

            if (Objects.nonNull(task.getThresholdCycle()) && Objects.nonNull(ldnd.getDoneCycle())) {

                if (Objects.nonNull(ldnd.getInitialCycle())) {
                    ldnd.setDueCycle(ldnd.getDoneCycle() + task.getThresholdCycle() - ldnd.getInitialCycle());
                } else {
                    ldnd.setDueCycle(ldnd.getDoneCycle() + task.getThresholdCycle());
                }

            }

            if (Objects.nonNull(task.getThresholdHour()) && Objects.nonNull(ldnd.getDoneHour())) {
                Double dueHour = DateUtil.calculateHour(ldnd.getDoneHour(), task.getThresholdHour(), HourCalculationType.ADD);
                if (Objects.nonNull(ldnd.getInitialHour())) {
                    ldnd.setDueHour(DateUtil.calculateHour(dueHour, ldnd.getInitialHour(), HourCalculationType.SUBTRACT));
                } else {
                    ldnd.setDueHour(dueHour);
                }
            }

            if (Objects.nonNull(task.getThresholdDay()) && Objects.nonNull(ldnd.getDoneDate())) {
                ldnd.setDueDate(ldnd.getDoneDate().plusDays(task.getThresholdDay().longValue()));
            }
        }
        return ldnd;
    }

    @Override
    public Ldnd calculateRemainForFlight(Ldnd ldnd, Aircraft aircraft) {

//      Remain  Calculation
        if (Objects.nonNull(ldnd.getDueHour())) {
            ldnd.setRemainingHour(DateUtil.calculateHour(ldnd.getDueHour(), aircraft.getAirFrameTotalTime(),
                    HourCalculationType.SUBTRACT));
        }

        if (Objects.nonNull(ldnd.getDueCycle())) {
            ldnd.setRemainingCycle(ldnd.getDueCycle() - aircraft.getAirframeTotalCycle());
        }

        if (Objects.nonNull(ldnd.getDueDate())) {
            ldnd.setRemainingDay(getRemainDay(ldnd, null));
        }
        return ldnd;
    }

    @Override
    public Ldnd calculateRemainForApu(Ldnd ldnd, Aircraft aircraft) {
//      Remain  Calculation
        if (Objects.nonNull(ldnd.getDueHour())) {
            ldnd.setRemainingHour(ldnd.getDueHour() - aircraft.getTotalApuHours());
        }

        if (Objects.nonNull(ldnd.getDueCycle())) {
            ldnd.setRemainingCycle(ldnd.getDueCycle() - aircraft.getTotalApuCycle());
        }

        if (Objects.nonNull(ldnd.getDueDate())) {
            ldnd.setRemainingDay(getRemainDay(ldnd, null));
        }
        return ldnd;
    }

    @Override
    public Ldnd calculateEstimatedDateForFlight(Ldnd ldnd, Aircraft aircraft, LocalDate amlDate) {
        int MAX_VALUE = Integer.MAX_VALUE;
        long estimatedFHDay = MAX_VALUE;
        long estimatedFCDay = MAX_VALUE;
        long estimatedDay = MAX_VALUE;

        if (Objects.nonNull(ldnd.getRemainingHour())) {
            if (ldnd.getRemainingHour().equals(ApplicationConstant.DOUBLE_VALUE_ZERO)) {
                estimatedFHDay = ApplicationConstant.VALUE_ZERO;
            } else {
                Double val = DateUtil.calculateHour(ldnd.getRemainingHour(), aircraft.getDailyAverageHours(),
                        HourCalculationType.DAY_COUNT);
                if (val != null) {
                    estimatedFHDay = val.longValue();
                }
            }
        }

        if (Objects.nonNull(ldnd.getRemainingCycle())) {
            if (ldnd.getRemainingCycle().equals(ApplicationConstant.VALUE_ZERO)) {
                estimatedFCDay = ApplicationConstant.VALUE_ZERO;
            } else {
                Double val = DateUtil.calculateHour(ldnd.getRemainingCycle().doubleValue(), aircraft.getDailyAverageCycle().doubleValue(),
                        HourCalculationType.DAY_COUNT);
                if (val != null) {
                    estimatedFCDay = val.longValue();
                }
            }

        }

        if (Objects.nonNull(ldnd.getRemainingDay())) {
            estimatedDay = ldnd.getRemainingDay();
        }
        estimatedDay = Math.min(estimatedDay, Math.min(estimatedFHDay, estimatedFCDay));

        if (estimatedDay == MAX_VALUE) {
            throw new EngineeringManagementServerException(ErrorId.ESTIMATED_DUE_CALCULATION_FAILED,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        LocalDate date = (Objects.nonNull(amlDate)) ? amlDate.plusDays(estimatedDay) :
                DateUtil.getCurrentUTCDate().plusDays(estimatedDay);
        ldnd.setEstimatedDueDate(date);

        return ldnd;
    }

    @Override
    public Ldnd calculateEstimatedDateForApu(Ldnd ldnd, Aircraft aircraft, LocalDate amlDate) {
        int MAX_VALUE = Integer.MAX_VALUE;
        long estimatedFHDay = MAX_VALUE;
        long estimatedFCDay = MAX_VALUE;
        long estimatedDay = MAX_VALUE;

        if (Objects.nonNull(ldnd.getRemainingHour())) {
            if (ldnd.getRemainingHour().equals(ApplicationConstant.DOUBLE_VALUE_ZERO)) {
                estimatedFHDay = ApplicationConstant.VALUE_ZERO;
            } else {
                Double val = DateUtil.calculateHour(ldnd.getRemainingHour(), aircraft.getDailyAverageApuHours(),
                        HourCalculationType.DAY_COUNT);
                if (val != null) {
                    estimatedFHDay = val.longValue();
                }
            }
        }

        if (Objects.nonNull(ldnd.getRemainingCycle())) {
            if (ldnd.getRemainingCycle().equals(ApplicationConstant.VALUE_ZERO)) {
                estimatedFCDay = ApplicationConstant.VALUE_ZERO;
            } else {
                Double val = DateUtil.calculateHour(ldnd.getRemainingCycle().doubleValue(),
                        aircraft.getDailyAverageApuCycle().doubleValue(),
                        HourCalculationType.DAY_COUNT);
                if (val != null) {
                    estimatedFCDay = val.longValue();
                }
            }

        }

        if (Objects.nonNull(ldnd.getRemainingDay())) {
            estimatedDay = ldnd.getRemainingDay();
        }
        estimatedDay = Math.min(estimatedDay, Math.min(estimatedFHDay, estimatedFCDay));

        if (estimatedDay == MAX_VALUE) {
            throw new EngineeringManagementServerException(ErrorId.ESTIMATED_DUE_CALCULATION_FAILED,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        LocalDate date = (Objects.nonNull(amlDate)) ? amlDate.plusDays(estimatedDay) :
                DateUtil.getCurrentUTCDate().plusDays(estimatedDay);
        ldnd.setEstimatedDueDate(date);

        return ldnd;
    }

    @Override
    public Ldnd calculateRemainForFlight(Ldnd ldnd, AmlFlightData amlFlightData, LocalDate amlDate) {

//      Remain  Calculation
        if (Objects.nonNull(ldnd.getDueHour()) && Objects.nonNull(amlFlightData.getAirTime())) {
            ldnd.setRemainingHour(DateUtil.calculateHour(ldnd.getDueHour(), amlFlightData.getGrandTotalAirTime(),
                    HourCalculationType.SUBTRACT));
        }

        if (Objects.nonNull(ldnd.getDueCycle()) && Objects.nonNull(amlFlightData.getNoOfLanding())) {
            ldnd.setRemainingCycle(ldnd.getDueCycle() - amlFlightData.getGrandTotalLanding());
        }

        if (Objects.nonNull(amlFlightData.getAirTime()) || Objects.nonNull(amlFlightData.getNoOfLanding())) {
            Long remainDay = getRemainDay(ldnd, amlDate);
            if (Objects.nonNull(remainDay)) {
                ldnd.setRemainingDay(remainDay);
            }
        }
        return ldnd;
    }

    @Override
    public Ldnd calculateRemainForApu(Ldnd ldnd, AmlFlightData amlFlightData, LocalDate amlDate) {
//      Remain  Calculation
        if (Objects.nonNull(ldnd.getDueHour()) && Objects.nonNull(amlFlightData.getTotalApuHours())) {
            ldnd.setRemainingHour(DateUtil.calculateHour(ldnd.getDueHour(), amlFlightData.getTotalApuHours(),
                    HourCalculationType.SUBTRACT));
        }

        if (Objects.nonNull(ldnd.getDueCycle()) && Objects.nonNull(amlFlightData.getTotalApuCycles())) {
            ldnd.setRemainingCycle(ldnd.getDueCycle() - amlFlightData.getTotalApuCycles());
        }

        if (Objects.nonNull(amlFlightData.getTotalApuCycles()) || Objects.nonNull(amlFlightData.getTotalApuHours())) {
            Long remainDay = getRemainDay(ldnd, amlDate);
            if (Objects.nonNull(remainDay)) {
                ldnd.setRemainingDay(remainDay);
            }
        }
        return ldnd;
    }

    private Long getRemainDay(Ldnd ldnd, LocalDate amlDate) {
        Long remainDay = null;
        if (Objects.nonNull(amlDate) && Objects.nonNull(ldnd.getDueDate())) {
            remainDay = ChronoUnit.DAYS.between(amlDate, ldnd.getDueDate());
        } else {
            if (Objects.nonNull(ldnd.getDueDate())) {
                remainDay = ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), ldnd.getDueDate());
            }
        }
        return remainDay;
    }

    @Override
    public List<Ldnd> findLdndListByAircraftId(Long aircraftId) {
        return ldndRepository.findAllByAircraftIdAndIsActiveTrue(aircraftId);
    }

    /**
     * This method is responsible for find all Ldnd Task by task ids
     *
     * @param taskIds    {@link Long}
     * @param aircraftId {@link Long}
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    @Override
    public List<LdndForTaskViewModel> findAllLdndTaskByTaskIdIn(Set<Long> taskIds, Long aircraftId) {
        return ldndRepository.findAllLdndTaskByTaskIdIn(taskIds, aircraftId);
    }

    /**
     * This method is responsible for find all Ldnd Task by aircraft
     *
     * @param aircraftId {@link Long}
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    @Override
    public List<LdndForTaskViewModel> findAllLdndTaskByAircraftId(Long aircraftId) {
        return ldndRepository.findAllLdndTaskByAircraftId(aircraftId);
    }

    @Override
    public List<Ldnd> getAllLdndByDomainIdIn(Set<Long> ldndIds, boolean isActive) {
        return ldndRepository.findAllByIdInAndIsActive(ldndIds, isActive);
    }

    @Override
    public LdndViewModel getCalculatedLdnd(TaskDoneDto dto) {
        Ldnd ldnd = mapToLdndEntity(dto, new Ldnd());

        if (!ldnd.getIsApuControl() || (Objects.nonNull(ldnd.getAircraft().getTotalApuHours())
                && ldnd.getAircraft().getTotalApuHours() >= 0)) {
            ldnd = calculateLdnd(ldnd);
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.APU_NOT_AVAILABLE_FOR_THIS_AIRCRAFT, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return LdndViewModel.builder()
                .dueCycle(ldnd.getDueCycle())
                .dueHour(ldnd.getDueHour())
                .dueDate(ldnd.getDueDate())
                .remainingCycle(ldnd.getRemainingCycle())
                .remainingHour(ldnd.getRemainingHour())
                .remainingDay(ldnd.getRemainingDay())
                .estimatedDueDate(ldnd.getEstimatedDueDate())
                .build();
    }

    @Override
    public void updateWithAmlFlightData(Aircraft aircraft, AmlFlightData amlFlightData, LocalDate amlDate) {
        List<Ldnd> exLdndList = this.findLdndListByAircraftId(aircraft.getId());
        if (CollectionUtils.isNotEmpty(exLdndList)) {
            List<Ldnd> updatedLdndList = new ArrayList<>();
            exLdndList.forEach(ldnd -> {
                Ldnd calculatedLdnd;
                if (ldnd.getIsApuControl()) {
                    calculatedLdnd = this.calculateRemainForApu(ldnd, amlFlightData, amlDate);
                    calculatedLdnd = this.calculateEstimatedDateForApu(calculatedLdnd, aircraft, amlDate);
                } else {
                    calculatedLdnd = this.calculateRemainForFlight(ldnd, amlFlightData, amlDate);
                    calculatedLdnd = this.calculateEstimatedDateForFlight(calculatedLdnd, aircraft, amlDate);
                }

                updatedLdndList.add(calculatedLdnd);
            });
            ldndRepository.saveAll(updatedLdndList);
        }
    }

    /**
     * reponsible for updating ldnd from man hour report
     *
     * @param manHourReportDto {@link ManHourReportDto}
     */
    @Override
    public void updateLdndFromManHourReport(ManHourReportDto manHourReportDto) {
        Optional<Ldnd> ldndOptional = ldndRepository.findByIdAndIsActiveTrue(manHourReportDto.getLdndId());

        if (ldndOptional.isEmpty()) {
            throw new EngineeringManagementServerException(
                    ErrorId.LDND_NOT_FOUND, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        Ldnd ldnd = ldndOptional.get();
        ldnd.setNoOfMan(manHourReportDto.getNoOfMan());
        ldnd.setElapsedTime(manHourReportDto.getElapsedTime());
        ldnd.setActualManHour(manHourReportDto.getActualManHour());

        this.save(ldnd);
    }

    /**
     * used for finding ldnd info by ldnd ids
     *
     * @param ldndIds set of ldnd ids
     * @return ldnd list as view model
     */
    @Override
    public List<LdndViewModelForForecast> findAllLdndByLdndIds(Set<Long> ldndIds) {
        return ldndRepository.findByIdIn(ldndIds);
    }

    @Override
    @Async
    @Transactional
    @Scheduled(initialDelayString = INITIAL_DELAY_VAL, fixedDelayString = FIXED_DELAY_VAL)
    public void processAndUpdateLdndRemainingValueCalculation() {

        List<Ldnd> updatedLdndList = new ArrayList<>();
        List<Ldnd> ldndList = ldndRepository.findAll();
        ldndList.forEach(ldnd -> {
            Ldnd singleLdnd = new Ldnd();
            singleLdnd = calculateScheduledRemainingDay(ldnd);
            updatedLdndList.add(singleLdnd);
        });
        ldndRepository.saveAll(updatedLdndList);
    }

    public Ldnd calculateScheduledRemainingDay(Ldnd ldnd) {

        if (Objects.nonNull(ldnd.getDueDate())) {
            ldnd.setRemainingDay(getRemainDay(ldnd, null));
        }
        return ldnd;
    }

}
