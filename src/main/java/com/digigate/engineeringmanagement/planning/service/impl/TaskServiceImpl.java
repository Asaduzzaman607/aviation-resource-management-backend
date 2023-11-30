package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.*;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.LdndRepository;
import com.digigate.engineeringmanagement.planning.repository.TaskRepository;
import com.digigate.engineeringmanagement.planning.service.IModelService;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.TaskService;
import com.digigate.engineeringmanagement.planning.service.TaskTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.planning.constant.TaskConstant.*;

/**
 * Task service implementation
 *
 * @author Pranoy Das
 */
@Service
public class TaskServiceImpl extends AbstractSearchService<Task, TaskDto, TaskSearchDto> implements TaskService {
    private static final String TASK_NO = "taskNo";

    private static final String MODEL_NAME = "modelName";
    private static final String MODEL_ENTITY = "model";
    private static final String AIRCRAFT_MODEL_ID = "aircraftModelId";
    private static final String ALL = "ALL";
    private static final String QUANTITY = "Quantity";
    private static final String NULL = "null";
    private static final String COLON_SEPARATOR = ":";
    private final TaskRepository taskRepository;
    private final IModelService modelService;
    private final AircraftService aircraftService;
    private final AircraftModelService aircraftModelService;
    private final PositionServiceImpl positionService;
    private final AircraftEffectivityServiceImpl aircraftEffectivityService;
    private final TaskTypeService taskTypeService;
    private final PartService partService;
    private final Environment environment;
    private final ModelTreeService modelTreeService;
    private final LdndRepository ldndRepository;

    /**
     * Autowired Constructor
     *
     * @param repository                 {@link AbstractRepository<Task>}
     * @param taskRepository             {@link TaskRepository}
     * @param modelService               {@link IModelService}
     * @param aircraftService            {@link AircraftService}
     * @param aircraftModelService       {@link AircraftModelService}
     * @param positionService            {@link PositionServiceImpl}
     * @param aircraftEffectivityService {@link AircraftEffectivityServiceImpl}
     * @param taskTypeService            {@link TaskTypeService}
     * @param partService                {@link PartService}
     * @param environment                {@link Environment}
     * @param modelTreeService           {@link ModelTreeService}
     * @param ldndRepository
     */
    @Autowired
    public TaskServiceImpl(AbstractRepository<Task> repository, TaskRepository taskRepository,
                           IModelService modelService, AircraftService aircraftService,
                           AircraftModelService aircraftModelService,
                           PositionServiceImpl positionService,
                           @Lazy AircraftEffectivityServiceImpl aircraftEffectivityService,
                           TaskTypeService taskTypeService, PartService partService, Environment environment,
                           ModelTreeService modelTreeService, LdndRepository ldndRepository) {
        super(repository);
        this.taskRepository = taskRepository;
        this.modelService = modelService;
        this.aircraftService = aircraftService;
        this.aircraftModelService = aircraftModelService;
        this.positionService = positionService;
        this.aircraftEffectivityService = aircraftEffectivityService;
        this.taskTypeService = taskTypeService;
        this.partService = partService;
        this.environment = environment;
        this.modelTreeService = modelTreeService;
        this.ldndRepository = ldndRepository;
    }

    /**
     * responsible for creating new task
     *
     * @param taskDto {@link TaskDto}
     * @return task entity {@link Task}
     */
    @Transactional
    @Override
    public Task create(TaskDto taskDto) {
        return super.saveItem(convertToEntity(taskDto));
    }

