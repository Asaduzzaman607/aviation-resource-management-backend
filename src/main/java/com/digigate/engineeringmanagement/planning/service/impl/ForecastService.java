package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.MapUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.dto.ForecastDataMap;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.ForecastRepository;
import com.digigate.engineeringmanagement.planning.service.*;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AircraftProjection;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Forecast Task Service
 *
 * @author Masud Rana
 */
@Service
public class ForecastService extends AbstractService<Forecast, ForecastDto> implements ForecastIService {
    private final AircraftService aircraftService;
    private final TaskServiceImpl taskService;
    private final PartService partService;
    private final ForecastAircraftIService forecastAircraftIService;
    private final ForecastTaskIService forecastTaskIService;
    private final ForecastTaskPartIService forecastTaskPartIService;
    private final ForecastRepository forecastRepository;
    private final LdndService ldndService;

    private static final String OR_SEPARATOR = " Or ";
    private final Boolean IS_ACTIVE = true;
    private static final int MAX_TASK_FOR_SINGLE_AIRCRAFT = 100;
    private static final int MAX_TASK_FOR_SINGLE_FORECAST = 1000;

    /**
     * Parameterized constructor
     *
     * @param aircraftService            {@link AircraftService}
     * @param taskService                {@link TaskServiceImpl}
     * @param partService                {@link PartService}
     * @param forecastAircraftIService   {@link ForecastAircraftIService}
     * @param forecastTaskIService       {@link ForecastTaskIService}
     * @param forecastTaskPartIService   {@link ForecastTaskPartIService}
     * @param forecastRepository         {@link ForecastRepository}
     * @param ldndService                {@link LdndService}
     */
    public ForecastService(AircraftService aircraftService,
                           TaskServiceImpl taskService, PartService partService,
                           AbstractRepository<Forecast> repository,
                           ForecastAircraftIService forecastAircraftIService,
                           ForecastTaskIService forecastTaskIService,
                           ForecastTaskPartIService forecastTaskPartIService, ForecastRepository forecastRepository,
                           LdndService ldndService) {
        super(repository);
        this.aircraftService = aircraftService;
        this.taskService = taskService;
        this.partService = partService;
        this.ldndService = ldndService;
        this.forecastAircraftIService = forecastAircraftIService;
        this.forecastTaskIService = forecastTaskIService;
        this.forecastTaskPartIService = forecastTaskPartIService;
        this.forecastRepository = forecastRepository;
    }

    /**
     * Generate forecast
     *
     * @param forecastGenerateDto {@link ForecastGenerateDto}
     * @param aircraftId          aircraft id
     * @return {@link ForecastAircraftDto}
     */
    @Override
    public ForecastAircraftDto generate(ForecastGenerateDto forecastGenerateDto, Long aircraftId) {
        List<ForecastRequest> forecastRequestList = forecastGenerateDto.getForecastRequestList();
        Aircraft aircraft = aircraftService.findById(aircraftId);

        Set<Long> ldndIds = new HashSet<>();
        Set<Long> taskIds = new HashSet<>();
        Set<Long> partIds = new HashSet<>();

        for (ForecastRequest forecastRequest : forecastRequestList) {
            if (Objects.nonNull(forecastRequest.getLdndId())) {
                ldndIds.add(forecastRequest.getLdndId());
            }

            if (Objects.nonNull(forecastRequest.getTaskId())) {
                taskIds.add(forecastRequest.getTaskId());
            }

            if (Objects.nonNull(forecastRequest.getPartId())) {
                partIds.add(forecastRequest.getPartId());
            }
        }

        Map<Long, LdndViewModelForForecast> ldndMap = prepareLdndViewMap(ldndIds);
        Map<Long, Set<Part>> partMapByLdnd = preparePartMap(forecastRequestList, partIds);
        Map<Long, Set<ConsumablePartPayload>> consumablePartMapByLdnd =
                prepareConsumablePartMap(taskIds, forecastRequestList);

        ForecastAircraftDto forecastAircraftDto = new ForecastAircraftDto();
        forecastAircraftDto.setAircraftId(aircraftId);
        forecastAircraftDto.setAircraftName(aircraft.getAircraftName());
        forecastAircraftDto.setAircraftSerial(aircraft.getAirframeSerial());

        for (ForecastRequest forecastRequest : forecastRequestList) {
            if (!partMapByLdnd.containsKey(forecastRequest.getLdndId())
                    || !ldndMap.containsKey(forecastRequest.getLdndId())) {
                continue;
            }

            forecastAircraftDto.addForecastTaskDto(buildForecastAircraftDto(forecastRequest,
                    ldndMap, partMapByLdnd, consumablePartMapByLdnd));
        }

        return forecastAircraftDto;
    }

