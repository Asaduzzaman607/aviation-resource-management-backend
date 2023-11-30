package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.constant.LdndConstant;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftEffectivityDto;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSaveDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.*;
import com.digigate.engineeringmanagement.planning.service.AircraftEffectivityIService;
import com.digigate.engineeringmanagement.planning.service.LdndService;
import com.digigate.engineeringmanagement.planning.service.TaskDoneIService;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Task Done service implementation
 *
 * @author Asifur Rahman
 */
@Service
public class TaskDoneIServiceImpl extends AbstractService<TaskDone, TaskDoneDto>
        implements TaskDoneIService {

    private static final int MAX_DATE_DIFFERENCE = 180;
    private final TaskProcedureRepository taskProcedureRepository;
    private final LdndRepository ldndRepository;
    private final TaskDoneRepository taskDoneRepository;
    private final AircraftEffectivityIService aircraftEffectivityIService;
    private final LdndService ldndService;
    private final AircraftBuildRepository aircraftBuildRepository;
    private final SerialRepository serialRepository;
    private final ModelService modelService;
    private final TaskRepository taskRepository;
    private final PositionRepository positionRepository;
    private final PartRepository partRepository;
    private final AircraftService aircraftService;
    private final Environment environment;

    /**
     * Autowired Constructor
     *
     * @param taskProcedureRepository {@link TaskProcedureRepository}
     * @param ldndRepository          {@link LdndRepository}
     * @param taskDoneRepository      {@link TaskDoneRepository}
     * @param aircraftBuildRepository {@link AircraftBuildRepository}
     * @param serialRepository        {@link SerialRepository}
     * @param modelService            {@link ModelService}
     * @param taskRepository          {@link TaskRepository}
     * @param positionRepository      {@link PositionRepository}
     * @param partRepository          {@link PartRepository}
     * @param aircraftService         {@link AircraftService}
     * @param environment             {@link Environment}
     */
    @Autowired
    public TaskDoneIServiceImpl(AbstractRepository<TaskDone> repository,
                                TaskProcedureRepository taskProcedureRepository,
                                LdndRepository ldndRepository,
                                TaskDoneRepository taskDoneRepository,
                                AircraftEffectivityIService aircraftEffectivityIService,
                                LdndService ldndService, AircraftBuildRepository aircraftBuildRepository,
                                SerialRepository serialRepository, ModelService modelService,
                                TaskRepository taskRepository, PositionRepository positionRepository,
                                PartRepository partRepository, AircraftService aircraftService,
                                Environment environment) {
        super(repository);
        this.taskProcedureRepository = taskProcedureRepository;
        this.ldndRepository = ldndRepository;
        this.taskDoneRepository = taskDoneRepository;
        this.aircraftEffectivityIService = aircraftEffectivityIService;
        this.ldndService = ldndService;
        this.aircraftBuildRepository = aircraftBuildRepository;
        this.serialRepository = serialRepository;
        this.modelService = modelService;
        this.taskRepository = taskRepository;
        this.positionRepository = positionRepository;
        this.partRepository = partRepository;
        this.aircraftService = aircraftService;
        this.environment = environment;
    }


    /**
     * responsible for creating new TaskDone
     *
     * @param dto {@link TaskDoneDto}
     * @return entity     {@link TaskDone}
     */
    @Transactional
    @Override
    public TaskDone create(TaskDoneDto dto) {
        TaskDone taskDone = new TaskDone();
        AircraftEffectivity aircraftEffectivity = aircraftEffectivityIService.getAircraftEffectiveByAircraftAndTask(
                dto.getAircraftId(), dto.getTaskId());

        if (aircraftEffectivity.getEffectivityType().equals(EffectivityType.EFFECTIVE)) {
            taskDone.setLdnd(processAndSaveLdnd(dto));
            taskDoneRepository.save(mapToEntity(dto, taskDone));
            return taskDone;
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.TASK_NOT_APPLICABLE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    private Ldnd processAndSaveLdnd(TaskDoneDto dto) {
        Ldnd existingLdnd = ldndService.findExistingLdnd(dto.getTaskId(),
                dto.getPartId(), dto.getSerialId());
        if (Objects.isNull(existingLdnd.getId())) {
            existingLdnd = ldndService.convertToLdndEntity(dto, new Ldnd());
        } else {
            existingLdnd = ldndService.convertToLdndEntity(dto, existingLdnd);
        }
        return ldndService.save(existingLdnd);
    }


    /**
     * responsible for updating existing TaskDone
     *
     * @param dto {@link TaskDoneDto}
     * @return entity     {@link TaskDone}
     */

    @Transactional
    @Override
    public TaskDone update(TaskDoneDto dto, Long id) {
        Optional<TaskDone> taskDoneOptional = taskDoneRepository.findById(id);
        if (taskDoneOptional.isEmpty()) {
            throw new EngineeringManagementServerException(ErrorId.DATA_NOT_FOUND, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        Ldnd ldnd = processAndSaveLdnd(dto);
        TaskDone taskDone = this.updateEntity(dto, taskDoneOptional.get());
        taskDone.setLdnd(ldnd);
        return taskDoneRepository.save(taskDone);
    }

    /**
     * entity to view model converter method
     *
     * @param taskDone {@link  TaskDone}
     * @return model        {@link  TaskDoneViewModel}
     */
    @Override
    protected TaskDoneViewModel convertToResponseDto(TaskDone taskDone) {
        Ldnd ldnd = taskDone.getLdnd();
        Task task = ldnd.getTask();
        Optional<Position> position = ldndRepository.findTaskDonePositionById(taskDone.getLdndId());
        return TaskDoneViewModel
                .builder()
                .id(taskDone.getId())
                .aircraftId(ldnd.getAircraftId())
                .taskId(task.getId())
                .taskNo(task.getTaskNo())
                .serialNo(ldnd.getSerial().getSerialNumber())
                .serialId(ldnd.getSerial().getId())
                .partId(ldnd.getPartId())
                .partNo(ldnd.getPart().getPartNo())
                .procedureId(ldnd.getTaskProcedureId())
                .isApuControl(ldnd.getIsApuControl())
                .nextDueCycle(ldnd.getDueCycle())
                .nextDueHour(ldnd.getDueHour())
                .nextDueDate(ldnd.getDueDate())
                .remainCycle(ldnd.getRemainingCycle())
                .remainHour(ldnd.getRemainingHour())
                .remainDay((Objects.nonNull(ldnd.getDueDate())) ?
                        ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), ldnd.getDueDate()) : null)
                .estimatedDueDate(ldnd.getEstimatedDueDate())
                .position(position.map(Position::getName).orElse(null))
                .positionId(position.map(Position::getId).orElse(null))
                .aircraftName(ldnd.getAircraft().getAircraftName())
                .doneDate(taskDone.getDoneDate())
                .doneCycle(taskDone.getDoneCycle())
                .initialCycle(taskDone.getInitialCycle())
                .initialHour(taskDone.getInitialHour())
                .intervalType(taskDone.getIntervalType().getId())
                .doneHour(taskDone.getDoneHour())
                .remark(taskDone.getRemark())
                .isActive(taskDone.getIsActive())
                .taskStatus(taskDone.getLdnd().getTaskStatus())
                .build();
    }


    @Override
    protected TaskDone convertToEntity(TaskDoneDto TaskDoneDto) {
        return mapToEntity(TaskDoneDto, new TaskDone());
    }

    @Override
    protected TaskDone updateEntity(TaskDoneDto dto, TaskDone entity) {
        return mapToEntity(dto, entity);
    }

    private TaskDone mapToEntity(TaskDoneDto dto, TaskDone entity) {
        entity.setDoneDate(dto.getDoneDate());
        entity.setDoneHour(dto.getDoneHour());
        entity.setDoneCycle(dto.getDoneCycle());
        entity.setInitialCycle(dto.getInitialCycle());
        entity.setInitialHour(dto.getInitialHour());
        entity.setIntervalType(dto.getIntervalType());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * search ldnd by aircraft and task-no
     *
     * @param searchDto {@link TaskDoneSearchDto }
     * @param pageable  {@link Pageable }
     * @return {@link PageData}
     */
    public PageData searchTaskDone(TaskDoneSearchDto searchDto, Pageable pageable) {

        Page<TaskReportViewModel> taskReportViewModelPage = ldndRepository.searchTaskDone(searchDto.getAircraftId(),
                PlanningUtil.setNullIfEmptyString(searchDto.getTaskNo()), searchDto.getIsActive(),
                PlanningUtil.setNullIfEmptyString(searchDto.getRemark()), pageable);

        return PageData.builder()
                .model(taskReportViewModelPage.getContent())
                .totalPages(taskReportViewModelPage.getTotalPages())
                .totalElements(taskReportViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * get ldnd list by aircraft and due date
     *
     * @param findDto {@link LdndDataFindDto }
     * @return {@link List<LdndDataViewModel>}
     */
    @Override
    public List<LdndDataViewModel> getLdndListByAircraftAndDueDate(LdndDataFindDto findDto) {
        validateMonthRange(findDto.getFromDate(), findDto.getToDate());
        return ldndRepository.getLdndListByAircraftAndDueDate(findDto.getAircraftIds(), findDto.getFromDate(),
                findDto.getToDate());
    }

    private void validateMonthRange(LocalDate fromDate, LocalDate toDate) {
        if (ChronoUnit.DAYS.between(fromDate, toDate) > MAX_DATE_DIFFERENCE) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATE_RANGE_LIMIT_ERROR, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }


    /**
     * get task position data by task id
     *
     * @param taskId {@link <Long>}
     * @return {@link List<TaskDonePositionDto>}
     */
    @Override
    public List<TaskDonePositionDto> getTaskPositionByTaskId(Long taskId) {
        List<TaskProcedure> taskProcedures = taskProcedureRepository.findAllByTaskId(taskId);
        return taskProcedures.stream().map(
                taskProcedure -> TaskDonePositionDto
                        .builder()
                        .position(taskProcedure.getPosition().getName())
                        .procedureId(taskProcedure.getId())
                        .build()).collect(Collectors.toList());
    }

    /**
     * This method Will generate TaskDonePositionDto
     *
     * @param aircraftId {@link Long}
     * @return {@link List<TaskDoneSaveDto>}
     */
    @Override
    public List<TaskDoneSaveDto> getTaskAndPositionByAircraftId(Long aircraftId) {
        List<TaskDoneSaveDto> taskDoneSaveDtoList;
        List<AircraftEffectivityDto> aircraftEffectivityDtoList =
                aircraftEffectivityIService.getAircraftEffectivityByAircraft(aircraftId);

        taskDoneSaveDtoList = aircraftEffectivityDtoList.stream().filter(
                        a -> a.getTask().getIsActive().equals(true))
                .map(aircraftEffectivityDto -> TaskDoneSaveDto.builder()
                        .taskId(aircraftEffectivityDto.getTask().getId())
                        .taskNo(aircraftEffectivityDto.getTask().getTaskNo())
                        .isApuControl(aircraftEffectivityDto.getTask().getIsApuControl())
                        .modelId(aircraftEffectivityDto.getTask().getModelId())
                        .taskStatus(aircraftEffectivityDto.getTask().getTaskStatus())
                        .repetitiveType(aircraftEffectivityDto.getTask().getRepetitiveType().getIntervalEnum())
                        .build())
                .collect(Collectors.toList());
        return taskDoneSaveDtoList;
    }

    @Override
    public Set<AcPartSerialResponse> findAcPartSerialResponse(Long aircraftId, Long modelId) {
        Set<AcPartSerialResponse> acPartSerialResponse = aircraftBuildRepository.findAcPartSerialResponseByModel(
                aircraftId, modelId);
        if (acPartSerialResponse.isEmpty()) {
            return aircraftBuildRepository.findAcPartSerialResponseByHigherModel(
                    aircraftId, modelId);
        } else
            return acPartSerialResponse;
    }

    @Override
    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftId) {
        if (Objects.isNull(aircraftId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_ID_IS_REQUIRED);
        }
        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(LdndConstant.ARM_EXCEL_LDND), LdndConstant.LDND);

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = validateAndPrepareEntity(excelData, aircraftId);

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData, Long aircraftId) {
        List<String> errorMessages = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }
        Aircraft aircraft = aircraftService.findById(aircraftId);

        Set<Position> positionSet = positionRepository.findAllByIsActiveTrue();
        Map<String, Position> positionMap = positionSet.stream().collect(Collectors.toMap(Position::getName,
                Function.identity()));

        Set<Long> modelIds = modelService.findModelIdsByAircraftId(aircraftId);

        Set<Task> taskSet = taskRepository.findAllByModelIdInAndIsActiveTrue(modelIds);
        Map<String, Task> taskMap = taskSet.stream().collect(Collectors.toMap(Task::getTaskNo, Function.identity()));

        Set<Part> partSet = partRepository.findAllParteByModelIdIn(modelIds);
        Map<String, Part> partMap = partSet.stream().collect(Collectors.toMap(part -> StringUtil.buildKey(part.getPartNo()
                , part.getModelId()), Function.identity()));

        List<Long> partIds = partSet.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toList());
        Set<Serial> serialSet = serialRepository.findAllByPartIdInAndIsActiveTrue(partIds);

        Map<String, Serial> serialMap = serialSet.stream().collect(Collectors
                .toMap(serial -> StringUtil.buildKey(serial.getSerialNumber(), serial.getPartId()), Function.identity()));

        List<AircraftBuild> aircraftBuildList = aircraftBuildRepository.findByAircraftId(aircraftId);
        Set<String> existingAircraftBuildKeys = new HashSet<>();

        String aircraftModel = aircraft.getAircraftModel().getAircraftModelName();

        Optional<AircraftBuild> acOwnBuild = aircraftBuildList.stream().filter(
                a -> a.getHigherPart().getPartNo().equals(aircraftModel)).findAny();

        acOwnBuild.ifPresent(aircraftBuild ->
                existingAircraftBuildKeys.add(StringUtil.buildKey(aircraftBuild.getHigherSerial().getSerialNumber(),
                        aircraftBuild.getHigherPartId())));

        aircraftBuildList.forEach(
                aircraftBuild -> existingAircraftBuildKeys.add(
                        StringUtil.buildKey(aircraftBuild.getSerial().getSerialNumber(), aircraftBuild.getPartId())));

        List<Ldnd> ldndList = new ArrayList<>();
        List<TaskDone> taskDoneList = new ArrayList<>();


        List<Map<String, ?>> dataList = excelData.getDataList();
        List<String> remarks = new ArrayList<>();

        for (Map<String, ?> dataMap : dataList) {

            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));

            boolean isValid = isValidClientData(
                    dataMap, partMap, serialMap, positionMap, taskMap, errorMessages);

            if (isValid) {
                Ldnd ldnd = new Ldnd();

                String taskNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.TASK_NUMBER)));
                Task task = taskMap.get(taskNo);

                String partNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.PART_NUMBER)));
                Part part = partMap.get(StringUtil.buildKey(partNo, task.getModelId()));

                String serialNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.SERIAL_NUMBER)));
                Serial serial = serialMap.get(StringUtil.buildKey(serialNo, part.getId()));

                String key = StringUtil.buildKey(serialNo, part.getId());
                if (!existingAircraftBuildKeys.contains(key)) {
                    isValid = false;
                    errorMessages.add(String.format("Aircraft build data not exists for part : {%s} serial: {%s}, at row: {%s}",
                            StringUtil.valueOf(dataMap.get(LdndConstant.PART_NUMBER)),
                            StringUtil.valueOf(dataMap.get(LdndConstant.SERIAL_NUMBER)), rowNumber));
                }

                Ldnd existingLdnd = ldndService.findExistingLdnd(task.getId(),
                        part.getId(), serial.getId());

                if (Objects.isNull(existingLdnd.getId())) {
                    ldnd.setAircraft(aircraft);
                    ldnd.setTask(task);
                    ldnd.setIsApuControl(task.getIsApuControl());
                    ldnd.setPart(part);
                    ldnd.setSerial(serial);
                } else {
                    ldnd = existingLdnd;
                }

                String positionName = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.POSITION)));
                if (ObjectUtils.isNotEmpty(positionName)) {
                    Position position = positionMap.get(positionName);
                    Optional<TaskProcedure> taskProcedure = taskProcedureRepository.findByTaskIdAndPositionId(task.getId(),
                            position.getId());
                    taskProcedure.ifPresent(ldnd::setTaskProcedure);
                }

                Double doneHour = (Double) dataMap.get(LdndConstant.DONE_HOUR);
                if (Objects.nonNull(doneHour) && !NumberUtil.checkValidAirTime(doneHour)) {
                    isValid = false;
                    errorMessages.add(String.format("Done Hour value is not valid : {%s} , at row: {%s}",
                            doneHour, rowNumber));
                }

                Double initialHour = (Double) dataMap.get(LdndConstant.INITIAL_HOUR);
                if (Objects.nonNull(initialHour) && !NumberUtil.checkValidAirTime(initialHour)) {
                    isValid = false;
                    errorMessages.add(String.format("Done Hour value is not valid : {%s} , at row: {%s}",
                            initialHour, rowNumber));
                }

                ldnd.setIntervalType((IntervalType) dataMap.get(LdndConstant.INTERVAL_TYPE));
                ldnd.setDoneHour(doneHour);
                ldnd.setDoneDate((LocalDate) dataMap.get(LdndConstant.DONE_DATE));
                ldnd.setDoneCycle((Integer) dataMap.get(LdndConstant.DONE_CYCLE));
                ldnd.setInitialCycle((Integer) dataMap.get(LdndConstant.INITIAL_CYCLE));
                ldnd.setInitialHour(initialHour);
                remarks.add((String) dataMap.get(LdndConstant.REMARK));
                ldnd.setTaskStatus((TaskStatusEnum) dataMap.get(LdndConstant.STATUS));
                if (isValid) {
                    try {
                        ldnd = ldndService.calculateLdnd(ldnd);
                        ldndList.add(ldnd);
                    } catch (Exception e) {
                        errorMessages.add(String.format("Couldn't Calculate ldnd due to invalid data at row: {%s}", rowNumber));
                    }
                }
            }
        }


        if (org.apache.commons.collections4.CollectionUtils.isEmpty(errorMessages)
                && org.apache.commons.collections4.CollectionUtils.isNotEmpty(ldndList)) {
            try {
                ldndList = ldndRepository.saveAll(ldndList);
                for (Ldnd ldnd : ldndList) {
                    taskDoneList.add(TaskDone.builder()
                            .ldnd(ldnd)
                            .doneHour(ldnd.getDoneHour())
                            .doneCycle(ldnd.getDoneCycle())
                            .doneDate(ldnd.getDoneDate())
                            .initialHour(ldnd.getInitialHour())
                            .initialCycle(ldnd.getInitialCycle())
                            .intervalType(ldnd.getIntervalType())
                            .build());
                }

                for (int i = 0; i < remarks.size(); i++) {
                    if (Objects.nonNull(remarks.get(i))) {
                        taskDoneList.get(i).setRemark(remarks.get(i));
                    }
                }


                taskDoneRepository.saveAll(taskDoneList);

            } catch (Exception e) {
                String entityName = ldndList.get(0).getClass().getSimpleName();
                LOGGER.error("Save failed for entity {}", entityName);
                LOGGER.error("Error message: {}", e.getMessage());
                throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                        entityName));
            }
        }
        return errorMessages;
    }

    private boolean isValidClientData(Map<String, ?> dataMap, Map<String, Part> partMap,
                                      Map<String, Serial> serialMap, Map<String, Position> positionMap,
                                      Map<String, Task> taskMap, List<String> errorMessages) {

        int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
        boolean isValid;

        String taskNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.TASK_NUMBER)));

        isValid = ExcelFileUtil.addErrorIfKeyNotExists(taskNo, LdndConstant.TASK_NUMBER, taskMap, rowNumber, errorMessages);

        Task task = taskMap.get(taskNo);

        String partNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.PART_NUMBER)));

        if (Objects.nonNull(task) && isValid) {
            isValid = ExcelFileUtil.addErrorIfKeyNotExists(
                    StringUtil.buildKey(partNo, task.getModelId()), partNo, LdndConstant.PART_NUMBER, taskNo,
                    LdndConstant.TASK_NUMBER, partMap, rowNumber,
                    errorMessages);
        }

        if (isValid) {

            Part part = partMap.get(StringUtil.buildKey(partNo, task.getModelId()));
            String serialNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(LdndConstant.SERIAL_NUMBER)));

            isValid = ExcelFileUtil.addErrorIfKeyNotExists(
                    StringUtil.buildKey(serialNo, part.getId()), serialNo, LdndConstant.SERIAL_NUMBER, partNo,
                    LdndConstant.PART_NUMBER, serialMap,
                    rowNumber, errorMessages);
        }


        String position = (String) dataMap.get(LdndConstant.POSITION);

        if (ObjectUtils.isNotEmpty(position)) {
            isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(position, LdndConstant.POSITION, positionMap, rowNumber, errorMessages);
        }
        return isValid;
    }

}