    /**
     * responsible for updating task
     *
     * @param taskDto {@link TaskDto}
     * @param id      id of a the task
     * @return task entity {@link Task}
     */
    @Transactional
    @Override
    public Task update(TaskDto taskDto, Long id) {
        Task task = super.findById(id);
        return super.saveItem(updateEntity(taskDto, task));
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        Task task = findByIdUnfiltered(id);
        if (task.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        task.setIsActive(isActive);
        saveItem(task);

        if(isActive.equals(false)) {
            ldndRepository.makeInActiveByTaskId(task.getId());
        }
    }

    /**
     * task search service
     *
     * @param searchDto {@link TaskSearchDto}
     * @return entity           {@link Specification<Task>}
     */
    @Override
    protected Specification<Task> buildSpecification(TaskSearchDto searchDto) {
        CustomSpecification<Task> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getTaskNo(), TASK_NO)
                .and(customSpecification.likeAllSpecificationAtChild(searchDto.getModelName(), MODEL_ENTITY, MODEL_NAME))
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getAircraftModelId(), AIRCRAFT_MODEL_ID))
        );
    }

    /**
     * entity to view model converter method
     *
     * @param task {@link  Task}
     * @return taskViewModel        {@link  TaskViewModel}
     */
    @Override
    protected TaskViewModel convertToResponseDto(Task task) {

        Set<AircraftEffectivity> aircraftEffectivitySet = task.getAircraftEffectivitySet();
        Set<EffectiveAircraftViewModel> effectiveAircraftViewModels = new HashSet<>();

        if (CollectionUtils.isNotEmpty(aircraftEffectivitySet)) {
            aircraftEffectivitySet.forEach(aircraftEffectivity -> effectiveAircraftViewModels.add(
                    EffectiveAircraftViewModel.builder()
                            .effectiveAircraftId(aircraftEffectivity.getId())
                            .aircraftId(aircraftEffectivity.getAircraftId())
                            .aircraftName(aircraftEffectivity.getAircraft().getAircraftName())
                            .effectivityType(aircraftEffectivity.getEffectivityType())
                            .remark(aircraftEffectivity.getRemark())
                            .build()
            ));
        }


        Set<TaskProcedure> taskProcedureSet = task.getTaskProcedureSet();
        Set<TaskProcedureViewModel> taskProcedureViewModels = new HashSet<>();

        if (CollectionUtils.isNotEmpty(taskProcedureSet)) {
            taskProcedureSet.forEach(taskProcedure ->
                    taskProcedureViewModels.add(
                            TaskProcedureViewModel.builder()
                                    .taskProcedureId(Objects.nonNull(taskProcedure.getId())
                                            ? taskProcedure.getId() : null)
                                    .positionId(Objects.nonNull(taskProcedure.getPosition())
                                            ? taskProcedure.getPosition().getId() : null)
                                    .jobProcedure(taskProcedure.getJobProcedure())
                                    .name(Objects.nonNull(taskProcedure.getPosition())
                                            ? taskProcedure.getPosition().getName() : null)
                                    .build()
                    ));
        }

        Set<TaskConsumablePart> taskConsumablePartSet = task.getTaskConsumablePartSet();
        Set<TaskConsumablePartViewModel> taskConsumablePartViewModels = new HashSet<>();

        if (CollectionUtils.isNotEmpty(taskConsumablePartSet)) {
            taskConsumablePartSet.forEach(taskConsumablePart ->
                    taskConsumablePartViewModels.add(
                            TaskConsumablePartViewModel.builder()
                                    .taskConsumablePartId(taskConsumablePart.getId())
                                    .consumablePartId(taskConsumablePart.getPartId())
                                    .partNo(Objects.nonNull(taskConsumablePart.getPart())
                                            ? taskConsumablePart.getPart().getPartNo() : null)
                                    .quantity(taskConsumablePart.getQuantity())
                                    .build()
                    ));
        }

        TaskType taskType = new TaskType();
        if (Objects.nonNull(task.getTaskType())) {
            taskType = task.getTaskType();
        }

        return TaskViewModel.builder()
                .taskId(task.getId())
                .aircraftModelId(task.getAircraftModelId())
                .aircraftModelName(Objects.nonNull(task.getAircraftModel()) ? task.getAircraftModel()
                        .getAircraftModelName() : null)
                .modelId(task.getModelId())
                .modelName(task.getModel().getModelName())
                .taskSource(task.getTaskSource())
                .taskNo(task.getTaskNo())
                .taskTypeId(taskType.getId())
                .taskTypeName(taskType.getName())
                .repeatType(task.getRepetitiveType().getIntervalEnum())
                .description(task.getDescription())
                .comment(task.getComment())
                .manHours(task.getManHours())
                .sources(task.getSources())
                .status(task.getTaskStatus().getTaskStatusType())
                .intervalCycle(task.getIntervalCycle())
                .intervalHour(task.getIntervalHour())
                .intervalDay(task.getIntervalDay())
                .thresholdCycle(task.getThresholdCycle())
                .thresholdHour(task.getThresholdHour())
                .thresholdDay(task.getThresholdDay())
                .trade(task.getTrade())
                .effectiveDate(task.getEffectiveDate())
                .issueDate(task.getIssueDate())
                .revisionNumber(task.getRevisionNumber())
                .effectiveAircraftViewModels(effectiveAircraftViewModels)
                .taskProcedureViewModels(taskProcedureViewModels)
                .taskConsumablePartViewModels(taskConsumablePartViewModels)
                .isApuControl(task.getIsApuControl())
                .isActive(task.getIsActive())
                .build();
    }


    @Override
    protected Task convertToEntity(TaskDto taskDto) {
        return saveOrUpdate(taskDto, new Task(), false);
    }

    @Override
    protected Task updateEntity(TaskDto dto, Task entity) {
        return saveOrUpdate(dto, entity, true);
    }

    /**
     * convert dto to entity for save/update purpose
     *
     * @param taskDto {@link  TaskDto}
     * @param task    {@link  Task}
     * @return task                 {@link Task}
     */
    private Task saveOrUpdate(TaskDto taskDto, Task task, Boolean isUpdatable) {
        AircraftModel aircraftModel = aircraftModelService.findById(taskDto.getAircraftModelId());

        task.setAircraftModel(aircraftModel);

        Model model = modelService.findById(taskDto.getModelId());
        task.setModel(model);


        if (isValidTaskNo(taskDto.getTaskNo(), isUpdatable, task)) {
            task.setTaskNo(taskDto.getTaskNo());
        }

        task.setTaskSource(taskDto.getTaskSource());
        task.setRepetitiveType(taskDto.getRepeatType());
        task.setDescription(taskDto.getDescription());
        task.setComment(taskDto.getComment());
        task.setManHours(taskDto.getManHours());
        task.setSources(taskDto.getSources());
        task.setIsApuControl(taskDto.getIsApuControl());
        task.setThresholdCycle(taskDto.getThresholdCycle());
        task.setThresholdHour(taskDto.getThresholdHour());
        task.setThresholdDay(taskDto.getThresholdDay());
        task.setIntervalCycle(taskDto.getIntervalCycle());
        task.setIntervalHour(taskDto.getIntervalHour());
        task.setIntervalDay(taskDto.getIntervalDay());
        if (Objects.nonNull(taskDto.getTaskTypeId())) {
            Optional<TaskType> taskType = taskTypeService.findOptionalById(taskDto.getTaskTypeId(),
                    true);
            taskType.ifPresent(task::setTaskType);
        } else {
            task.setTaskType(null);
        }
        task.setTrade(taskDto.getTrade());
        task.setEffectiveDate(taskDto.getEffectiveDate());
        task.setIssueDate(taskDto.getIssueDate());
        task.setRevisionNumber(taskDto.getRevisionNumber());
        prepareStatus(taskDto.getRepeatType(), taskDto.getStatus(), task);
        prepareEffectiveAircraft(taskDto.getEffectiveAircraftDtoList(), task);
        prepareTaskProcedure(taskDto.getTaskProcedureDtoList(), task, isUpdatable);
        prepareTaskConsumablePart(taskDto.getTaskConsumablePartDtoList(), task, isUpdatable);

        return task;
    }

    private void prepareStatus(RepetitiveTypeEnum repetitiveType, TaskStatusEnum taskStatus, Task task) {
        if (repetitiveType.equals(RepetitiveTypeEnum.REPETITIVE)) {
            if (Objects.isNull(taskStatus)) {
                task.setTaskStatus(TaskStatusEnum.REP);
            } else {
                task.setTaskStatus(taskStatus);
            }
        } else {
            task.setTaskStatus(taskStatus);
        }
    }

    private void prepareTaskProcedure(List<TaskProcedureDto> taskProcedureDtoList, Task task, Boolean isUpdatable) {

        Set<TaskProcedure> taskProcedureSet = task.getTaskProcedureSet();
        if (CollectionUtils.isNotEmpty(taskProcedureDtoList)) {
            if (CollectionUtils.isEmpty(taskProcedureSet)) {
                taskProcedureSet = new HashSet<>();
            }

            Map<Long, TaskProcedure> taskProcedureMap = taskProcedureSet.stream()
                    .collect(Collectors.toMap(TaskProcedure::getId, taskProcedure -> taskProcedure));
            Set<Long> procedureIds = new HashSet<>();

            Set<Long> positionIds = taskProcedureDtoList.stream().map(TaskProcedureDto::getPositionId)
                    .collect(Collectors.toSet());
            List<Position> positionList = positionService.getAllByDomainIdInUnfiltered(positionIds);

            Map<Long, Position> positionMap =
                    positionList.stream().collect(Collectors.toMap(Position::getId, Function.identity()));

            for (TaskProcedureDto taskProcedureDto : taskProcedureDtoList) {
                if (Objects.isNull(taskProcedureDto.getPositionId())
                        && StringUtils.isBlank(taskProcedureDto.getJobProcedure())) {
                    continue;
                }

                TaskProcedure taskProcedure;
                if (Objects.nonNull(taskProcedureDto.getTaskProcedureId())
                        && taskProcedureMap.containsKey(taskProcedureDto.getTaskProcedureId())) {
                    taskProcedure = taskProcedureMap.get(taskProcedureDto.getTaskProcedureId());
                    procedureIds.add(taskProcedureDto.getTaskProcedureId());
                } else {
                    taskProcedure = new TaskProcedure();
                }
                taskProcedure.setPosition(positionMap.get(taskProcedureDto.getPositionId()));
                taskProcedure.setTask(task);
                taskProcedure.setJobProcedure(taskProcedureDto.getJobProcedure());
                task.addTaskProcedure(taskProcedure);
            }

            if (isUpdatable && CollectionUtils.isNotEmpty(task.getTaskProcedureSet())) {
                task.getTaskProcedureSet().removeIf(taskProcedure -> Objects.nonNull(taskProcedure.getId())
                        && !procedureIds.contains(taskProcedure.getId()));
            }

        } else {
            if (CollectionUtils.isNotEmpty(taskProcedureSet)) {
                taskProcedureSet.clear();
            }
        }

    }

    private void prepareTaskConsumablePart(List<TaskConsumablePartDto> taskConsumablePartDtoList, Task task,
                                           Boolean isUpdatable) {
        Set<TaskConsumablePart> taskConsumablePartSet = task.getTaskConsumablePartSet();

        if (CollectionUtils.isNotEmpty(taskConsumablePartDtoList)) {
            if (CollectionUtils.isEmpty(taskConsumablePartSet)) {
                taskConsumablePartSet = new HashSet<>();
            }

            Map<Long, TaskConsumablePart> taskConsumablePartMap = taskConsumablePartSet.stream()
                    .collect(Collectors.toMap(TaskConsumablePart::getId, taskConsumablePart -> taskConsumablePart));

            Set<Long> consumablePartIds = taskConsumablePartDtoList.stream()
                    .map(TaskConsumablePartDto::getConsumablePartId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<Part> consumablePartList = partService.getAllByDomainIdIn(consumablePartIds, true);

            if (consumablePartIds.size() != consumablePartList.size()) {
                throw new EngineeringManagementServerException(
                        ErrorId.CONSUMABLE_PART_NOT_FOUND, HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }

            Map<Long, Part> consumablePartMap =
                    consumablePartList.stream().collect(Collectors.toMap(Part::getId, Function.identity()));

            Set<Long> taskConsumablePartIds = new HashSet<>();
            taskConsumablePartDtoList.forEach(taskConsumablePartDto -> {
                        TaskConsumablePart taskConsumablePart;

                        if (Objects.nonNull(taskConsumablePartDto.getTaskConsumablePartId())
                                && taskConsumablePartMap.containsKey(taskConsumablePartDto.getConsumablePartId())) {
                            taskConsumablePart = taskConsumablePartMap
                                    .get(taskConsumablePartDto.getTaskConsumablePartId());
                            taskConsumablePartIds.add(taskConsumablePartDto.getTaskConsumablePartId());
                        } else {
                            taskConsumablePart = new TaskConsumablePart();
                        }

                        taskConsumablePart.setPart(consumablePartMap
                                .get(taskConsumablePartDto.getConsumablePartId()));
                        taskConsumablePart.setTask(task);
                        taskConsumablePart.setQuantity(taskConsumablePartDto.getQuantity());
                        task.addTaskConsumablePart(taskConsumablePart);
                    }
            );
            if (isUpdatable && CollectionUtils.isNotEmpty(task.getTaskConsumablePartSet())) {
                task.getTaskConsumablePartSet().removeIf(taskConsumablePart -> Objects
                        .nonNull(taskConsumablePart.getId())
                        && !taskConsumablePartIds.contains(taskConsumablePart.getId()));
            }
        } else {
            if (CollectionUtils.isNotEmpty(taskConsumablePartSet)) {
                taskConsumablePartSet.clear();
            }
        }
    }

    private void prepareEffectiveAircraft(List<EffectiveAircraftDto> effectiveAircraftDtoList, Task task) {
        if (CollectionUtils.isNotEmpty(effectiveAircraftDtoList)) {
            Set<Long> aircraftIds =
                    effectiveAircraftDtoList.stream()
                            .map(EffectiveAircraftDto::getAircraftId)
                            .collect(Collectors.toSet());

            List<Aircraft> aircraftList = aircraftService.getAllByDomainIdIn(aircraftIds, true);

            Map<Long, Aircraft> aircraftMap = aircraftList.stream()
                    .collect(Collectors.toMap(Aircraft::getId, aircraft -> aircraft));


            Set<AircraftEffectivity> aircraftEffectivitySet = task.getAircraftEffectivitySet();
            Map<Long, AircraftEffectivity> aircraftEffectivityMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(aircraftEffectivitySet)) {
                aircraftEffectivitySet.forEach(aircraftEffectivity ->
                        aircraftEffectivityMap.put(aircraftEffectivity.getId(), aircraftEffectivity));
            }

            effectiveAircraftDtoList.forEach(effectiveAircraftDto -> {
                AircraftEffectivity aircraftEffectivity;

                if (Objects.nonNull(effectiveAircraftDto.getEffectiveAircraftId())
                        && aircraftEffectivityMap.containsKey(effectiveAircraftDto.getEffectiveAircraftId())) {
                    aircraftEffectivity = aircraftEffectivityMap.get(effectiveAircraftDto.getEffectiveAircraftId());
                } else {
                    aircraftEffectivity = new AircraftEffectivity();
                }

                aircraftEffectivity.setAircraft(aircraftMap.get(effectiveAircraftDto.getAircraftId()));
                aircraftEffectivity.setTask(task);
                aircraftEffectivity.setRemark(effectiveAircraftDto.getRemark());
                aircraftEffectivity.setEffectivityType(effectiveAircraftDto.getEffectivityType());
                task.addEffectiveAircraft(aircraftEffectivity);
            });
        }
    }


    private boolean isValidTaskNo(String taskNo, Boolean isUpdatable, Task task) {
        if (isUpdatable && taskNo.equals(task.getTaskNo())) {
            return false;
        }
        Optional<Long> taskOptional = taskRepository.findTaskByTaskNo(taskNo);

        if (taskOptional.isPresent()) {
            throw new EngineeringManagementServerException(ErrorId.TASK_NO_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return true;
    }


    /**
     * find all task by aircraft model id
     *
     * @param aircraftModelId {@link Long}
     * @return {@link List<TaskViewModel>}
     */
    @Override
    public List<TaskViewModel> findTaskByAircraftModelId(Long aircraftModelId) {
        return taskRepository.findTasksByAircraftModelId(aircraftModelId);
    }

    /**
     * This method will save Aircraft Specific Task
     *
     * @param aircraftEffectivityTaskDtoList {@link List<AircraftEffectivityTaskDto>}
     */
    @Override
    public void saveAircraftSpecificTask(List<AircraftEffectivityTaskDto> aircraftEffectivityTaskDtoList) {
        Set<Long> taskIds = aircraftEffectivityTaskDtoList.stream()
                .map(AircraftEffectivityTaskDto::getTaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Task> taskList = getAllByDomainIdIn(taskIds, true);
        Map<Long, Task> taskMap = taskList.stream()
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        Aircraft aircraft = aircraftService.findById(aircraftEffectivityTaskDtoList.get(0).getAircraftId());

        List<AircraftEffectivity> aircraftEffectivityList = new ArrayList<>();

        Set<Long> aircraftEffectivityIds = aircraftEffectivityTaskDtoList.stream()
                .map(AircraftEffectivityTaskDto::getEffectivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<AircraftEffectivity> aircraftEffectivities =
                aircraftEffectivityService.findAllAircraftEffectivityById(aircraftEffectivityIds);

        Map<Long, AircraftEffectivity> aircraftEffectivityMap = aircraftEffectivities.stream()
                .collect(Collectors.toMap(AircraftEffectivity::getId, Function.identity()));

        for (AircraftEffectivityTaskDto aircraftEffectivityTaskDto : aircraftEffectivityTaskDtoList) {
            AircraftEffectivity aircraftEffectivity;

            if (Objects.isNull(aircraftEffectivityTaskDto.getEffectivityId())) {
                aircraftEffectivity = new AircraftEffectivity();
            } else {
                aircraftEffectivity = aircraftEffectivityMap.get(aircraftEffectivityTaskDto.getEffectivityId());
            }

            AircraftEffectivity updatedAircraftEffectivity = aircraftEffectivityService.
                    convertRequestDtoModelToEntity(aircraftEffectivityTaskDto,
                            aircraftEffectivity, aircraft, taskMap);

            aircraftEffectivityList.add(updatedAircraftEffectivity);
        }

        try {
            aircraftEffectivityService.saveItemList(aircraftEffectivityList);
        } catch (Exception e) {
            throw EngineeringManagementServerException.dataSaveException(ErrorId.DATA_NOT_FOUND);
        }
    }

    /**
     * This method will find all method of aircraft model by aircraft
     *
     * @param aircraftId {@link Long}
     * @return {@link List<TaskViewModel>}
     */
    @Override
    public List<AircraftEffectivityTaskDto> getTaskListByAircraft(Long aircraftId) {
        return aircraftEffectivityService.findAllAircraftEffectivityTaskDtoByAircraftId(aircraftId);
    }

    /**
     * This method will find all Task  by aircraftModelId
     *
     * @param acModelId     {@link Long}
     * @return taskViewModelForAcCheck {@link TaskViewModelForAcCheck}
     */
    public List<TaskViewModelForAcCheck> findAllTaskByAircraftModelId(Long acModelId, Double hour,
                                                                      Integer day) {
        return taskRepository.findAllTaskByAircraftModelId(
                acModelId, TaskStatusEnum.CLOSED, hour, day);
    }

    @Override
    public List<TaskModelResponseDto> getTaskModelByAircraftModelId(Long aircraftModelId) {
        return taskRepository.getTaskModelByAircraftModelId(aircraftModelId);
    }

    /**
     * Upload excel for task
     *
     * @param file            {@link  MultipartFile}
     * @param aircraftModelId {@link  Long}
     * @return {@link ResponseEntity}
     */
    @Override
    public ExcelDataResponse uploadTaskExcel(MultipartFile file, Long aircraftModelId) {
        if (Objects.isNull(aircraftModelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }

        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_TASK), TASK);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorList = validateAndPrepareTaskEntity(excelData.getDataList(), aircraftModelId);

        if (CollectionUtils.isNotEmpty(errorList)) {
            return ExcelFileUtil.prepareErrorResponse(errorList);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareTaskEntity(List<Map<String, ?>> dataList, Long aircraftModelId) {
        List<String> errorList = new ArrayList<>();
        AircraftModel aircraftModel = aircraftModelService.findById(aircraftModelId);

        if (Objects.isNull(aircraftModel)) {
            errorList.add("No aircraft model found");
        }

        List<Model> modelList = modelService.findAllModelByAircraftModelId(aircraftModelId);
        Map<String, Model> modelMap = modelList.stream()
                .collect(Collectors.toMap(Model::getModelName, Function.identity()));

        List<TaskType> taskTypes = taskTypeService.getAllActiveTaskTypes(true);
        Map<String, TaskType> taskTypeMap = taskTypes.stream()
                .collect(Collectors.toMap(TaskType::getName, Function.identity()));

        Set<String> taskNumbers = taskRepository.findAllTaskNoByAircraftModelId(aircraftModelId);

        List<Task> taskList = new ArrayList<>();

        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            Model model = modelMap.get(dataMap.get(MODEL));

            if (Objects.isNull(model)) {
                errorList.add(String.format("Invalid Model. Model no: {%s}, at row: {%s}",
                        dataMap.get(MODEL), rowNumber));
            }

            RepetitiveTypeEnum repeatType = (RepetitiveTypeEnum) dataMap.get(REPEAT_TYPE);

            if (Objects.isNull(repeatType)) {
                errorList.add(String.format("Invalid Repetitive Type. Repetitive Type: {%s}, at row: {%s}",
                        dataMap.get(REPEAT_TYPE), rowNumber));
            }

            TaskStatusEnum status = (TaskStatusEnum) dataMap.get(STATUS);

            if (Objects.isNull(status)) {
                errorList.add(String.format("Invalid status. Task status: {%s}, at row {%s}: ",
                        dataMap.get(STATUS), rowNumber));
            }

            String taskNo = (String) dataMap.get(TASK_NUMBER);

            if (taskNumbers.contains(taskNo)) {
                errorList.add(String.format("Duplicate task no found. Task no: {%s}, at row: {%s}", taskNo, rowNumber));
            }

            TaskType taskType = taskTypeMap.get(dataMap.get(TASK_TYPE));

            if (StringUtils.isNotBlank(StringUtil.valueOf(dataMap.get(TASK_TYPE))) && Objects.isNull(taskType)) {
                errorList.add(String.format("Invalid task type. Task type: {%s}, at row: {%s}",
                        dataMap.get(TASK_TYPE), rowNumber));
            }

            Integer intervalDay = (Integer) dataMap.get(INTERVAL_DAY);
            Double intervalHour = (Double) dataMap.get(INTERVAL_HOUR);
            Integer intervalCycle = (Integer) dataMap.get(INTERVAL_CYCLE);
            if (repeatType.equals(RepetitiveTypeEnum.ONE_TIME)
                    && (Objects.nonNull(intervalDay) || Objects.nonNull(intervalHour)
                    || Objects.nonNull(intervalCycle))) {
                if (Objects.isNull(taskType)) {
                    errorList.add(String.format("One time repetitive type task can't have interval value. Row no: {%s}",
                            rowNumber));
                }
            }

            Task task = new Task();
            task.setAircraftModel(aircraftModel);
            task.setModel(model);
            task.setTaskNo(taskNo);
            task.setTaskSource((String) dataMap.get(TASK_SOURCE));
            task.setRepetitiveType(repeatType);
            task.setDescription((String) dataMap.get(DESCRIPTION));
            task.setManHours((Double) dataMap.get(MAN_HOURS));
            task.setSources((String) dataMap.get(SOURCES));
            prepareStatus(repeatType, status, task);
            task.setIsApuControl((Boolean) dataMap.get(IS_APU_CONTROL));
            task.setIntervalDay(intervalDay);
            task.setIntervalHour(intervalHour);
            task.setIntervalCycle(intervalCycle);
            task.setThresholdDay((Integer) dataMap.get(THRESHOLD_DAY));
            task.setThresholdHour((Double) dataMap.get(THRESHOLD_HOUR));
            task.setThresholdCycle((Integer) dataMap.get(THRESHOLD_CYCLE));
            task.setEffectiveDate((LocalDate) dataMap.get(EFFECTIVE_DATE));
            task.setTaskType(taskType);
            task.setTrade(new HashSet<>((List) dataMap.get(TRADE)));
            task.setComment(StringUtil.valueOf(dataMap.get(COMMENT)));

            taskList.add(task);
        }

        saveTaskList(errorList, taskList);

        return errorList;
    }

    @Override
    public ExcelDataResponse importExcelFileAircraftEffectivity(MultipartFile file, Long aircraftModelId) {
        if (Objects.isNull(aircraftModelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }

        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_AIRCRAFT_EFFECTIVITY),
                        AIRCRAFT_EFFECTIVITY);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorList = validateAircraftEffectivityAndPrepareEntity(aircraftModelId, excelData);

        if (CollectionUtils.isNotEmpty(errorList)) {
            return ExcelFileUtil.prepareErrorResponse(errorList);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAircraftEffectivityAndPrepareEntity(Long aircraftModelId, ExcelData excelData) {
        List<String> errorList = new ArrayList<>();
        List<Task> taskList = getTaskListByAircraftModel(aircraftModelId);
        Map<String, Task> taskMap = taskList.stream()
                .collect(Collectors.toMap(Task::getTaskNo, Function.identity()));

        List<Aircraft> aircraftList = aircraftService.findAllActiveAircraftByAircraftModel(aircraftModelId);
        Map<String, Aircraft> aircraftMap = aircraftList.stream()
                .collect(Collectors.toMap(Aircraft::getAircraftName, Function.identity()));

        Set<Long> taskIds = new HashSet<>();
        List<Task> tasks = new ArrayList<>();
        List<Map<String, ?>> dataList = excelData.getDataList();

        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            Set<String> unusedAircrafts = new HashSet<>(aircraftMap.keySet());
            String taskNo = (String) dataMap.get(TaskConstant.TASK_NO);
            Task task = taskMap.get(taskNo);

            if (Objects.isNull(task)) {
                errorList.add(String.format("Invalid task. Task no: {%s}, at row {%s}", taskNo, rowNumber));
                continue;
            }

            if (CollectionUtils.isNotEmpty(task.getAircraftEffectivitySet())) {
                errorList.add(String.format("Aircraft effectivity is already uploaded for specific task. " +
                        "Task no: {%s}, at row {%s}", taskNo, rowNumber));
            }

            EffectivityType effectivityType = (EffectivityType) dataMap.get(EFFECTIVITY_TYPE);

            if (Objects.isNull(effectivityType)) {
                errorList.add(String.format("Invalid Effectivity Type. Effectivity Type: {%s}, at row: {%s}",
                        dataMap.get(EFFECTIVITY_TYPE), rowNumber));
            }

            String remark = StringUtil.valueOf(dataMap.get(REMARK));

            Set<String> aircraftNames = new HashSet<>((List) dataMap.get(AIRCRAFT_NAME));

            if (aircraftNames.size() == 1 && aircraftNames.contains(ALL)) {
                prepareEffectivityForAllType(task, effectivityType, remark, aircraftList);
            } else {
                for (String aircraftName : aircraftNames) {
                    if (aircraftMap.containsKey(aircraftName)) {
                        AircraftEffectivity aircraftEffectivity = new AircraftEffectivity();
                        aircraftEffectivity.setTask(task);
                        aircraftEffectivity.setAircraft(aircraftMap.get(aircraftName));
                        aircraftEffectivity.setRemark(remark);
                        aircraftEffectivity.setEffectivityType(effectivityType);
                        task.addEffectiveAircraft(aircraftEffectivity);
                        unusedAircrafts.remove(aircraftName);
                    } else {
                        errorList.add(String.format("Invalid aircraft. Aircraft not found for specific aircraft model. "
                                        + "Aircraft: {%s}, at row: {%s}", aircraftName, rowNumber));
                    }
                }

                if (CollectionUtils.isNotEmpty(unusedAircrafts)) {
                    EffectivityType type = effectivityType.equals(EffectivityType.EFFECTIVE) ?
                            EffectivityType.NON_EFFECTIVE : EffectivityType.EFFECTIVE;
                    for (String aircraftName : unusedAircrafts) {
                        AircraftEffectivity aircraftEffectivity = new AircraftEffectivity();
                        aircraftEffectivity.setTask(task);
                        aircraftEffectivity.setAircraft(aircraftMap.get(aircraftName));
                        aircraftEffectivity.setRemark(remark);
                        aircraftEffectivity.setEffectivityType(type);
                        task.addEffectiveAircraft(aircraftEffectivity);
                    }
                }
            }

            if (taskIds.add(task.getId())) {
                tasks.add(task);
            }
        }

        saveTaskList(errorList, tasks);
        return errorList;
    }

    private void prepareEffectivityForAllType(Task task, EffectivityType effectivityType, String remark,
                                              List<Aircraft> aircraftList) {
        for (Aircraft aircraft : aircraftList) {
            AircraftEffectivity aircraftEffectivity = new AircraftEffectivity();
            aircraftEffectivity.setTask(task);
            aircraftEffectivity.setAircraft(aircraft);
            aircraftEffectivity.setRemark(remark);
            aircraftEffectivity.setEffectivityType(effectivityType);
            task.addEffectiveAircraft(aircraftEffectivity);
        }
    }


    private void saveTaskList(List<String> errorList, List<Task> tasks) {
        if (CollectionUtils.isNotEmpty(tasks) && CollectionUtils.isEmpty(errorList)) {
            try {
                taskRepository.saveAll(tasks);
            } catch (Exception ex) {
                LOGGER.error("Exception happened while saving task. Exception: {}.", ex.getMessage());
                errorList.add(String.format("Exception happened while saving task. Exception: {%s}", ex.getMessage()));
            }
        }
    }

    @Override
    public ExcelDataResponse importExcelFileTaskProcedure(MultipartFile file, Long aircraftModelId) {
        if (Objects.isNull(aircraftModelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }

        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_TASK_PROCEDURE), TASK_PROCEDURE);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorList = validatedTaskProcedureAndPrepareEntity(aircraftModelId, excelData);

        if (CollectionUtils.isNotEmpty(errorList)) {
            return ExcelFileUtil.prepareErrorResponse(errorList);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    @Override
    public ExcelDataResponse importExcelFileConsumableParts(MultipartFile file, Long aircraftModelId) {
        if (Objects.isNull(aircraftModelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }

        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_TASK_CONSUMABLE_PART),
                        TASK_CONSUMABLE_PART);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorList = validatedTaskConsumablePartsAndPrepareEntity(aircraftModelId, excelData);

        if (CollectionUtils.isNotEmpty(errorList)) {
            return ExcelFileUtil.prepareErrorResponse(errorList);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validatedTaskConsumablePartsAndPrepareEntity(Long aircraftModelId, ExcelData excelData) {
        List<String> errorList = new ArrayList<>();
        List<Task> taskList = getTaskListByAircraftModel(aircraftModelId);

        Map<String, Task> taskMap = taskList.stream()
                .collect(Collectors.toMap(Task::getTaskNo, Function.identity()));
        List<Part> partList = partService.findConsumableParts();

        Map<String, Part> partMap = partList.stream()
                .collect(Collectors.toMap(Part::getPartNo, Function.identity()));

        List<Task> tasks = new ArrayList<>();
        Set<Long> taskIds = new HashSet<>();
        List<Map<String, ?>> dataList = excelData.getDataList();


        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));

            TaskConsumablePart taskConsumablePart = new TaskConsumablePart();

            String taskNo = (String) dataMap.get(TaskConstant.TASK_NO);

            Task task = taskMap.get(taskNo);
            if (Objects.isNull(task)) {
                errorList.add(String.format("Invalid task. Task no: {%s}, at row: {%s}",
                        dataMap.get(TaskConstant.TASK_NO), rowNumber));
                continue;
            }

            if (CollectionUtils.isNotEmpty(task.getTaskConsumablePartSet())) {
                errorList.add(String.format("Consumable part is already uploaded for specific task. " +
                        "Task no: {%s}, at row {%s}", taskNo, rowNumber));
            }

            taskConsumablePart.setTask(task);

            String partNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(PART_NO)));

            if (!partMap.containsKey(partNo)) {
                errorList.add(String.format("Invalid part. Part no: {%s}, at row: {%s}", partNo, rowNumber));
            }
            taskConsumablePart.setPart(partMap.get(partNo));
            taskConsumablePart.setQuantity((Long) dataMap.get(QUANTITY));
            task.addTaskConsumablePart(taskConsumablePart);

            if (taskIds.add(task.getId())) {
                tasks.add(task);
            }
        }

        saveTaskList(errorList, tasks);
        return errorList;
    }

    private List<String> validatedTaskProcedureAndPrepareEntity(Long aircraftModelId, ExcelData excelData) {
        List<String> errorList = new ArrayList<>();
        List<Task> taskList = getTaskListByAircraftModel(aircraftModelId);

        Set<Long> modelIds = taskList.stream()
                .map(Task::getModel)
                .filter(Objects::nonNull)
                .map(Model::getId)
                .collect(Collectors.toSet());

        Map<String, Task> taskMap = taskList.stream()
                .collect(Collectors.toMap(Task::getTaskNo, Function.identity()));
        List<PositionModelView> positionList = modelTreeService.getPositionsByModelIds(modelIds);

        Map<String, Position> positionMap = new HashMap<>();

        for (PositionModelView positionModelView : positionList) {
            if (Objects.nonNull(positionModelView.getPosition())
                    && !positionMap.containsKey(positionModelView.getPosition().getName())) {
                positionMap.put(positionModelView.getPosition().getName(), positionModelView.getPosition());
            }
        }

        Map<Long, Set<String>> modelPositionMap = positionList.stream()
                .collect(Collectors.groupingBy(PositionModelView::getModelId,
                        Collectors.mapping(p -> p.getPosition().getName(), Collectors.toSet())));
        Set<String> taskProcedures = new HashSet<>();
        for (Task task : taskList) {
            Set<TaskProcedure> taskProcedureSet = task.getTaskProcedureSet();

            for (TaskProcedure taskProcedure : taskProcedureSet) {
                if (Objects.nonNull(taskProcedure) && Objects.nonNull(taskProcedure.getPosition())) {
                    taskProcedures.add(task.getTaskNo() + COLON_SEPARATOR + taskProcedure.getPosition().getName());
                } else if (Objects.nonNull(taskProcedure)) {
                    taskProcedures.add(task.getTaskNo() + COLON_SEPARATOR + NULL);
                }
            }
        }

        List<Task> tasks = new ArrayList<>();
        List<Map<String, ?>> dataList = excelData.getDataList();
        Set<Long> taskIds = new HashSet<>();

        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            TaskProcedure taskProcedure = new TaskProcedure();
            String taskNo = (String) dataMap.get(TaskConstant.TASK_NO);

            Task task = taskMap.get(taskNo);
            if (Objects.isNull(task)) {
                errorList.add(String.format("Invalid task. Task no: {%s}, at row: {%s}",
                        dataMap.get(TaskConstant.TASK_NO), rowNumber));
                continue;
            }

            if (CollectionUtils.isNotEmpty(task.getTaskProcedureSet())) {
                errorList.add(String.format("Job procedure is already uploaded for specific task. " +
                        "Task no: {%s}, at row {%s}", taskNo, rowNumber));
            }

            taskProcedure.setTask(taskMap.get(taskNo));

            Set<String> positionNames = modelPositionMap.get(task.getModelId());
            String positionName = (String) dataMap.get(POSITION_NAME);

            if (StringUtils.isNotBlank(positionName)) {
                if (taskProcedures.contains(taskNo + COLON_SEPARATOR + positionName)) {
                    errorList.add(String.format("Duplicate position found. Task:{%s}, Position: {%s}, at row: {%s}",
                            taskNo, positionName, rowNumber));
                }

                if (CollectionUtils.isEmpty(positionNames)) {
                    errorList.add(String.format("No position found for specific task model. Task:{%s}, at row: {%s}",
                            taskNo, rowNumber));
                }

                if (CollectionUtils.isNotEmpty(positionNames) && !positionNames.contains(positionName)) {
                    errorList.add(String.format("Invalid position name. Position Name: {%s}, at row: {%s}",
                            positionName, rowNumber));
                }
            }

            taskProcedure.setPosition(positionMap.get(positionName));
            taskProcedure.setJobProcedure((String) dataMap.get(JOB_PROCEDURE));

            task.addTaskProcedure(taskProcedure);
            if (taskIds.add(task.getId())) {
                tasks.add(task);
            }
        }

        saveTaskList(errorList, tasks);
        return errorList;
    }

    private List<Task> getTaskListByAircraftModel(Long aircraftModelId) {
        return taskRepository.findAllByAircraftModelIdAndIsActiveTrue(aircraftModelId);
    }

    private List<AircraftEffectivityTaskDto> findRequestDtoByAircraftModelId(Long aircraftModelId) {
        return taskRepository.getTaskByAircraftModelId(aircraftModelId);
    }
}