    private ForecastTaskDto buildForecastAircraftDto(ForecastRequest forecastRequest,
                                                     Map<Long, LdndViewModelForForecast> ldndMap,
                                                     Map<Long, Set<Part>> partMap,
                                                     Map<Long, Set<ConsumablePartPayload>> consumablePartMap) {
        LdndViewModelForForecast ldnd = ldndMap.get(forecastRequest.getLdndId());

        return createForecastTaskDto(ldnd, forecastRequest, partMap.get(ldnd.getLdndId()),
                consumablePartMap.get(ldnd.getLdndId()));
    }

    private ForecastTaskDto createForecastTaskDto(LdndViewModelForForecast ldnd,
                                                  ForecastRequest forecastRequest,
                                                  Set<Part> parts, Set<ConsumablePartPayload> consumableParts) {
        ForecastTaskDto forecastTaskDto = new ForecastTaskDto();

        forecastTaskDto.setLdndId(ldnd.getLdndId());
        forecastTaskDto.setDueDate(forecastRequest.getDueDate());
        forecastTaskDto.setTaskId(ldnd.getTaskId());
        forecastTaskDto.setTaskNo(ldnd.getTaskNo());
        addForecastPartFromPart(forecastTaskDto, parts);
        addForecastPartFromConsumablePart(forecastTaskDto, consumableParts);
        return forecastTaskDto;
    }

    private void addForecastPartFromPart(ForecastTaskDto forecastTaskDto, Set<Part> parts) {
        if (CollectionUtils.isEmpty(parts)) {
            return;
        }

        for (Part part : parts) {
            ForecastTaskPartDto forecastTaskPartDto = new ForecastTaskPartDto();
            Long partId = part.getId();
            forecastTaskPartDto.setQuantity(1L);
            forecastTaskPartDto.setPartId(partId);
            forecastTaskPartDto.setDescription(part.getDescription());
            forecastTaskPartDto.setPartNo(buildPartNo(part));
            forecastTaskDto.addForecastPartDto(forecastTaskPartDto);
        }
    }

    private String buildPartNo(Part part) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(part.getPartNo());

