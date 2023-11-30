package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.dto.FlyingDayFlyingHourDto;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftCheckRepository;
import com.digigate.engineeringmanagement.planning.service.impl.TaskServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * AircraftCheck Service
 *
 * @author Ashraful
 */
@Service
public class AircraftCheckService extends AbstractSearchService<AircraftCheck, AircraftCheckDto,
        AircraftCheckSearchDto> {

    private final CheckService checkService;
    private final AircraftModelService aircraftModelService;
    private final AircraftCheckRepository aircraftCheckRepository;
    private static final String AIRCRAFT_MODEL_ID = "aircraftModelId";
    private static final String AC_CHECK_IS_ACTIVE = "isActive";
    private final TaskServiceImpl taskService;
    private final LdndService ldndService;

    /**
     * Autowired constructor
     * @param repository           {@link AbstractRepository}
     * @param checkService         {@link CheckService}
     * @param aircraftModelService {@link AircraftModelService}
     * @param aircraftCheckRepository {@link AircraftCheckRepository}
     * @param taskService   {@link TaskServiceImpl}
     * @param ldndService   {@link LdndService}
     */
    public AircraftCheckService(AbstractRepository<AircraftCheck> repository,
                                CheckService checkService, AircraftModelService aircraftModelService,
                                AircraftCheckRepository aircraftCheckRepository, TaskServiceImpl taskService,
                                LdndService ldndService) {
        super(repository);
        this.checkService = checkService;
        this.aircraftModelService = aircraftModelService;
        this.aircraftCheckRepository = aircraftCheckRepository;
        this.taskService = taskService;
        this.ldndService = ldndService;
    }

    @Override
    protected Specification<AircraftCheck> buildSpecification(AircraftCheckSearchDto searchDto) {
        CustomSpecification<AircraftCheck> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getAircraftModelId(),
                AIRCRAFT_MODEL_ID).and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(),
                AC_CHECK_IS_ACTIVE)));
    }

    @Override
    protected AircraftCheckViewModel convertToResponseDto(AircraftCheck aircraftCheck) {
        Set<TaskViewModelForAcCheck> taskViewModelForAcCheckHashSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(aircraftCheck.getAircraftCheckTasks())) {

            aircraftCheck.getAircraftCheckTasks().forEach(task ->
            {
                TaskViewModelForAcCheck taskViewModelForAcCheck = new TaskViewModelForAcCheck();
                taskViewModelForAcCheck.setTaskId(task.getId());
                taskViewModelForAcCheck.setTaskNo(task.getTaskNo());
                taskViewModelForAcCheckHashSet.add(taskViewModelForAcCheck);
            });
        }

        return AircraftCheckViewModel.builder()
                .id(aircraftCheck.getId())
                .isActive(aircraftCheck.getIsActive())
                .aircraftModelId(aircraftCheck.getAircraftModelId())
                .aircraftModelName(Objects.nonNull(aircraftCheck.getAircraftModel()) ? aircraftCheck
                        .getAircraftModel().getAircraftModelName() : null)
                .checkId(aircraftCheck.getCheckId())
                .checkTitle(Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck
                        .getCheck().getTitle() : null)
                .checkDescription(Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck
                        .getCheck().getDescription() : null)
                .flyingDay(aircraftCheck.getFlyingDay())
                .flyingHour(aircraftCheck.getFlyingHour())
                .aircraftCheckTasks(taskViewModelForAcCheckHashSet)
                .build();
    }

    @Override
    protected AircraftCheck convertToEntity(AircraftCheckDto aircraftCheckDto) {
        return saveOrUpdate(aircraftCheckDto, new AircraftCheck(), false);

    }

    /**
     * convert dto to entity for save/update purpose
     *
     * @param aircraftCheckDto {@link AircraftCheckDto}
     * @param aircraftCheck   {@link AircraftCheck}
     * @param isUpdatable      {@link Boolean}
     * @return aircraftCheck   {@link AircraftCheck}
     */
    private AircraftCheck saveOrUpdate(AircraftCheckDto aircraftCheckDto, AircraftCheck aircraftCheck,
                                       Boolean isUpdatable) {
        validAircraftCheck(aircraftCheck, aircraftCheckDto, isUpdatable);
        aircraftCheck.setCheck(checkService.findById(aircraftCheckDto.getCheckId()));
        aircraftCheck.setAircraftModel(aircraftModelService.findById(aircraftCheckDto.getAircraftModelId()));
        aircraftCheck.setFlyingDay(aircraftCheckDto.getFlyingDay());
        aircraftCheck.setFlyingHour(aircraftCheckDto.getFlyingHour());
        if (CollectionUtils.isNotEmpty(aircraftCheck.getAircraftCheckTasks())) {
            aircraftCheck.getAircraftCheckTasks().clear();
        }

        if (CollectionUtils.isNotEmpty(aircraftCheckDto.getTaskIds())) {
            List<Task> taskList = taskService.getAllByDomainIdIn(aircraftCheckDto.getTaskIds(), true);
            if (aircraftCheckDto.getTaskIds().size() != taskList.size()) {
                throw EngineeringManagementServerException.notFound(ErrorId.TASK_NOT_FOUND);
            }
            aircraftCheck.setAircraftCheckTasks(new HashSet<>(taskList));
        }
        return aircraftCheck;
    }

    /**
     * This method is responsible for check composite key
     *
     * @param aircraftCheckDto {@link AircraftCheckDto}
     */
    private void validAircraftCheck(AircraftCheck aircraftCheck, AircraftCheckDto aircraftCheckDto,
                                    Boolean isUpdatable) {
        if (!isUpdatable) {
            if (aircraftCheckRepository.findByCheckIdAndAircraftModelId(aircraftCheckDto.getCheckId(),
                    aircraftCheckDto.getAircraftModelId()).isPresent()) {
                throw new EngineeringManagementServerException(
                        ErrorId.AC_CHECK_ALREADY_EXIST, HttpStatus.UNPROCESSABLE_ENTITY,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
        } else {
            if (!aircraftCheck.getCheckId().equals(aircraftCheckDto.getCheckId()) || !aircraftCheck.getAircraftModelId()
                    .equals(aircraftCheckDto.getAircraftModelId())) {
                if (aircraftCheckRepository.findByCheckIdAndAircraftModelId(aircraftCheckDto.getCheckId(),
                        aircraftCheckDto.getAircraftModelId()).isPresent()) {
                    throw new EngineeringManagementServerException(
                            ErrorId.AC_CHECK_ALREADY_EXIST, HttpStatus.UNPROCESSABLE_ENTITY,
                            MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }
        }
    }

    @Override
    protected AircraftCheck updateEntity(AircraftCheckDto dto, AircraftCheck aircraftCheck) {
        return saveOrUpdate(dto, aircraftCheck, true);
    }

    /**
     * This method responsible for Task and AcCheck related data vai AcModelId
     *
     * @param acModelId     {@link Long}
     * @param hour {@link Double}
     * @param day  {@link Integer}
     * @return taskAndAcCheckViewModel {@link TaskAndAcCheckViewModel}
     */
    public TaskAndAcCheckViewModel findAllByAcModelId(Long acModelId, Double hour, Integer day) {
        List<TaskViewModelForAcCheck> taskViewModelForAcCheckList = this.taskService
                .findAllTaskByAircraftModelId(acModelId, hour, day);
        TaskAndAcCheckViewModel taskAndAcCheckViewModel = new TaskAndAcCheckViewModel();
        taskAndAcCheckViewModel.setTaskViewModelForAcCheckList(taskViewModelForAcCheckList);
        return taskAndAcCheckViewModel;
    }
    /**
     * This method responsible for find All AircraftCheck by aircraftId
     *
     * @param aircraftId {@link Long}
     * @return aircraftCheckForAircraftViewModel {@link AircraftCheckForAircraftViewModel}
     */
    public List<AircraftCheckForAircraftViewModel> findAllAircraftCheckByAircraft(Long aircraftId) {
        return  aircraftCheckRepository.findAllAircraftCheckByAircraft(aircraftId);
    }

    /**
     * This method is responsible for find All LdndTask ByAcCheck Id In
     *
     * @param acCheckIds {@link Long}
     * @param aircraftId
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    public List<LdndForTaskViewModel> findAllLdndTaskByAcCheckIdIn(Set<Long> acCheckIds, Long aircraftId)
    {
        checkAcCheckValidity(acCheckIds);
        Set<Long> taskIds = new HashSet<>();
        List<AircraftCheck> aircraftCheckSet = aircraftCheckRepository.findAllByIdIn(acCheckIds);
        aircraftCheckSet.forEach(
                aircraftCheck -> aircraftCheck.getAircraftCheckTasks().forEach(
                        task -> taskIds.add(task.getId())
                )
        );
        List<LdndForTaskViewModel> ldndForTaskViewModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskIds)) {
            ldndForTaskViewModelList = ldndService.findAllLdndTaskByTaskIdIn(taskIds, aircraftId);
        }
        return ldndForTaskViewModelList;
    }
    /**
     * This method is responsible for check AcCheckValidity
     * @param acCheckIds {@link Long}
     */
    private void checkAcCheckValidity(Set<Long> acCheckIds) {

        List<FlyingDayFlyingHourDto> flyingDayFlyingHourDtoList = aircraftCheckRepository.
                findAllFlyingHourAndFlyingDayByAcCheckIdsIn(acCheckIds);

        if(flyingDayFlyingHourDtoList.size() != acCheckIds.size()) {
            throw new EngineeringManagementServerException(
                    ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        if (flyingDayFlyingHourDtoList.size() == 1) {
            return;
        }

        List<Double> flyingHourList = flyingDayFlyingHourDtoList.stream()
                .map(FlyingDayFlyingHourDto::getFlyingHour)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        List<Long> flyingDayList = flyingDayFlyingHourDtoList.stream()
                .map(FlyingDayFlyingHourDto::getFlyingDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<FlyingDayFlyingHourDto> sortedFlyingDayFlyingHour;

        if (CollectionUtils.isNotEmpty(flyingHourList) && CollectionUtils.isNotEmpty(flyingDayList)) {
            if (flyingHourList.size() != acCheckIds.size() || flyingDayList.size() != acCheckIds.size()) {
                throw new EngineeringManagementServerException(
                        ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }

            sortedFlyingDayFlyingHour = flyingDayFlyingHourDtoList.stream()
                    .sorted(Comparator.comparingDouble(FlyingDayFlyingHourDto::getFlyingHour))
                    .collect(Collectors.toList());

            for (int flyingDayHour = 0; flyingDayHour < sortedFlyingDayFlyingHour.size() - 1; flyingDayHour++) {
                FlyingDayFlyingHourDto flyingDayFlyingHourDto = sortedFlyingDayFlyingHour.get(flyingDayHour);
                for (int innerFlyingDayHour = flyingDayHour + 1; innerFlyingDayHour < sortedFlyingDayFlyingHour.size();
                     innerFlyingDayHour++) {
                    FlyingDayFlyingHourDto innerDto = sortedFlyingDayFlyingHour.get(innerFlyingDayHour);

                    long hourQuotient = (long) (innerDto.getFlyingHour() / flyingDayFlyingHourDto.getFlyingHour());
                    long dayQuotient =  innerDto.getFlyingDay() / flyingDayFlyingHourDto.getFlyingDay();

                    long hourRemainder = (long) (innerDto.getFlyingHour() % flyingDayFlyingHourDto.getFlyingHour());
                    long dayRemainder =  innerDto.getFlyingDay() % flyingDayFlyingHourDto.getFlyingDay();

                    if ((hourQuotient != dayQuotient) || (hourRemainder != 0 || dayRemainder != 0)) {
                        throw new EngineeringManagementServerException(
                                ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                                MDC.get(ApplicationConstant.TRACE_ID));
                    }
                }
            }

        } else if (CollectionUtils.isNotEmpty(flyingHourList)) {
            if (flyingHourList.size() != acCheckIds.size()) {
                throw new EngineeringManagementServerException(
                        ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }

            sortedFlyingDayFlyingHour = flyingDayFlyingHourDtoList.stream()
                    .sorted(Comparator.comparingDouble(FlyingDayFlyingHourDto::getFlyingHour))
                    .collect(Collectors.toList());

            Double maxHour = sortedFlyingDayFlyingHour.get(sortedFlyingDayFlyingHour.size() - 1).getFlyingHour();

            for (FlyingDayFlyingHourDto flyingDayFlyingHourDto : sortedFlyingDayFlyingHour) {
                long hourRemainder = (long) (maxHour % flyingDayFlyingHourDto.getFlyingHour());

                if (hourRemainder != 0) {
                    throw new EngineeringManagementServerException(
                            ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID));
                }
            }
        } else if (CollectionUtils.isNotEmpty(flyingDayList)) {
            if (flyingDayList.size() != acCheckIds.size()) {
                throw new EngineeringManagementServerException(
                        ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }

            sortedFlyingDayFlyingHour = flyingDayFlyingHourDtoList.stream()
                    .sorted(Comparator.comparingLong(FlyingDayFlyingHourDto::getFlyingDay))
                    .collect(Collectors.toList());

            Long maxDay = sortedFlyingDayFlyingHour.get(sortedFlyingDayFlyingHour.size() - 1).getFlyingDay();

            for (FlyingDayFlyingHourDto flyingDayFlyingHourDto : sortedFlyingDayFlyingHour) {
                long dayRemainder =  maxDay % flyingDayFlyingHourDto.getFlyingDay();

                if (dayRemainder != 0) {
                    throw new EngineeringManagementServerException(
                            ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID));
                }
            }
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.AC_CHECK_LIST_ARE_NOT_VALID, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    /**
     *  This method is responsible for find all Ldnd Task by aircraft
     *
     * @param aircraftId {@link Long}
     * @return ldndForTaskViewModel {@link LdndForTaskViewModel}
     */
    public List<LdndForTaskViewModel>findAllLdndTaskByAircraftId(Long aircraftId)
    {
        return ldndService.findAllLdndTaskByAircraftId(aircraftId);
    }
}

