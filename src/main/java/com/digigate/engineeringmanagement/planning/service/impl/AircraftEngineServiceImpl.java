package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftBuildRepository;
import com.digigate.engineeringmanagement.planning.repository.EngineShopVisitRepository;
import com.digigate.engineeringmanagement.planning.repository.EngineTimeRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftBuildIService;
import com.digigate.engineeringmanagement.planning.service.AircraftEngineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AircraftEngineServiceImpl implements AircraftEngineService {
    private final AircraftBuildIService aircraftBuildIService;
    private final EngineShopVisitRepository engineShopVisitRepository;
    private final EngineTimeRepository engineTimeRepository;
    private final AircraftBuildRepository aircraftBuildRepository;

    private final AircraftRepository aircraftRepository;

    private final AircraftMaintenanceLogServiceImpl aircraftMaintenanceLogService;

    /**
     * parametrized constructor
     *
     * @param aircraftBuildIService         {@link AircraftBuildIService}
     * @param engineShopVisitRepository     {@link EngineShopVisitRepository}
     * @param engineTimeRepository          {@link EngineTimeRepository}
     * @param aircraftBuildRepository
     * @param aircraftRepository
     * @param aircraftMaintenanceLogService
     */
    public AircraftEngineServiceImpl(AircraftBuildIService aircraftBuildIService,
                                     EngineShopVisitRepository engineShopVisitRepository,
                                     EngineTimeRepository engineTimeRepository,
                                     AircraftBuildRepository aircraftBuildRepository,
                                     AircraftRepository aircraftRepository,
                                     AircraftMaintenanceLogServiceImpl aircraftMaintenanceLogService) {
        this.aircraftBuildIService = aircraftBuildIService;
        this.engineShopVisitRepository = engineShopVisitRepository;
        this.engineTimeRepository = engineTimeRepository;
        this.aircraftBuildRepository = aircraftBuildRepository;
        this.aircraftRepository = aircraftRepository;
        this.aircraftMaintenanceLogService = aircraftMaintenanceLogService;
    }

    /**
     * responsible for saving or updating Engine Tmm and Rgb Info
     *
     * @param aircraftEngineDto {@link AircraftEngineDto}
     * @return                  response message
     */
    @Transactional
    @Override
    public String saveOrUpdateEngineTmmRgbInfo(AircraftEngineDto aircraftEngineDto) {
        validateAircraftEngineTmmRgbPayload(aircraftEngineDto);
        AircraftBuild aircraftBuild = aircraftBuildIService.findById(aircraftEngineDto.getAircraftBuildId());
        saveOrUpdateEngineTmmRgbInfo(aircraftBuild, aircraftEngineDto);
        return "Engine TMM and RGB information updated successfully.";
    }

    /**
     * responsible for search aircraft engine info by aircraft id
     *
     * @param aircraftEngineSearchDto {@link AircraftEngineSearchDto}
     * @param pageable                {@link Pageable}
     * @return                        response info as page data
     */
    @Override
    public Page<EngineInfoViewModel> searchAircraftEngineInfo(AircraftEngineSearchDto aircraftEngineSearchDto,
                                                              Pageable pageable) {
        Set<EngineInfoViewModel> engineInfoViewModels =
                engineTimeRepository.findEngineInfoByAircraftId(aircraftEngineSearchDto.getAircraftId());


        return new PageImpl<>(new ArrayList<>(engineInfoViewModels),
                pageable, engineInfoViewModels.size());
    }

    /**
     * responsible for finding engine tmm and rgb info by aircraft build id
     *
     * @param aircraftBuildId aircraftBuildId
     * @return                engine tmm or rgb info as view model
     */
    @Override
    public AircraftEngineTmmRgbViewModel findEngineTmmRgbInfoByAircraftBuild(Long aircraftBuildId) {
        AircraftBuild aircraftBuild = aircraftBuildIService.findById(aircraftBuildId);

        List<EngineShopVisitViewModel> engineShopVisitViewModels =
                engineShopVisitRepository.findAllByAircraftBuildId(aircraftBuildId);

        List<EngineTimeViewModel> engineTimeViewModels =
                engineTimeRepository.findAllByAircraftBuildId(aircraftBuildId);
        return AircraftEngineTmmRgbViewModel.builder()
                .aircraftId(aircraftBuild.getAircraftId())
                .aircraftBuildId(aircraftBuildId)
                .position(Objects.nonNull(aircraftBuild.getPosition())
                        ? aircraftBuild.getPosition().getName() : null)
                .aircraftName(Objects.nonNull(aircraftBuild.getAircraft())
                        ? aircraftBuild.getAircraft().getAircraftName() : null)
                .engineShopVisitViewModels(engineShopVisitViewModels)
                .engineTimeViewModels(engineTimeViewModels)
                .build();
    }

    @Override
    public EngineLlpStatusReportViewModel generateInactivateEngineLlpStatusReport(
            EngineLlpStatusReportDto engineLlpStatusReportDto) {
        EngineLlpStatusReportViewModel engineLlpStatusReportViewModel = new EngineLlpStatusReportViewModel();
        AircraftBuild aircraftBuild = aircraftBuildRepository.findByInactivateId(engineLlpStatusReportDto.getAircraftBuildId());
        List<AircraftBuild> aircraftBuildList =
                aircraftBuildIService.findAllInactivateTmmAndRgbByHigherSerialAndPart(aircraftBuild.getSerialId(),
                        aircraftBuild.getPartId());

        prepareLastShopVisitedInfoForInactiveEngine(engineLlpStatusReportDto.getAircraftBuildId(), engineLlpStatusReportViewModel);

        CurrentTimesViewModel currentTimesViewModel =
                prepareTime(aircraftBuildList, aircraftBuild.getAircraft(),
                        aircraftBuild);
        engineLlpStatusReportViewModel.setCurrentTimesViewModel(currentTimesViewModel);

        ShopVisitedInformation shopVisitedInformation =
                prepareShopVisitedInfoForInactivateEngine(aircraftBuild, aircraftBuild.getAircraft());
        engineLlpStatusReportViewModel.setShopVisitedInformation(shopVisitedInformation);

        EngineInstallationInfoViewModel engineInstallationInfoViewModel =
                prepareEngineInstallationInfo(aircraftBuild, aircraftBuildList, aircraftBuild.getAircraft());
        engineLlpStatusReportViewModel.setEngineInstallationInfoViewModel(engineInstallationInfoViewModel);

        List<EngineLlpPartViewModel> engineLlpPartViewModels =
                prepareInactivateEngineLlpParts(aircraftBuild.getSerialId(), aircraftBuild.getPartId(),
                        aircraftBuild.getAircraft(),aircraftBuild.getOutDate());
        engineLlpStatusReportViewModel.setEngineLlpPartViewModels(engineLlpPartViewModels);

        return engineLlpStatusReportViewModel;
    }

    private void prepareLastShopVisitedInfoForInactiveEngine(Long aircraftBuildId,
                                            EngineLlpStatusReportViewModel engineLlpStatusReportViewModel) {
        LastShopVisitedInfoViewModel lastShopVisitedInfoViewModel = new LastShopVisitedInfoViewModel();
        List<EngineShopVisit> engineShopVisits =
                engineShopVisitRepository.findEngineShopInfoByAircraftBuildId(aircraftBuildId);

        for (EngineShopVisit engineShopVisit : engineShopVisits) {
            if (Objects.nonNull(engineShopVisit.getDate())) {
                if (engineShopVisit.getType().equals(ModelType.ENGINE_TMM)) {
                    lastShopVisitedInfoViewModel.setEngineTmmViewModel(
                            EngineTmmViewModel.builder()
                                    .currentDate(engineShopVisit.getDate())
                                    .tsn(engineShopVisit.getTsn())
                                    .csn(engineShopVisit.getCsn())
                                    .tso(engineShopVisit.getTso())
                                    .cso(engineShopVisit.getCso())
                                    .status(engineShopVisit.getStatus())
                                    .build()
                    );
                } else if (engineShopVisit.getType().equals(ModelType.ENGINE_RGB)) {
                    lastShopVisitedInfoViewModel.setEngineRgbViewModel(
                            EngineRgbViewModel.builder()
                                    .currentDate(engineShopVisit.getDate())
                                    .tsn(engineShopVisit.getTsn())
                                    .csn(engineShopVisit.getCsn())
                                    .tso(engineShopVisit.getTso())
                                    .cso(engineShopVisit.getCso())
                                    .status(engineShopVisit.getStatus())
                                    .build()
                    );
                }
            }
        }
        engineLlpStatusReportViewModel.setLastShopVisitedInfoViewModel(lastShopVisitedInfoViewModel);
    }

    private ShopVisitedInformation prepareShopVisitedInfoForInactivateEngine(AircraftBuild aircraftBuild, Aircraft aircraft) {
        ShopVisitedInformation shopVisitedInformation = new ShopVisitedInformation();
        if (BooleanUtils.isTrue(aircraftBuild.getIsShopVisited()) && Objects.nonNull(aircraftBuild.getAircraftOutHour())
                && Objects.nonNull(aircraftBuild.getAircraftInHour())) {
            if (Objects.nonNull(aircraftBuild.getTslsvHour())) {
                shopVisitedInformation.setTslsv(DateUtil.addTimes(
                        DateUtil.subtractTimes(aircraftBuild.getAircraftOutHour(), aircraftBuild.getAircraftInHour()),
                        aircraftBuild.getTslsvHour()));
            }

            if (Objects.nonNull(aircraftBuild.getTslsvCycle())) {
                shopVisitedInformation.setCslsv(aircraftBuild.getAircraftOutCycle() -
                        aircraftBuild.getAircraftInCycle() + aircraftBuild.getTslsvCycle());
            }
        }

        return shopVisitedInformation;
    }

    private List<EngineLlpPartViewModel> prepareInactivateEngineLlpParts(Long serialId, Long partId,
                                                                         Aircraft aircraft, LocalDate date) {
        List<EngineLlpPartViewModel> engineLlpPartViewModels = new ArrayList<>();

        List<AircraftBuild> aircraftBuildList = aircraftBuildIService.findAllInactivateEngineLlpParts(serialId, partId);

        for (AircraftBuild aircraftBuild : aircraftBuildList) {
            Double installedTsn = Objects.nonNull(aircraftBuild.getTsnHour()) ? aircraftBuild.getTsnHour() : null;
            Integer installedCsn = Objects.nonNull(aircraftBuild.getTsnCycle()) ? aircraftBuild.getTsnCycle() : null;

            Double currentTsn = calculateCurrentTsnForInactiveParts(aircraftBuild, installedTsn);
            Integer currentCsn = calculateCurrentCsnForInactiveParts(aircraftBuild, installedCsn);

            Double countFactor = calculateCountFactor(aircraftBuild.getPart());
            Long lifeLimit = prepareLifeLimit(aircraftBuild.getPart());

            Long remainingFC = calculateRemainingFC(countFactor, lifeLimit, currentCsn);
            LocalDate estimatedDueDate = calculateEstimatedDueDateForGivenDate(remainingFC,
                    aircraft.getDailyAverageCycle(), date);

            engineLlpPartViewModels.add(
                    EngineLlpPartViewModel.builder()
                            .nomenclature(aircraftBuild.getPart().getDescription())
                            .partNo(aircraftBuild.getPart().getPartNo())
                            .serialNo(aircraftBuild.getSerial().getSerialNumber())
                            .serialId(aircraftBuild.getSerialId())
                            .installedTsn(installedTsn)
                            .installedCsn(installedCsn)
                            .currentTsn(currentTsn)
                            .currentCsn(currentCsn)
                            .lifeLimit(lifeLimit)
                            .remainingFC(remainingFC)
                            .estimatedDueDate(estimatedDueDate)
                            .build()
            );
        }

        return engineLlpPartViewModels;
    }

    private CurrentTimesViewModel prepareTime(List<AircraftBuild> aircraftBuildList, Aircraft aircraft,
                                              AircraftBuild engineAircraftBuild) {
            CurrentTimesViewModel currentTimesViewModel = new CurrentTimesViewModel();
            List<EngineTime> engineTimes =
                    engineTimeRepository.findEngineShopInfoByAircraftBuildId(engineAircraftBuild.getId());

            Map<Integer, EngineTime> engineTimeMap = new HashMap<>();
            CurrentEngineTimeViewModel currentEngineTimeViewModel = new CurrentEngineTimeViewModel();
            currentTimesViewModel.setCurrentEngineTimeViewModel(currentEngineTimeViewModel);
            if (CollectionUtils.isNotEmpty(engineTimes)) {
                engineTimeMap = engineTimes.stream()
                        .collect(Collectors.toMap(engineTime -> engineTime.getType().getId(), Function.identity()));
                currentEngineTimeViewModel.setNameExtension(engineTimes.get(0).getNameExtension());
            }

            for (AircraftBuild aircraftBuild : aircraftBuildList) {
                if (Objects.nonNull(aircraftBuild.getModel())) {
                    if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_TMM)) {
                        EngineTmmViewModel engineTmmViewModel = new EngineTmmViewModel();

                        engineTmmViewModel.setCurrentDate(aircraftBuild.getOutDate());
                        engineTmmViewModel.setTat(aircraftBuild.getAircraftOutHour());
                        engineTmmViewModel.setTac(aircraftBuild.getAircraftOutCycle());
                        engineTmmViewModel.setTsn(prepareTSN(engineTmmViewModel.getTat(), aircraftBuild));
                        engineTmmViewModel.setCsn(prepareCSN(engineTmmViewModel.getTac(), aircraftBuild));
                        engineTmmViewModel.setTso(prepareTSO(engineTmmViewModel.getTat(), aircraftBuild));
                        engineTmmViewModel.setCso(prepareCSO(engineTmmViewModel.getTac(), aircraftBuild));
                        prepareInactivateEngineTimeInfo(aircraftBuild, currentEngineTimeViewModel,
                                engineTimeMap.getOrDefault(aircraftBuild.getModel().getModelType().getId(), null));

                        currentTimesViewModel.setEngineTmmViewModel(engineTmmViewModel);
                    } else if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_RGB)) {
                        EngineRgbViewModel engineRgbViewModel = new EngineRgbViewModel();

                        engineRgbViewModel.setCurrentDate(aircraftBuild.getOutDate());
                        engineRgbViewModel.setTat(aircraftBuild.getAircraftOutHour());
                        engineRgbViewModel.setTac(aircraftBuild.getAircraftOutCycle());
                        engineRgbViewModel.setTsn(prepareTSN(engineRgbViewModel.getTat(), aircraftBuild));
                        engineRgbViewModel.setCsn(prepareCSN(engineRgbViewModel.getTac(), aircraftBuild));
                        engineRgbViewModel.setTso(prepareTSO(engineRgbViewModel.getTat(), aircraftBuild));
                        engineRgbViewModel.setCso(prepareCSO(engineRgbViewModel.getTac(), aircraftBuild));
                        prepareInactivateEngineTimeInfo(aircraftBuild, currentEngineTimeViewModel,
                                engineTimeMap.getOrDefault(aircraftBuild.getModel().getModelType().getId(), null));

                        currentTimesViewModel.setEngineRgbViewModel(engineRgbViewModel);
                    }
                }
            }
            return currentTimesViewModel;
    }

    /**
     * responsible for generating engine llp status report
     *
     * @param engineLlpStatusReportDto {@link EngineLlpStatusReportDto}
     * @return                         engine LLP status report as view model
     */
    @Override
    public EngineLlpStatusReportViewModel generateEngineLlpStatusReport(
            EngineLlpStatusReportDto engineLlpStatusReportDto) {

        LocalDate currentDate = DateUtil.getCurrentUTCDate();
        if (Objects.nonNull(engineLlpStatusReportDto.getDate())
                && engineLlpStatusReportDto.getDate().isAfter(currentDate)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DATE_FORMAT);
        }

        Aircraft aircraft = aircraftBuildIService.getAircraftInfoByAircraftBuildId
                (engineLlpStatusReportDto.getAircraftBuildId(), engineLlpStatusReportDto.getDate());

        EngineLlpStatusReportViewModel engineLlpStatusReportViewModel = new EngineLlpStatusReportViewModel();
        AircraftBuild aircraftBuild = aircraftBuildIService.findById(engineLlpStatusReportDto.getAircraftBuildId());

        List<AircraftBuild> aircraftBuildList =
                aircraftBuildIService.findAllTmmAndRgbByHigherSerialAndPart(aircraftBuild.getSerialId(),
                        aircraftBuild.getPartId());

        prepareLastShopVisitedInfo(engineLlpStatusReportDto.getAircraftBuildId(), engineLlpStatusReportDto.getDate(),
                engineLlpStatusReportViewModel);

        CurrentTimesViewModel currentTimesViewModel =
                prepareCurrentTimes(aircraftBuildList, aircraftBuild.getAircraft(),
                        aircraftBuild);
        engineLlpStatusReportViewModel.setCurrentTimesViewModel(currentTimesViewModel);

        ShopVisitedInformation shopVisitedInformation =
                prepareShopVisitedInfo(aircraftBuild, aircraftBuild.getAircraft());
        engineLlpStatusReportViewModel.setShopVisitedInformation(shopVisitedInformation);

        EngineInstallationInfoViewModel engineInstallationInfoViewModel =
                prepareEngineInstallationInfo(aircraftBuild, aircraftBuildList, aircraftBuild.getAircraft());
        engineLlpStatusReportViewModel.setEngineInstallationInfoViewModel(engineInstallationInfoViewModel);

        List<EngineLlpPartViewModel> engineLlpPartViewModels =
                prepareEngineLlpParts(aircraftBuild.getSerialId(), aircraftBuild.getPartId(),
                        aircraftBuild.getAircraft(),engineLlpStatusReportDto.getDate());
        engineLlpStatusReportViewModel.setEngineLlpPartViewModels(engineLlpPartViewModels);

        return engineLlpStatusReportViewModel;
    }

    private void prepareLastShopVisitedInfo(Long aircraftBuildId, LocalDate date,
                                            EngineLlpStatusReportViewModel engineLlpStatusReportViewModel) {
        LastShopVisitedInfoViewModel lastShopVisitedInfoViewModel = new LastShopVisitedInfoViewModel();
        List<EngineShopVisit> engineShopVisits =
                engineShopVisitRepository.findEngineShopInfoByAircraftBuildId(aircraftBuildId);

        for (EngineShopVisit engineShopVisit : engineShopVisits) {
            if (Objects.nonNull(engineShopVisit.getDate())) {
                if (Objects.isNull(date) || engineShopVisit.getDate().isBefore(date)
                        || engineShopVisit.getDate().equals(date)) {
                    if (engineShopVisit.getType().equals(ModelType.ENGINE_TMM)) {
                        lastShopVisitedInfoViewModel.setEngineTmmViewModel(
                                EngineTmmViewModel.builder()
                                        .currentDate(engineShopVisit.getDate())
                                        .tsn(engineShopVisit.getTsn())
                                        .csn(engineShopVisit.getCsn())
                                        .tso(engineShopVisit.getTso())
                                        .cso(engineShopVisit.getCso())
                                        .status(engineShopVisit.getStatus())
                                        .build()
                        );
                    } else if (engineShopVisit.getType().equals(ModelType.ENGINE_RGB)) {
                        lastShopVisitedInfoViewModel.setEngineRgbViewModel(
                                EngineRgbViewModel.builder()
                                        .currentDate(engineShopVisit.getDate())
                                        .tsn(engineShopVisit.getTsn())
                                        .csn(engineShopVisit.getCsn())
                                        .tso(engineShopVisit.getTso())
                                        .cso(engineShopVisit.getCso())
                                        .status(engineShopVisit.getStatus())
                                        .build()
                        );
                    }
                }
            }
        }
        engineLlpStatusReportViewModel.setLastShopVisitedInfoViewModel(lastShopVisitedInfoViewModel);
    }

    private void validateAircraftEngineTmmRgbPayload(AircraftEngineDto aircraftEngineDto) {
        List<EngineShopVisitDto> engineShopVisitDtoList = aircraftEngineDto.getEngineShopVisitDtoList();

        if (CollectionUtils.isNotEmpty(engineShopVisitDtoList)) {
            if (engineShopVisitDtoList.size() > 2) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_SHOP_VISITED_INFO,
                        HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
            }

            List<Integer> modelTypes = engineShopVisitDtoList.stream()
                    .map(EngineShopVisitDto::getModelType)
                    .map(ModelType::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            validatedModelType(modelTypes);
        }

        List<EngineTimeDto> engineTimeDtoList = aircraftEngineDto.getEngineTimeDtoList();

        if (CollectionUtils.isNotEmpty(engineTimeDtoList)) {
            if (engineTimeDtoList.size() > 2) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_ENGINE_TIME_INFO,
                        HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
            }

            List<Integer> modelTypes = engineTimeDtoList.stream()
                    .map(EngineTimeDto::getModelType)
                    .map(ModelType::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            validatedModelType(modelTypes);
        }
    }

    private void validatedModelType(List<Integer> modelTypes) {
        int tmmModelType = 0;
        int rgbModelType = 0;
        for (Integer modelType : modelTypes) {
            if (!(modelType.equals(ModelType.ENGINE_TMM.getId())
                    || modelType.equals(ModelType.ENGINE_RGB.getId()))) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_MODEL_TYPE,
                        HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
            }

            if (modelType.equals(ModelType.ENGINE_TMM.getId())) {
                tmmModelType++;
            }

            if (modelType.equals(ModelType.ENGINE_RGB.getId())) {
                rgbModelType++;
            }
        }

        if (tmmModelType > 1 || rgbModelType > 1) {
            throw new EngineeringManagementServerException(ErrorId.WRONG_MODEL_TYPE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    private void saveOrUpdateEngineTmmRgbInfo(AircraftBuild aircraftBuild, AircraftEngineDto aircraftEngineDto) {
        List<EngineShopVisitDto> aircraftEngineDtoList = aircraftEngineDto.getEngineShopVisitDtoList();

        if (CollectionUtils.isNotEmpty(aircraftEngineDtoList)) {
            saveOrUpdateEngineShopVisits(aircraftEngineDtoList, aircraftBuild);
        }

        List<EngineTimeDto> engineTimeDtoList = aircraftEngineDto.getEngineTimeDtoList();
        saveOrUpdateEngineTimes(engineTimeDtoList, aircraftBuild, aircraftEngineDto.getNameExtension());
    }

    private void saveOrUpdateEngineTimes(List<EngineTimeDto> engineTimeDtoList, AircraftBuild aircraftBuild,
                                         String nameExtension) {
        Set<Long> engineTimeIds = engineTimeDtoList.stream()
                .map(EngineTimeDto::getEngineTimeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        engineTimesDuplicateCheck(engineTimeIds, aircraftBuild.getId());

        Map<Long, EngineTime> engineTimeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(engineTimeIds)) {
            List<EngineTime> engineTimes = engineTimeRepository.findAllByIdIn(engineTimeIds);

            if (engineTimeIds.size() != engineTimes.size()) {
                throw new EngineeringManagementServerException(ErrorId.ENGINE_TIMES_NOT_FOUND,
                        HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID));
            }

            engineTimeMap = engineTimes.stream()
                    .collect(Collectors.toMap(EngineTime::getId, Function.identity()));
        }

        List<EngineTime> engineTimes = new ArrayList<>();

        for (EngineTimeDto engineTimeDto : engineTimeDtoList) {
            EngineTime engineTime;
            if (Objects.nonNull(engineTimeDto.getEngineTimeId())) {
                engineTime = engineTimeMap.getOrDefault(engineTimeDto.getEngineTimeId(), null);

                if (Objects.isNull(engineTime)) {
                    throw new EngineeringManagementServerException(ErrorId.ENGINE_TIMES_NOT_FOUND,
                            HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID));
                }
            } else {
                engineTime = new EngineTime();
            }

            engineTime.setAircraftBuild(aircraftBuild);
            engineTime.setNameExtension(nameExtension);
            engineTime.setType(engineTimeDto.getModelType());
            engineTime.setDate(engineTimeDto.getDate());
            engineTime.setHour(engineTimeDto.getHour());
            engineTime.setCycle(engineTimeDto.getCycle());

            engineTimes.add(engineTime);
        }

        if (CollectionUtils.isNotEmpty(engineTimes)) {
            try {
                engineTimeRepository.saveAll(engineTimes);
            } catch (Exception ex) {
                throw new EngineeringManagementServerException(ErrorId.FAILED_TO_SAVE_ENGINE_TIME,
                        HttpStatus.INTERNAL_SERVER_ERROR, MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
    }

    private void engineTimesDuplicateCheck(Set<Long> engineTimeIds, Long aircraftBuildId) {
        if (CollectionUtils.isEmpty(engineTimeIds)
                && CollectionUtils.isNotEmpty(
                engineTimeRepository.findEngineShopIdsByAircraftBuildId(aircraftBuildId))) {
            throw new EngineeringManagementServerException(ErrorId.DUPLICATE_ENGINE_TIME_INFO_FOUND,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }


    private void saveOrUpdateEngineShopVisits(List<EngineShopVisitDto> aircraftEngineDtoList,
                                              AircraftBuild aircraftBuild) {
        Set<Long> engineShopVisitIds = aircraftEngineDtoList.stream()
                .map(EngineShopVisitDto::getEngineShopVisitId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        engineShopVisitDuplicateCheck(engineShopVisitIds, aircraftBuild.getId());

        Map<Long, EngineShopVisit> engineShopVisitMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(engineShopVisitIds)) {
            List<EngineShopVisit> engineShopVisits = engineShopVisitRepository.findAllByIdIn(engineShopVisitIds);

            if (engineShopVisitIds.size() != engineShopVisits.size()) {
                throw new EngineeringManagementServerException(ErrorId.ENGINE_SHOP_VISIT_NOT_FOUND,
                        HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID));
            }

            engineShopVisitMap = engineShopVisits.stream()
                    .collect(Collectors.toMap(EngineShopVisit::getId, Function.identity()));
        }

        List<EngineShopVisit> engineShopVisits = new ArrayList<>();

        for (EngineShopVisitDto engineShopVisitDto : aircraftEngineDtoList) {
            EngineShopVisit engineShopVisit;
            if (Objects.nonNull(engineShopVisitDto.getEngineShopVisitId())) {
                engineShopVisit = engineShopVisitMap.getOrDefault(engineShopVisitDto.getEngineShopVisitId(), null);

                if (Objects.isNull(engineShopVisit)) {
                    throw new EngineeringManagementServerException(ErrorId.ENGINE_SHOP_VISIT_NOT_FOUND,
                            HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID));
                }
            } else {
                engineShopVisit = new EngineShopVisit();
            }

            engineShopVisit.setAircraftBuild(aircraftBuild);
            engineShopVisit.setType(engineShopVisitDto.getModelType());
            engineShopVisit.setDate(engineShopVisitDto.getDate());
            engineShopVisit.setTsn(engineShopVisitDto.getTsn());
            engineShopVisit.setCsn(engineShopVisitDto.getCsn());
            engineShopVisit.setTso(engineShopVisitDto.getTso());
            engineShopVisit.setCso(engineShopVisitDto.getCso());
            engineShopVisit.setStatus(engineShopVisitDto.getStatus());

            engineShopVisits.add(engineShopVisit);
        }

        if (CollectionUtils.isNotEmpty(engineShopVisits)) {
            try {
                engineShopVisitRepository.saveAll(engineShopVisits);
            } catch (Exception ex) {
                throw new EngineeringManagementServerException(ErrorId.FAILED_TO_SAVE_ENGINE_SHOP_VISITS,
                        HttpStatus.INTERNAL_SERVER_ERROR, MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
    }

    private void engineShopVisitDuplicateCheck(Set<Long> engineShopVisitIds, Long aircraftBuildId) {
        if (CollectionUtils.isEmpty(engineShopVisitIds)
                && CollectionUtils.isNotEmpty(
                engineShopVisitRepository.findEngineShopIdsByAircraftBuildId(aircraftBuildId))) {
            throw new EngineeringManagementServerException(ErrorId.DUPLICATE_SHOP_INFO_FOUND, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    private ShopVisitedInformation prepareShopVisitedInfo(AircraftBuild aircraftBuild, Aircraft aircraft) {
        ShopVisitedInformation shopVisitedInformation = new ShopVisitedInformation();
        if (BooleanUtils.isTrue(aircraftBuild.getIsShopVisited()) && Objects.nonNull(aircraft.getAirFrameTotalTime())
                && Objects.nonNull(aircraftBuild.getAircraftInHour())) {
                if (Objects.nonNull(aircraftBuild.getTslsvHour())) {
                    shopVisitedInformation.setTslsv(DateUtil.addTimes(
                            DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), aircraftBuild.getAircraftInHour()),
                            aircraftBuild.getTslsvHour()));
                }

                if (Objects.nonNull(aircraftBuild.getTslsvCycle())) {
                    shopVisitedInformation.setCslsv(aircraft.getAirframeTotalCycle() -
                            aircraftBuild.getAircraftInCycle() + aircraftBuild.getTslsvCycle());
                }
        }

        return shopVisitedInformation;
    }

    private List<EngineLlpPartViewModel> prepareEngineLlpParts(Long serialId, Long partId, Aircraft aircraft, LocalDate date) {
        List<EngineLlpPartViewModel> engineLlpPartViewModels = new ArrayList<>();

        List<AircraftBuild> aircraftBuildList = aircraftBuildIService.findAllEngineLlpParts(serialId, partId);

        for (AircraftBuild aircraftBuild : aircraftBuildList) {
            Double installedTsn = Objects.nonNull(aircraftBuild.getTsnHour()) ? aircraftBuild.getTsnHour() : null;
            Integer installedCsn = Objects.nonNull(aircraftBuild.getTsnCycle()) ? aircraftBuild.getTsnCycle() : null;

            Double currentTsn = calculateCurrentTsn(aircraft, aircraftBuild, installedTsn);
            Integer currentCsn = calculateCurrentCsn(aircraft, aircraftBuild, installedCsn);

            Double countFactor = calculateCountFactor(aircraftBuild.getPart());
            Long lifeLimit = prepareLifeLimit(aircraftBuild.getPart());

            Long remainingFC = calculateRemainingFC(countFactor, lifeLimit, currentCsn);

            LocalDate estimatedDueDate;
            if(Objects.nonNull(date)){
                 estimatedDueDate = calculateEstimatedDueDateForGivenDate(remainingFC, aircraft.getDailyAverageCycle(), date);
            }else{
                 estimatedDueDate = calculateEstimatedDueDate(remainingFC, aircraft.getDailyAverageCycle());
            }
            engineLlpPartViewModels.add(
                    EngineLlpPartViewModel.builder()
                            .nomenclature(aircraftBuild.getPart().getDescription())
                            .partNo(aircraftBuild.getPart().getPartNo())
                            .serialNo(aircraftBuild.getSerial().getSerialNumber())
                            .serialId(aircraftBuild.getSerialId())
                            .installedTsn(installedTsn)
                            .installedCsn(installedCsn)
                            .currentTsn(currentTsn)
                            .currentCsn(currentCsn)
                            .lifeLimit(lifeLimit)
                            .remainingFC(remainingFC)
                            .estimatedDueDate(estimatedDueDate)
                            .build()
            );
        }

        return engineLlpPartViewModels;
    }

    private Integer calculateCurrentCsn(Aircraft aircraft, AircraftBuild aircraftBuild, Integer installedCsn) {
        if (Objects.nonNull(installedCsn)) {
            return aircraft.getAirframeTotalCycle() - aircraftBuild.getAircraftInCycle() + installedCsn;
        }

        return null;
    }

    private Double calculateCurrentTsn(Aircraft aircraft, AircraftBuild aircraftBuild, Double installedTsn) {
        if (Objects.nonNull(aircraft.getAirFrameTotalTime()) && Objects.nonNull(aircraftBuild.getAircraftInHour())
                && Objects.nonNull(installedTsn)) {
            return DateUtil.addTimes(
                    DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), aircraftBuild.getAircraftInHour()),
                    installedTsn
            );
        }
        return null;
    }

    private Integer calculateCurrentCsnForInactiveParts(AircraftBuild aircraftBuild, Integer installedCsn) {
        if (Objects.nonNull(installedCsn)) {
            return aircraftBuild.getAircraftOutCycle() - aircraftBuild.getAircraftInCycle() + installedCsn;
        }

        return null;
    }

    private Double calculateCurrentTsnForInactiveParts(AircraftBuild aircraftBuild, Double installedTsn) {
        if (Objects.nonNull(aircraftBuild.getAircraftOutHour()) && Objects.nonNull(aircraftBuild.getAircraftInHour())
                && Objects.nonNull(installedTsn)) {
            return DateUtil.addTimes(
                    DateUtil.subtractTimes(aircraftBuild.getAircraftOutHour(), aircraftBuild.getAircraftInHour()),
                    installedTsn
            );
        }
        return null;
    }

    private Long prepareLifeLimit(Part part) {
        if (Objects.nonNull(part)) {
            return Objects.nonNull(part.getLifeLimit()) ? part.getLifeLimit() : null;
        }

        return null;
    }

    private LocalDate calculateEstimatedDueDate(Long remainingFC, Integer dailyAverageCycle) {
        if (Objects.nonNull(dailyAverageCycle) && dailyAverageCycle > 0 && Objects.nonNull(remainingFC)) {
            int noOfDays = remainingFC.intValue() / dailyAverageCycle;

            return DateUtil.getCurrentUTCDate().plusDays(noOfDays);
        }

        return null;
    }

    private LocalDate calculateEstimatedDueDateForGivenDate(Long remainingFC, Integer dailyAverageCycle, LocalDate date) {
        if (Objects.nonNull(dailyAverageCycle) && dailyAverageCycle > 0 && Objects.nonNull(remainingFC)) {
            int noOfDays = remainingFC.intValue() / dailyAverageCycle;

            return date.plusDays(noOfDays);
        }

        return null;
    }

    private Long calculateRemainingFC(Double countFactor, Long lifeLimit, Integer installedCsn) {
        if (countFactor > 0 && Objects.nonNull(lifeLimit) && Objects.nonNull(installedCsn)) {
            return DateUtil.roundUp ((lifeLimit / countFactor) - installedCsn);
        }

        return null;
    }

    private Double calculateCountFactor(Part part) {
        if (Objects.nonNull(part)) {
            return Objects.nonNull(part.getCountFactor()) ? part.getCountFactor() : 1.0;
        }

        return 1.0;
    }

    private EngineInstallationInfoViewModel prepareEngineInstallationInfo(AircraftBuild aircraftBuild,
                                                                          List<AircraftBuild> aircraftBuildList,
                                                                          Aircraft aircraft) {
        EngineInstallationInfoViewModel engineInstallationInfoViewModel = new EngineInstallationInfoViewModel();

        engineInstallationInfoViewModel.setAircraftRegistrationNo(aircraft.getAircraftName());
        engineInstallationInfoViewModel.setMsn(aircraft.getAirframeSerial());
        engineInstallationInfoViewModel.setPositionName(Objects.nonNull(aircraftBuild.getPosition()) ?
                aircraftBuild.getPosition().getName() : null);
        engineInstallationInfoViewModel.setAttachDate(aircraftBuild.getAttachDate());
        engineInstallationInfoViewModel.setTat(aircraftBuild.getAircraftInHour());
        engineInstallationInfoViewModel.setTac(aircraftBuild.getAircraftInCycle());
        prepareInstallationTmmRgb(aircraftBuildList, engineInstallationInfoViewModel);
        engineInstallationInfoViewModel.setAverageCycles(aircraft.getDailyAverageCycle());

        return engineInstallationInfoViewModel;
    }

    private void prepareInstallationTmmRgb(List<AircraftBuild> aircraftBuildList,
                                                      EngineInstallationInfoViewModel engineInstallationInfoViewModel) {

        for (AircraftBuild aircraftBuild : aircraftBuildList) {
            if (Objects.nonNull(aircraftBuild.getModel())) {
                if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_TMM)) {
                    engineInstallationInfoViewModel.setEngineTmmViewModel(
                            EngineTmmViewModel.builder()
                                    .tsn(prepareInstallationTsn(aircraftBuild))
                                    .csn(prepareInstallationCsn(aircraftBuild))
                                    .tso(prepareInstallationTso(aircraftBuild))
                                    .cso(prepareInstallationCso(aircraftBuild))
                                    .build()
                    );
                } else if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_RGB)) {
                    engineInstallationInfoViewModel.setEngineRgbViewModel(
                            EngineRgbViewModel.builder()
                                    .tsn(prepareInstallationTsn(aircraftBuild))
                                    .csn(prepareInstallationCsn(aircraftBuild))
                                    .tso(prepareInstallationTso(aircraftBuild))
                                    .cso(prepareInstallationCso(aircraftBuild))
                                    .build()
                    );
                }
            }
        }
    }

    private Integer prepareInstallationCso(AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsOverhauled())
                && Objects.nonNull(aircraftBuild.getTsoCycle())) {
            return aircraftBuild.getTsoCycle();
        }
        return null;
    }

    private Double prepareInstallationTso(AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsOverhauled())
                && Objects.nonNull(aircraftBuild.getTsoHour())) {
            return aircraftBuild.getTsoHour();
        }
        return null;
    }

    private Integer prepareInstallationCsn(AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsTsnAvailable())
                && Objects.nonNull(aircraftBuild.getTsnCycle())) {
            return aircraftBuild.getTsnCycle();
        }
        return null;
    }

    private Double prepareInstallationTsn(AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsTsnAvailable())
                && Objects.nonNull(aircraftBuild.getTsnHour())) {
            return aircraftBuild.getTsnHour();
        }
        return null;
    }

    private CurrentTimesViewModel prepareCurrentTimes( List<AircraftBuild> aircraftBuildList, Aircraft aircraft,
                                                       AircraftBuild engineAircraftBuild) {
        CurrentTimesViewModel currentTimesViewModel = new CurrentTimesViewModel();

        List<EngineTime> engineTimes =
                engineTimeRepository.findEngineShopInfoByAircraftBuildId(engineAircraftBuild.getId());

        Map<Integer, EngineTime> engineTimeMap = new HashMap<>();
        CurrentEngineTimeViewModel currentEngineTimeViewModel = new CurrentEngineTimeViewModel();
        currentTimesViewModel.setCurrentEngineTimeViewModel(currentEngineTimeViewModel);
        if (CollectionUtils.isNotEmpty(engineTimes)) {
            engineTimeMap = engineTimes.stream()
                    .collect(Collectors.toMap(engineTime -> engineTime.getType().getId(), Function.identity()));
            currentEngineTimeViewModel.setNameExtension(engineTimes.get(0).getNameExtension());
        }

        for (AircraftBuild aircraftBuild : aircraftBuildList) {
            if (Objects.nonNull(aircraftBuild.getModel())) {
                if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_TMM)) {
                    EngineTmmViewModel engineTmmViewModel = new EngineTmmViewModel();

                    engineTmmViewModel.setCurrentDate(aircraft.getUpdatedAt());
                    engineTmmViewModel.setTat(aircraft.getAirFrameTotalTime());
                    engineTmmViewModel.setTac(aircraft.getAirframeTotalCycle());
                    engineTmmViewModel.setTsn(prepareTSN(engineTmmViewModel.getTat(), aircraftBuild));
                    engineTmmViewModel.setCsn(prepareCSN(engineTmmViewModel.getTac(), aircraftBuild));
                    engineTmmViewModel.setTso(prepareTSO(engineTmmViewModel.getTat(), aircraftBuild));
                    engineTmmViewModel.setCso(prepareCSO(engineTmmViewModel.getTac(), aircraftBuild));
                    prepareCurrentEngineTimeInfo(aircraft, aircraftBuild, currentEngineTimeViewModel,
                            engineTimeMap.getOrDefault(aircraftBuild.getModel().getModelType().getId(), null));

                    currentTimesViewModel.setEngineTmmViewModel(engineTmmViewModel);
                } else if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_RGB)) {
                    EngineRgbViewModel engineRgbViewModel = new EngineRgbViewModel();

                    engineRgbViewModel.setCurrentDate(aircraft.getUpdatedAt());
                    engineRgbViewModel.setTat(aircraft.getAirFrameTotalTime());
                    engineRgbViewModel.setTac(aircraft.getAirframeTotalCycle());
                    engineRgbViewModel.setTsn(prepareTSN(engineRgbViewModel.getTat(), aircraftBuild));
                    engineRgbViewModel.setCsn(prepareCSN(engineRgbViewModel.getTac(), aircraftBuild));
                    engineRgbViewModel.setTso(prepareTSO(engineRgbViewModel.getTat(), aircraftBuild));
                    engineRgbViewModel.setCso(prepareCSO(engineRgbViewModel.getTac(), aircraftBuild));
                    prepareCurrentEngineTimeInfo(aircraft, aircraftBuild, currentEngineTimeViewModel,
                            engineTimeMap.getOrDefault(aircraftBuild.getModel().getModelType().getId(), null));

                    currentTimesViewModel.setEngineRgbViewModel(engineRgbViewModel);
                }
            }
        }

        return currentTimesViewModel;
    }

    private void prepareCurrentEngineTimeInfo(Aircraft aircraft, AircraftBuild aircraftBuild,
                                              CurrentEngineTimeViewModel currentEngineTimeViewModel,
                                              EngineTime engineTime) {
        if (Objects.nonNull(engineTime)) {
            if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_TMM)) {
                currentEngineTimeViewModel.setCurrentEngineTimeTmmViewModel(
                        CurrentEngineTimeTmmViewModel.builder()
                                .hour(prepareCurrentEngineTime(engineTime.getHour(), aircraft.getAirFrameTotalTime(),
                                        aircraftBuild.getAircraftInHour()))
                                .cycle(prepareCurrentEngineCycle(engineTime.getCycle(),
                                        aircraft.getAirframeTotalCycle(), aircraftBuild.getAircraftInCycle()))
                                .build()
                );
            } else if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_RGB)) {
                currentEngineTimeViewModel.setCurrentEngineTimeRgbViewModel(
                        CurrentEngineTimeRgbViewModel.builder()
                                .hour(prepareCurrentEngineTime(engineTime.getHour(), aircraft.getAirFrameTotalTime(),
                                        aircraftBuild.getAircraftInHour()))
                                .cycle(prepareCurrentEngineCycle(engineTime.getCycle(),
                                        aircraft.getAirframeTotalCycle(), aircraftBuild.getAircraftInCycle()))
                                .build()
                );
            }
        }
    }

    private void prepareInactivateEngineTimeInfo(AircraftBuild aircraftBuild,
                                              CurrentEngineTimeViewModel currentEngineTimeViewModel,
                                              EngineTime engineTime) {
        if (Objects.nonNull(engineTime)) {
            if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_TMM)) {
                currentEngineTimeViewModel.setCurrentEngineTimeTmmViewModel(
                        CurrentEngineTimeTmmViewModel.builder()
                                .hour(prepareCurrentEngineTime(engineTime.getHour(), aircraftBuild.getAircraftOutHour(),
                                        aircraftBuild.getAircraftInHour()))
                                .cycle(prepareCurrentEngineCycle(engineTime.getCycle(),
                                        aircraftBuild.getAircraftOutCycle(), aircraftBuild.getAircraftInCycle()))
                                .build()
                );
            } else if (aircraftBuild.getModel().getModelType().equals(ModelType.ENGINE_RGB)) {
                currentEngineTimeViewModel.setCurrentEngineTimeRgbViewModel(
                        CurrentEngineTimeRgbViewModel.builder()
                                .hour(prepareCurrentEngineTime(engineTime.getHour(), aircraftBuild.getAircraftOutHour(),
                                        aircraftBuild.getAircraftInHour()))
                                .cycle(prepareCurrentEngineCycle(engineTime.getCycle(),
                                        aircraftBuild.getAircraftOutCycle(), aircraftBuild.getAircraftInCycle()))
                                .build()
                );
            }
        }
    }

    private Integer prepareCurrentEngineCycle(Integer cycle, Integer airframeTotalCycle, Integer aircraftInCycle) {
        if (Objects.nonNull(cycle) && Objects.nonNull(airframeTotalCycle) && Objects.nonNull(aircraftInCycle)) {
            return airframeTotalCycle - aircraftInCycle + cycle;
        }

        return null;
    }

    private Double prepareCurrentEngineTime(Double hour, Double airFrameTotalTime, Double aircraftInHour) {
        if (Objects.nonNull(hour) && Objects.nonNull(airFrameTotalTime) && Objects.nonNull(aircraftInHour)) {
            return DateUtil.addTimes(DateUtil.subtractTimes(airFrameTotalTime, aircraftInHour), hour);
        }

        return null;
    }

    private Double prepareTSN(Double airFrameTotalTime, AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsTsnAvailable())
                && Objects.nonNull(airFrameTotalTime) && Objects.nonNull(aircraftBuild.getTsnHour())) {
            return DateUtil.addTimes(DateUtil.subtractTimes(airFrameTotalTime, aircraftBuild.getAircraftInHour()),
                    aircraftBuild.getTsnHour());
        }
        return null;
    }

    private Integer prepareCSN(Integer airFrameTotalCycle, AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsTsnAvailable())
                && Objects.nonNull(airFrameTotalCycle) && Objects.nonNull(aircraftBuild.getTsnCycle())) {
            return (airFrameTotalCycle - aircraftBuild.getAircraftInCycle()) + aircraftBuild.getTsnCycle();
        }
        return null;
    }

    private Double prepareTSO(Double airFrameTotalTime, AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsOverhauled())
                && Objects.nonNull(airFrameTotalTime) && Objects.nonNull(aircraftBuild.getTsoHour())) {
            return DateUtil.addTimes(DateUtil.subtractTimes(airFrameTotalTime, aircraftBuild.getAircraftInHour()),
                    aircraftBuild.getTsoHour());
        }
        return null;
    }

    private Integer prepareCSO(Integer airFrameTotalCycle, AircraftBuild aircraftBuild) {
        if (BooleanUtils.isTrue(aircraftBuild.getIsOverhauled())
                && Objects.nonNull(airFrameTotalCycle) && Objects.nonNull(aircraftBuild.getTsoCycle())) {
            return (airFrameTotalCycle - aircraftBuild.getAircraftInCycle()) + aircraftBuild.getTsoCycle();
        }
        return null;
    }
}