        if (CollectionUtils.isNotEmpty(part.getAlternatePartSet())) {
            part.getAlternatePartSet().forEach(alternatePart -> {
                stringBuilder.append(OR_SEPARATOR);
                stringBuilder.append(alternatePart.getPartNo());
            });
        }
        return stringBuilder.toString();
    }

    private Map<Long, AircraftProjection> getAircraftProjectionMap(Set<Long> aircraftIds) {
        if (CollectionUtils.isEmpty(aircraftIds)) {
            return Collections.emptyMap();
        }
        List<AircraftProjection> aircraftProjections = aircraftService.findAircraftByIdInAndIsActiveTrue(aircraftIds);

        return aircraftProjections.stream()
                .collect(Collectors.toMap(AircraftProjection::getId, Function.identity()));

    }

    private Map<Long, Set<Part>> preparePartMap(List<ForecastRequest> forecastRequestList, Set<Long> partIds) {
        List<Part> parts = partService.getAllByDomainIdIn(partIds, true);

        Map<Long, Part> partMap = parts.stream()
                .collect(Collectors.toMap(Part::getId, Function.identity()));

        Map<Long, Set<Part>> partMapByLdnd = new HashMap<>();
        forecastRequestList.forEach(forecastRequest -> {
            if (!MapUtil.containsNonNullValue(partMapByLdnd, forecastRequest.getLdndId())) {
                partMapByLdnd.put(forecastRequest.getLdndId(), new HashSet<>());
            }

            partMapByLdnd.get(forecastRequest.getLdndId()).add(partMap.get(forecastRequest.getPartId()));
        });

        return partMapByLdnd;
    }

    private void addForecastPartFromConsumablePart(ForecastTaskDto forecastTaskDto,
                                                   Set<ConsumablePartPayload> consumableParts) {
        if (CollectionUtils.isEmpty(consumableParts)) {
            return;
        }

        for (ConsumablePartPayload consumablePart : consumableParts) {

            ForecastTaskPartDto forecastTaskPartDto = new ForecastTaskPartDto();
            forecastTaskPartDto.setPartId(consumablePart.getPart().getId());
            forecastTaskPartDto.setPartNo(consumablePart.getPart().getPartNo());
            forecastTaskPartDto.setDescription(consumablePart.getPart().getDescription());
            forecastTaskPartDto.setQuantity(consumablePart.getQuantity());
            forecastTaskDto.addForecastPartDto(forecastTaskPartDto);
        }
    }

    /**
     * search forecast
     *
     * @param forecast {@link Forecast}
     * @return {@link ForecastDto}
     */
    @Override
    protected ForecastDto convertToResponseDto(Forecast forecast) {
        return prepareDetailResponse(forecast);
    }

    /**
     * search forecast
     *
     * @param searchDto {@link ForecastSearchDto}
     * @return {@link List<Forecast>}
     */
    @Override
    public Page<ForecastViewModel> search(ForecastSearchDto searchDto, Pageable pageable) {
        return forecastRepository.findByName(searchDto.getName(), searchDto.getIsActive(), pageable);
    }

    private ForecastDto prepareDetailResponse(Forecast forecast) {
        List<ForecastAircraftDto> forecastAircraftList = forecastAircraftIService.findByForecastId(forecast.getId());

        Map<Long, Set<ForecastAircraftDto>> forecastAircraftMap = forecastAircraftList
                .stream().collect(Collectors.groupingBy(ForecastAircraftDto::getForecastId,
                        Collectors.mapping(forecastAircraftDto -> forecastAircraftDto, Collectors.toSet())));

        updateForecastAircraft(forecastAircraftList);
        Long forecastId = forecast.getId();
        ForecastDto forecastDto = new ForecastDto();
        forecastDto.setId(forecastId);
        forecastDto.setName(forecast.getName());
        forecastDto.setIsActive(forecast.getIsActive());
        forecastDto.setForecastAircraftDtoList(forecastAircraftMap.get(forecastId));
        return forecastDto;
    }

    private void updateForecastAircraft(List<ForecastAircraftDto> forecastAircraftList) {
        if (CollectionUtils.isEmpty(forecastAircraftList)) {
            return;
        }

        Set<Long> aircraftIds = new HashSet<>();
        Set<Long> forecastAircraftIds = new HashSet<>();
        for (ForecastAircraftDto aircraftDto : forecastAircraftList) {
            forecastAircraftIds.add(aircraftDto.getId());
            aircraftIds.add(aircraftDto.getAircraftId());
        }

        Set<ForecastTaskDto> forecastTaskDtoSet = forecastTaskIService.findByForecastAircraftIdIn(forecastAircraftIds);
        Map<Long, AircraftProjection> aircraftMap = getAircraftProjectionMap(aircraftIds);
        Map<Long, Set<ForecastTaskDto>> forecastTaskMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(forecastTaskDtoSet)) {
            forecastTaskMap =
                    forecastTaskDtoSet.stream().collect(
                            Collectors.groupingBy(ForecastTaskDto::getForecastAircraftId,
                                    Collectors.mapping(forecastTaskDto -> forecastTaskDto, Collectors.toSet())));
        }

        for (ForecastAircraftDto forecastAircraftDto : forecastAircraftList) {
            setAircraftNameAndSerial(forecastAircraftDto, aircraftMap.get(forecastAircraftDto.getAircraftId()));
            forecastAircraftDto
                    .setForecastTaskDtoList(forecastTaskMap.get(forecastAircraftDto.getId()));
        }

        updateForecastTask(forecastTaskDtoSet);
    }

    private void setAircraftNameAndSerial(ForecastAircraftDto aircraftDto, AircraftProjection aircraftProjection) {
        if (Objects.nonNull(aircraftProjection)) {
            aircraftDto.setAircraftName(aircraftProjection.getAircraftName());
            aircraftDto.setAircraftSerial(aircraftProjection.getAirframeSerial());
        }
    }

    private void updateForecastTaskPart(Set<ForecastTaskPartDto> forecastTaskPartDtoSet) {
        if (CollectionUtils.isEmpty(forecastTaskPartDtoSet)) {
            return;
        }
        Set<Long> partIds = new HashSet<>();
        forecastTaskPartDtoSet.forEach(forecastTaskPartDto -> {
            if (Objects.nonNull(forecastTaskPartDto.getPartId())) {
                partIds.add(forecastTaskPartDto.getPartId());
            }
        });

        Map<Long, Part> partMap = preparePartMap(partIds);
        forecastTaskPartDtoSet.forEach(forecastTaskPartDto -> {
            Long partId = forecastTaskPartDto.getPartId();
            if (MapUtil.containsNonNullValue(partMap, partId)) {
                Part part = partMap.get(partId);
                forecastTaskPartDto.setPartNo(buildPartNo(part));
                forecastTaskPartDto.setDescription(part.getDescription());
            }
        });
    }

    private void updateForecastTask(Set<ForecastTaskDto> forecastTaskDtoSet) {
        if (CollectionUtils.isEmpty(forecastTaskDtoSet)) {
            return;
        }
        Set<Long> forecastTaskIds = new HashSet<>();
        for (ForecastTaskDto taskDto : forecastTaskDtoSet) {
            forecastTaskIds.add(taskDto.getId());
        }

        Set<ForecastTaskPartDto> forecastTaskPartSet = forecastTaskPartIService.findByForecastTaskIdIn(forecastTaskIds);
        Map<Long, Set<ForecastTaskPartDto>> forecastTaskPartMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(forecastTaskPartSet)) {
            forecastTaskPartMap =
                    forecastTaskPartSet.stream().collect(Collectors.groupingBy(ForecastTaskPartDto::getForecastTaskId,
                            Collectors.mapping(forecastTaskPartDto -> forecastTaskPartDto, Collectors.toSet())));
        }

        for (ForecastTaskDto forecastTaskDto : forecastTaskDtoSet) {
            forecastTaskDto.setForecastTaskPartDtoList(forecastTaskPartMap.get(forecastTaskDto.getId()));
        }

        updateForecastTaskPart(forecastTaskPartSet);
    }

    /**
     * Create entity
     *
     * @param forecastDto {@link ForecastDto}
     * @return {@link Forecast}
     */
    @Override
    @Transactional
    public Forecast create(ForecastDto forecastDto) {
        validateClientData(forecastDto, null);
        Forecast entity = convertToEntity(forecastDto);
        return saveItem(entity);
    }

    /**
     * Update entity
     *
     * @param forecastDto {@link ForecastDto}
     * @param id          {@link Long}
     * @return {@link Forecast}
     */
    @Override
    @Transactional
    public Forecast update(ForecastDto forecastDto, Long id) {
        validateClientData(forecastDto, null);
        Forecast entity = updateEntity(forecastDto, findByIdUnfiltered(id));
        return saveItem(entity);
    }

    @Override
    public Boolean validateClientData(ForecastDto forecastDto, Long id) {
        int numOfTask = 0;
        if (CollectionUtils.isNotEmpty(forecastDto.getForecastAircraftDtoList())) {
            numOfTask = calculateTotalTask(forecastDto.getForecastAircraftDtoList(), numOfTask);
        }
        if (numOfTask > MAX_TASK_FOR_SINGLE_FORECAST) {
            throw EngineeringManagementServerException.badRequest(ErrorId.NUMBER_OF_TOTAL_FORECAST_TASKS_EXCEED);
        }
        return true;
    }

    private int calculateTotalTask(Set<ForecastAircraftDto> forecastAircraftDtoSet, int numOfTask) {
        for (ForecastAircraftDto forecastAircraftDto : forecastAircraftDtoSet) {
            if (CollectionUtils.isNotEmpty(forecastAircraftDto.getForecastTaskDtoList())) {
                int size = forecastAircraftDto.getForecastTaskDtoList().size();
                if (size > MAX_TASK_FOR_SINGLE_AIRCRAFT) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.TASK_EXCEED_FOR_SINGLE_AIRCRAFT);
                }
                numOfTask = numOfTask + size;
            }
        }
        return numOfTask;
    }


    /**
     * Convert dto to entity
     *
     * @param forecastDto {@link ForecastDto}
     * @return {@link Forecast}
     */
    @Override
    protected Forecast convertToEntity(ForecastDto forecastDto) {
        return prepareForecast(forecastDto, new Forecast());
    }


    /**
     * Convert dto to entity
     *
     * @param forecastDto {@link ForecastDto}
     * @return {@link Forecast}
     */
    @Override
    protected Forecast updateEntity(ForecastDto forecastDto, Forecast forecast) {
        return prepareForecast(forecastDto, forecast);
    }

    private Forecast prepareForecast(ForecastDto forecastDto, Forecast forecast) {
        ForecastDataMap forecastDataMap = new ForecastDataMap();
        parsePartAircraftAndLdndIds(forecastDto.getForecastAircraftDtoList(), forecastDataMap);
        forecast.setName(forecastDto.getName());
        updateExistingDataMap(forecast, forecastDataMap);
        addForecastAircraftToForecast(forecastDto.getForecastAircraftDtoList(), forecastDataMap, forecast);
        return forecast;
    }

    private void addForecastAircraftToForecast(Set<ForecastAircraftDto> forecastAircraftDtoSet,
                                               ForecastDataMap forecastDataMap, Forecast forecast) {
        if (CollectionUtils.isEmpty(forecastAircraftDtoSet)) {
            if (CollectionUtils.isNotEmpty(forecast.getForecastAircraftSet())) {
                forecast.getForecastAircraftSet().clear();
            }
            return;
        }

        Map<Long, Aircraft> aircraftMap = forecastDataMap.getAircraftMap();
        Set<ForecastAircraft> forecastAircraftSet = new HashSet<>();

        forecastAircraftDtoSet.forEach(forecastAircraftDto -> {
            if (!aircraftMap.containsKey(forecastAircraftDto.getAircraftId())) {
                throw EngineeringManagementServerException.notFound(ErrorId.AIRCRAFT_NOT_FOUND);
            }

            ForecastAircraft forecastAircraft = getExistingOrNewForeCastAircraft(
                    forecastAircraftDto.getId(), forecastDataMap.getForecastAircraftMap());

            forecastAircraft.setAircraft(aircraftMap.get(forecastAircraftDto.getAircraftId()));
            forecastAircraftSet.add(forecastAircraft);

            addForecastTaskToForecastAircraft(forecastAircraft,
                    forecastAircraftDto.getForecastTaskDtoList(), forecastDataMap);
        });

        if (CollectionUtils.isNotEmpty(forecast.getForecastAircraftSet())) {
            forecast.getForecastAircraftSet().clear();
        }

        forecastAircraftSet.forEach(forecast::addForecastAircraft);
    }

    private void addForecastTaskToForecastAircraft(ForecastAircraft forecastAircraft,
                                                   Set<ForecastTaskDto> forecastTaskDtoSet,
                                                   ForecastDataMap forecastDataMap) {
        if (CollectionUtils.isEmpty(forecastTaskDtoSet)) {
            if (CollectionUtils.isNotEmpty(forecastAircraft.getForecastTaskSet())) {
                forecastAircraft.getForecastTaskSet().clear();
            }
            return;
        }

        Map<Long, ForecastTask> forecastTaskMap = forecastDataMap.getForecastTaskMap();
        Map<Long, Ldnd> ldndMap = forecastDataMap.getLdndMap();
        Set<ForecastTask> forecastTaskSet = new HashSet<>();

        forecastTaskDtoSet.forEach(forecastTaskDto -> {
            ForecastTask forecastTask = getExistingOrNewForecastTask(forecastTaskMap, forecastTaskDto.getId());
           Long ldndId = forecastTaskDto.getLdndId();
            if (!MapUtil.containsNonNullValue(ldndMap, ldndId)) {
                throw EngineeringManagementServerException.notFound(ErrorId.INVALID_LDND_ID);
            }

            forecastTask.setLdnd(ldndMap.get(ldndId));
            forecastTask.setDueDate(forecastTaskDto.getDueDate());
            forecastTask.setComment(forecastTaskDto.getComment());
            forecastTaskSet.add(forecastTask);
            addForecastTaskPartToForecastTask(forecastTask,
                    forecastTaskDto.getForecastTaskPartDtoList(), forecastDataMap);
        });

        if (CollectionUtils.isNotEmpty(forecastAircraft.getForecastTaskSet())) {
            forecastAircraft.getForecastTaskSet().clear();
        }
        forecastTaskSet.forEach(forecastAircraft::addForecastTask);
    }

    private void addForecastTaskPartToForecastTask(ForecastTask forecastTask,
                                                   Set<ForecastTaskPartDto> forecastTaskPartDtoSet,
                                                   ForecastDataMap forecastDataMap) {
        if (CollectionUtils.isEmpty(forecastTaskPartDtoSet)) {
            if (CollectionUtils.isNotEmpty(forecastTask.getForecastTaskPartSet())) {
                forecastTask.getForecastTaskPartSet().clear();
            }
            return;
        }

        Map<Long, Part> partMap = forecastDataMap.getPartMap();
        Map<Long, ForecastTaskPart> forecastTaskPartMap = forecastDataMap.getForecastTaskPartMap();
        Set<ForecastTaskPart> forecastTaskPartSet = new HashSet<>();

        forecastTaskPartDtoSet.forEach(forecastTaskPartDto -> {
            ForecastTaskPart forecastTaskPart
                    = getExistingOrNewForecastTaskPart(forecastTaskPartMap, forecastTaskPartDto.getId());

            forecastTaskPart.setQuantity(forecastTaskPartDto.getQuantity());
            forecastTaskPart.setIpcRef(forecastTaskPartDto.getIpcRef());
            Long partId = forecastTaskPartDto.getPartId();

            if (Objects.nonNull(partId)) {
                addPart(partId, forecastTaskPart, partMap);
            }

            forecastTaskPartSet.add(forecastTaskPart);
        });

        if (CollectionUtils.isNotEmpty(forecastTask.getForecastTaskPartSet())) {
            forecastTask.getForecastTaskPartSet().clear();
        }
        forecastTaskPartSet.forEach(forecastTask::addForecastTaskPart);

    }

    private void addPart(Long partId, ForecastTaskPart forecastTaskPart, Map<Long, Part> partMap) {
        if (!MapUtil.containsNonNullValue(partMap, partId)) {
            throw EngineeringManagementServerException.notFound(ErrorId.PART_NOT_FOUND);
        }
        forecastTaskPart.setPart(partMap.get(partId));
    }

    private ForecastTask getExistingOrNewForecastTask(
            Map<Long, ForecastTask> forecastTaskMap, Long forecastTaskId) {

        if (Objects.isNull(forecastTaskId)) {
            return new ForecastTask();
        }
        if (!MapUtil.containsNonNullValue(forecastTaskMap, forecastTaskId)) {
            throw EngineeringManagementServerException.notFound(ErrorId.FORECAST_TASK_NOT_FOUND);
        }
        return forecastTaskMap.get(forecastTaskId);
    }

    private ForecastTaskPart getExistingOrNewForecastTaskPart(
            Map<Long, ForecastTaskPart> forecastTaskPartMap, Long forecastPartId) {

        if (Objects.isNull(forecastPartId)) {
            return new ForecastTaskPart();
        }
        if (!MapUtil.containsNonNullValue(forecastTaskPartMap, forecastPartId)) {
            throw EngineeringManagementServerException.notFound(ErrorId.FORECAST_PART_NOT_FOUND);
        }
        return forecastTaskPartMap.get(forecastPartId);

    }

    private ForecastAircraft getExistingOrNewForeCastAircraft(
            Long forecastAircraftId, Map<Long, ForecastAircraft> forecastAircraftMap) {
        if (Objects.isNull(forecastAircraftId)) {
            return new ForecastAircraft();
        }
        ForecastAircraft forecastAircraft = forecastAircraftMap.get(forecastAircraftId);
        if (Objects.isNull(forecastAircraft)) {
            throw EngineeringManagementServerException.notFound(ErrorId.FORECAST_AIRCRAFT_NOT_FOUND);
        }
        return forecastAircraft;
    }

    private void parsePartAircraftAndLdndIds(Set<ForecastAircraftDto> forecastAircraftDtoList,
                                             ForecastDataMap forecastDataMap) {
        Set<Long> partIds = new HashSet<>();
        Set<Long> aircraftIds = new HashSet<>();
        Set<Long> ldndIds = new HashSet<>();
        forecastAircraftDtoList.forEach(forecastAircraftDto -> {
            if (Objects.nonNull(forecastAircraftDto.getAircraftId())) {
                aircraftIds.add(forecastAircraftDto.getAircraftId());
            }
            parseLdndAndPartIds(forecastAircraftDto.getForecastTaskDtoList(),
                    partIds, ldndIds);
        });
        forecastDataMap.setPartMap(preparePartMap(partIds));
        forecastDataMap.setAircraftMap(prepareAircraftMap(aircraftIds));
        forecastDataMap.setLdndMap(prepareLdndMap(ldndIds));
    }


    private void updateExistingDataMap(Forecast forecast, ForecastDataMap forecastDataMap) {
        if (CollectionUtils.isEmpty(forecast.getForecastAircraftSet())) {
            return;
        }
        Map<Long, ForecastAircraft> forecastAircraftMap = new HashMap<>();
        Map<Long, ForecastTask> forecastTaskMap = new HashMap<>();
        Map<Long, ForecastTaskPart> forecastTaskPartMap = new HashMap<>();
        forecast.getForecastAircraftSet().forEach(forecastAircraft -> {
            forecastAircraftMap.put(forecastAircraft.getId(), forecastAircraft);
            updateForecastTaskAndPartMap(forecastTaskMap, forecastTaskPartMap, forecastAircraft.getForecastTaskSet());
        });
        forecastDataMap.setForecastAircraftMap(forecastAircraftMap);
        forecastDataMap.setForecastTaskMap(forecastTaskMap);
        forecastDataMap.setForecastTaskPartMap(forecastTaskPartMap);
    }

    private void updateForecastTaskAndPartMap(Map<Long, ForecastTask> forecastTaskMap,
                                              Map<Long, ForecastTaskPart> forecastTaskPartMap,
                                              Set<ForecastTask> forecastTaskSet) {
        if (CollectionUtils.isEmpty(forecastTaskSet)) {
            return;
        }
        forecastTaskSet.forEach(forecastTask -> {
            forecastTaskMap.put(forecastTask.getId(), forecastTask);
            updateForecastTaskPartMap(forecastTaskPartMap, forecastTask.getForecastTaskPartSet());
        });
    }

    private void updateForecastTaskPartMap(Map<Long, ForecastTaskPart> forecastTaskPartMap,
                                           Set<ForecastTaskPart> forecastTaskPartSet) {
        if (CollectionUtils.isEmpty(forecastTaskPartSet)) {
            return;
        }
        forecastTaskPartSet
                .forEach(forecastTaskPart -> forecastTaskPartMap.put(forecastTaskPart.getId(), forecastTaskPart));
    }

    private Map<Long, Part> preparePartMap(Set<Long> partIds) {
        if (isEmptyIds(partIds)) {
            return Collections.emptyMap();
        }
        List<Part> partList = partService.getAllByDomainIdIn(partIds, IS_ACTIVE);
        if (partList.size() != partIds.size()) {
            throw EngineeringManagementServerException.notFound(ErrorId.PART_NOT_FOUND);
        }
        return MapUtil.convertToMapById(partList);
    }

    private Map<Long, Ldnd> prepareLdndMap(Set<Long> ldndIds) {
        if (isEmptyIds(ldndIds)) {
            return Collections.emptyMap();
        }
        List<Ldnd> ldndList = ldndService.getAllLdndByDomainIdIn(ldndIds, true);
        if (ldndIds.size() != ldndList.size()) {
            throw EngineeringManagementServerException.notFound(ErrorId.INVALID_LDND_ID);
        }
        return MapUtil.convertToMapById(ldndList);
    }

    private Map<Long, LdndViewModelForForecast> prepareLdndViewMap(Set<Long> ldndIds) {
        if (isEmptyIds(ldndIds)) {
            return Collections.emptyMap();
        }
        List<LdndViewModelForForecast> ldndList = ldndService.findAllLdndByLdndIds(ldndIds);

        if (ldndIds.size() != ldndList.size()) {
            throw EngineeringManagementServerException.notFound(ErrorId.INVALID_LDND_ID);
        }

        return ldndList.stream()
                .collect(Collectors.toMap(LdndViewModelForForecast::getLdndId, Function.identity()));
    }

    private Map<Long, Set<ConsumablePartPayload>> prepareConsumablePartMap(Set<Long> taskIds,
                                                          List<ForecastRequest> forecastRequestList) {
        Map<Long, Set<ConsumablePartPayload>> consumablePartMapByLdnd = new HashMap<>();

        List<Task> taskList = taskService.getAllByDomainIdIn(taskIds, Boolean.TRUE);
        Map<Long, Task> taskMap = taskList.stream()
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        for (ForecastRequest forecastRequest : forecastRequestList) {
            Task task = MapUtil.getOrDefaultFromMap(taskMap, forecastRequest.getTaskId(), null);

            if (Objects.isNull(task)) {
               continue;
            }

            Set<TaskConsumablePart> taskConsumablePartSet = task.getTaskConsumablePartSet();

            if (CollectionUtils.isNotEmpty(taskConsumablePartSet)) {
                Set<ConsumablePartPayload> partSet = new HashSet<>();
                taskConsumablePartSet.forEach(taskConsumablePart -> {
                    if (Objects.nonNull(taskConsumablePart.getPart())) {
                        partSet.add(
                                ConsumablePartPayload.builder()
                                        .part(taskConsumablePart.getPart())
                                        .quantity(taskConsumablePart.getQuantity())
                                        .build()
                        );
                    }
                });

                if (CollectionUtils.isNotEmpty(partSet)) {
                    consumablePartMapByLdnd.put(forecastRequest.getLdndId(), partSet);
                }
            }
        }

        return consumablePartMapByLdnd;
    }

    private Map<Long, Aircraft> prepareAircraftMap(Set<Long> aircraftIds) {
        if (isEmptyIds(aircraftIds)) {
            return Collections.emptyMap();
        }
        List<Aircraft> aircraftList = aircraftService.getAllByDomainIdIn(aircraftIds, IS_ACTIVE);
        if (aircraftList.size() != aircraftIds.size()) {
            throw EngineeringManagementServerException.notFound(ErrorId.AIRCRAFT_NOT_FOUND);
        }
        return MapUtil.convertToMapById(aircraftList);
    }

    private boolean isEmptyIds(Set<Long> ids) {
        return CollectionUtils.isEmpty(ids);
    }

    private void parseLdndAndPartIds(Set<ForecastTaskDto> forecastTaskDtoList,
                                     Set<Long> partIds, Set<Long> ldndIds) {
        if (CollectionUtils.isEmpty(forecastTaskDtoList)) {
            return;
        }
        forecastTaskDtoList.forEach(forecastTaskDto -> {
            if (Objects.nonNull(forecastTaskDto.getLdndId())) {
                ldndIds.add(forecastTaskDto.getLdndId());
            }
            parsePartIds(forecastTaskDto.getForecastTaskPartDtoList(), partIds);
        });
    }

    private void parsePartIds(Set<ForecastTaskPartDto> forecastTaskPartDtoList,
                              Set<Long> partIds) {
        if (CollectionUtils.isEmpty(forecastTaskPartDtoList)) {
            return;
        }
        forecastTaskPartDtoList.forEach(forecastTaskPartDto -> {
            if (Objects.nonNull(forecastTaskPartDto.getPartId())) {
                partIds.add(forecastTaskPartDto.getPartId());
            }
        });
    }
}
