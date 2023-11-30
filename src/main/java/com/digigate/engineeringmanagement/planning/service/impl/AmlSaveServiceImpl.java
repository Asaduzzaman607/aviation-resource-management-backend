package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.constant.MelType;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.entity.DailyUtilization;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftMaintenanceViewModel;
import com.digigate.engineeringmanagement.planning.repository.*;
import com.digigate.engineeringmanagement.planning.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class AmlSaveServiceImpl implements AmlSaveService {

    private final AircraftMaintenanceLogRepository amlRepository;
    private final AmlFlightDataRepository flightDataRepository;
    private final AmlDefectRectificationService defectRectificationService;
    private final AmlOilRecordService oilRecordService;
    private final AircraftService aircraftService;
    private final LdndService ldndService;
    private final AmlValidatorService amlValidatorService;
    private final AcStatisticsService acStatisticsService;

    private final AircraftRepository aircraftRepository;


    protected static final Logger LOGGER = LoggerFactory.getLogger(AircraftMaintenanceLogServiceImpl.class);
    private final AmlDefectRectificationRepository amlDefectRectificationRepository;

    private final MelRepository melRepository;

    private final DailyUtilizationService dailyUtilizationService;

    private final DailyUtilizationRepository dailyUtilizationRepository;


    @Autowired
    public AmlSaveServiceImpl(AircraftMaintenanceLogRepository amlRepository, AmlFlightDataRepository flightDataRepository,
                              AmlDefectRectificationService defectRectificationService, AmlOilRecordService oilRecordService,
                              AircraftService aircraftService, LdndService ldndService, AmlValidatorService amlValidatorService,
                              AcStatisticsService acStatisticsService, AircraftRepository aircraftRepository,
                              AmlDefectRectificationRepository amlDefectRectificationRepository, MelRepository melRepository,
                              DailyUtilizationService dailyUtilizationService, DailyUtilizationRepository dailyUtilizationRepository) {
        this.amlRepository = amlRepository;
        this.flightDataRepository = flightDataRepository;
        this.defectRectificationService = defectRectificationService;
        this.oilRecordService = oilRecordService;
        this.aircraftService = aircraftService;
        this.ldndService = ldndService;
        this.amlValidatorService = amlValidatorService;
        this.acStatisticsService = acStatisticsService;
        this.aircraftRepository = aircraftRepository;
        this.amlDefectRectificationRepository = amlDefectRectificationRepository;
        this.melRepository = melRepository;
        this.dailyUtilizationService = dailyUtilizationService;
        this.dailyUtilizationRepository = dailyUtilizationRepository;
    }


    @Transactional
    @Override
    public AircraftMaintenanceLog createAml(AircraftMaintenanceLogDto amlDto) {

        amlValidatorService.validateMaintenanceLogData(amlDto);

        AircraftMaintenanceLog aircraftMaintenanceLog = amlValidatorService.convertToEntity(amlDto);

        AmlFlightData amlFlightData = convertToFlightData(amlDto, new AmlFlightData());

        aircraftMaintenanceLog = amlRepository.save(aircraftMaintenanceLog);

        amlFlightData.setAircraftMaintenanceLog(aircraftMaintenanceLog);
        amlFlightData = flightDataRepository.save(amlFlightData);

        saveOrUpdateAmlOtherModules(amlDto, aircraftMaintenanceLog);

        AmlOilRecordDto oilRecordDto = amlDto.getAmlOilRecord().getUpLift();

        createDailyUtilization(amlDto, null, mapToAirTimeCycle(amlFlightData, oilRecordDto.getEngineOil1(),
                oilRecordDto.getEngineOil2()));

        updateLdndWithFlightData(amlFlightData, aircraftMaintenanceLog.getDate(), aircraftMaintenanceLog.getAircraft());

        validateAndSaveAcStatData(aircraftMaintenanceLog);
        return aircraftMaintenanceLog;
    }

    @Transactional
    @Override
    public AircraftMaintenanceLog updateAml(AircraftMaintenanceLogDto amlDto, Long id) {

        amlValidatorService.validateMaintenanceLogData(amlDto);
        Optional<AircraftMaintenanceLog> exAircraftMaintenanceLog = amlRepository.findById(id);

        if (exAircraftMaintenanceLog.isPresent()) {
            AircraftMaintenanceLog updatedAml = amlValidatorService.updateEntity(amlDto, exAircraftMaintenanceLog.get());

            Optional<AmlFlightData> exFlightData = flightDataRepository.findByAmlId(updatedAml.getId());

            AmlOilRecordDto oilRecordDto = amlDto.getAmlOilRecord().getUpLift();

            if (exFlightData.isPresent()) {
                AtomicReference<Double> engOil1 = new AtomicReference<>();
                AtomicReference<Double> engOil2 = new AtomicReference<>();
                exAircraftMaintenanceLog.get().getAmlOilRecords().forEach(oilRecord -> {
                    if (oilRecord.getType().equals(OilRecordTypeEnum.UPLIFT)) {
                       engOil1.set(oilRecord.getEngineOil1());
                       engOil2.set(oilRecord.getEngineOil2());
                    }
                });
                DailyAirtimeCycle exAirTimeCycle = mapToAirTimeCycle(exFlightData.get(), engOil1.get(), engOil2.get());

                AmlFlightData amlFlightData = convertToFlightData(amlDto, exFlightData.get());
                updatedAml = amlRepository.save(updatedAml);
                amlFlightData.setAircraftMaintenanceLog(updatedAml);
                amlFlightData = flightDataRepository.save(amlFlightData);

                saveOrUpdateAmlOtherModules(amlDto, updatedAml);

                createDailyUtilization(amlDto, exAirTimeCycle, mapToAirTimeCycle(amlFlightData,
                        oilRecordDto.getEngineOil1(), oilRecordDto.getEngineOil2()));

                updateLdndWithFlightData(amlFlightData, updatedAml.getDate(), updatedAml.getAircraft());

                validateAndSaveAcStatData(updatedAml);
                return updatedAml;
            } else {
                throw EngineeringManagementServerException.notFound(ErrorId.FLIGHT_NOT_FOUND_BY_THIS_AML);
            }

        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.AML_LOG_NOT_EXISTS);
        }

    }

    private DailyAirtimeCycle mapToAirTimeCycle(AmlFlightData amlFlightData, Double engOil1, Double engOil2) {
        return DailyAirtimeCycle.builder()
                .hour(amlFlightData.getAirTime())
                .cycle(amlFlightData.getNoOfLanding())
                .tat(amlFlightData.getGrandTotalAirTime())
                .tac(amlFlightData.getGrandTotalLanding())
                .apuHour(amlFlightData.getApuHours())
                .apuCycle(amlFlightData.getApuCycles())
                .engineOil1(Objects.nonNull(engOil1) ? engOil1 : null)
                .engineOil2(Objects.nonNull(engOil2) ? engOil2 : null)
                .build();
    }

    private void createDailyUtilization(AircraftMaintenanceLogDto amlDto, DailyAirtimeCycle exAirtimeCycle,
                                        DailyAirtimeCycle newAirTimeCycle) {


        if (amlDto.getAmlType().equals(AmlType.REGULAR) || amlDto.getAmlType().equals(AmlType.MAINT)) {
            DailyUtilizationReqDto reqDto = DailyUtilizationReqDto.builder()
                    .aircraftId(amlDto.getAircraftId())
                    .date(amlDto.getDate())
                    .exAirtimeCycle(exAirtimeCycle)
                    .newAirTimeCycle(newAirTimeCycle)
                    .build();

            dailyUtilizationService.createDailyUtilization(reqDto);
        }


    }

    @Override
    @Transactional
    public void deleteAtlInfo(Long aircraftId) {

        AircraftMaintenanceLog secondToLastLog, lastLog;
        List<AircraftMaintenanceLog> aircraftMaintenanceLogs = amlRepository.findAmls(aircraftId);

        int aircraftMaintenanceLogsSize = aircraftMaintenanceLogs.size();

        if (aircraftMaintenanceLogsSize >= 2) {
            secondToLastLog = aircraftMaintenanceLogs.get(aircraftMaintenanceLogsSize - 2);
            lastLog = aircraftMaintenanceLogs.get(aircraftMaintenanceLogsSize - 1);
        } else {
            throw new EngineeringManagementServerException(ErrorId.REQUIRED_AT_LEAST_TWO_ATL_DATA,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        updateAircraftLatestData(secondToLastLog, lastLog);

        Optional<DailyUtilization> exUtilization = dailyUtilizationRepository.findByAircraftIdAndDate(
                lastLog.getAmlAircraftId(), lastLog.getDate());
        exUtilization.ifPresent(dailyUtilization -> updateDailyUtilization(lastLog, dailyUtilization, secondToLastLog));
    }

    private void updateDailyUtilization(AircraftMaintenanceLog lastLog, DailyUtilization exUtilization
            , AircraftMaintenanceLog secondToLastLog) {
        Aircraft aircraft = aircraftService.findById(secondToLastLog.getAmlAircraftId());
        AmlFlightData flightData = secondToLastLog.getFlightData();

        exUtilization.setUsedHours(DateUtil.subtractTimes(NumberUtil.getDefaultIfNull(exUtilization.getUsedHours(), 0.0)
                , lastLog.getFlightData().getAirTime()));
        exUtilization.setUsedCycle(Objects.nonNull(lastLog.getFlightData().getNoOfLanding()) ?
                NumberUtil.getDefaultIfNull(exUtilization.getUsedCycle(), 0) - lastLog.getFlightData().getNoOfLanding() : NumberUtil.getDefaultIfNull(exUtilization.getUsedCycle(), 0));
        if (Objects.nonNull(lastLog.getFlightData().getTotalApuHours()) && Objects.nonNull(lastLog.getFlightData().getTotalApuCycles())) {
            exUtilization.setApuUsedHrs(Objects.nonNull(lastLog.getFlightData().getApuHours()) ?
                    exUtilization.getApuUsedHrs() - lastLog.getFlightData().getApuHours() : exUtilization.getApuUsedHrs());
            exUtilization.setApuUsedCycle(Objects.nonNull(lastLog.getFlightData().getApuCycles()) ?
                    exUtilization.getApuUsedCycle() - lastLog.getFlightData().getApuCycles() : exUtilization.getApuUsedCycle());
        }

        Double totalTimeDifference = DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), flightData.getGrandTotalAirTime());
        Integer totalCycleDifference = aircraft.getAirframeTotalCycle() - flightData.getGrandTotalLanding();
        exUtilization.setTat(DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), totalTimeDifference));
        exUtilization.setTac(aircraft.getAirframeTotalCycle() - totalCycleDifference);
        exUtilization.setDate(lastLog.getDate());
        Boolean isAmlAvailable = amlRepository.findAmlByDate(lastLog.getAmlAircraftId(), lastLog.getDate());
        if (isAmlAvailable) {
            dailyUtilizationService.saveItem(exUtilization);
        } else {
            dailyUtilizationService.deleteItem(exUtilization);
        }
    }


    private void updateAircraftLatestData(AircraftMaintenanceLog secondToLastLog, AircraftMaintenanceLog lastLog) {

        Aircraft aircraft = aircraftService.findById(secondToLastLog.getAmlAircraftId());

        AmlFlightData flightData = secondToLastLog.getFlightData();

        Double totalTimeDifference = DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), flightData.getGrandTotalAirTime());

        Integer totalCycleDifference = aircraft.getAirframeTotalCycle() - flightData.getGrandTotalLanding();

        aircraft.setAirFrameTotalTime(DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), totalTimeDifference));

        aircraft.setAirframeTotalCycle(aircraft.getAirframeTotalCycle() - totalCycleDifference);

        aircraft.setBdTotalTime(DateUtil.subtractTimes(aircraft.getBdTotalTime(), totalTimeDifference));

        aircraft.setBdTotalCycle(aircraft.getBdTotalCycle() - totalCycleDifference);

        aircraft.setTotalApuHours(Objects.nonNull(lastLog.getFlightData().getApuHours()) ? aircraft.getTotalApuHours() -
                lastLog.getFlightData().getApuHours() : aircraft.getTotalApuHours());

        aircraft.setTotalApuCycle(Objects.nonNull(lastLog.getFlightData().getApuCycles()) ? aircraft.getTotalApuCycle()
                - lastLog.getFlightData().getApuCycles() : aircraft.getTotalApuCycle());

        aircraft.setUpdatedAt(secondToLastLog.getDate());

        amlDefectRectificationRepository.findByAircraftMaintenanceLogId(lastLog.getId())
                .ifPresent(amlDefectRectification -> melRepository.findByIntDefRectId(amlDefectRectification.getId())
                        .ifPresent(mel -> melRepository.deleteById(mel.getId())));

        amlRepository.deleteById(lastLog.getId());
        aircraftRepository.save(aircraft);
    }

    private void saveOrUpdateAmlOtherModules(AircraftMaintenanceLogDto aircraftMaintenanceLogDto,
                                             AircraftMaintenanceLog aircraftMaintenanceLog) {
        LOGGER.info("----------In saveOrUpdateAmlOtherModules----------");
        if (aircraftMaintenanceLogDto.getAmlType().equals(AmlType.REGULAR)
                || aircraftMaintenanceLogDto.getAmlType().equals(AmlType.MAINT)) {

            if (BooleanUtils.isTrue(aircraftMaintenanceLogDto.getSaveOilRecord())) {
                AmlRecordRequest amlRecordRequest = aircraftMaintenanceLogDto.getAmlOilRecord();
                AmlOilRecordDto onArrival = amlRecordRequest.getOnArrival();
                if (Objects.nonNull(onArrival)) {
                    onArrival.setAmlId(aircraftMaintenanceLog.getId());
                }
                AmlOilRecordDto upLift = amlRecordRequest.getUpLift();
                if (Objects.nonNull(upLift)) {
                    upLift.setAmlId(aircraftMaintenanceLog.getId());
                }

                if (Objects.nonNull(onArrival.getId())) {
                    oilRecordService.updateAllRecords(aircraftMaintenanceLogDto.getAmlOilRecord(),
                            aircraftMaintenanceLog.getId());
                } else {
                    oilRecordService.saveAllRecords(aircraftMaintenanceLogDto.getAmlOilRecord(),
                            aircraftMaintenanceLog.getId());
                }
            }
            LOGGER.info("----------OIL Record Saved----------amlId:{}", aircraftMaintenanceLog.getId());
            if (aircraftMaintenanceLogDto.getNeedToSaveDefectRectification()) {
                List<AMLDefectRectificationDto> amlDefectRectifications =
                        aircraftMaintenanceLogDto.getDefectRectifications();

                amlDefectRectifications
                        .forEach(amlDefectRectificationDto ->
                                amlDefectRectificationDto.setAmlId(aircraftMaintenanceLog.getId()));

                List<AMLDefectRectificationDto> needToSaveDefectRectifications = amlDefectRectifications.stream()
                        .filter(amlDefectRectificationDto -> Objects.isNull(amlDefectRectificationDto.getId())
                                && StringUtils.isNotBlank(amlDefectRectificationDto.getRectDescription()))
                        .collect(Collectors.toList());

                List<AMLDefectRectificationDto> needToUpdateDefectRectifications = amlDefectRectifications.stream()
                        .filter(amlDefectRectificationDto -> Objects.nonNull(amlDefectRectificationDto.getId())
                                && StringUtils.isNotBlank(amlDefectRectificationDto.getRectDescription()))
                        .collect(Collectors.toList());

                List<Long> needToDeleteDefectRectifications = amlDefectRectifications.stream()
                        .filter(amlDefectRectificationDto -> Objects.nonNull(amlDefectRectificationDto.getId())
                                && StringUtils.isBlank(amlDefectRectificationDto.getRectDescription())
                                && amlDefectRectificationDto.getMelType().equals(MelType.NONE))
                        .map(AMLDefectRectificationDto::getId)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(needToUpdateDefectRectifications)) {
                    defectRectificationService.update(needToUpdateDefectRectifications);
                }

                if (CollectionUtils.isNotEmpty(needToSaveDefectRectifications)) {
                    defectRectificationService.create(needToSaveDefectRectifications);
                }

                if (CollectionUtils.isNotEmpty(needToDeleteDefectRectifications)) {
                    defectRectificationService.deleteDefectAndRectifications(needToDeleteDefectRectifications);
                }
            }
        }
    }


    private AmlFlightData convertToFlightData(AircraftMaintenanceLogDto amlDto, AmlFlightData flightDataEntity) {

        Aircraft aircraft = aircraftService.findById(amlDto.getAircraftId());

        AmlFlightDataDto flightDataDto = amlDto.getAmlFlightData();
        flightDataDto.setPageNo(amlDto.getPageNo());
        flightDataDto.setAlphabet(amlDto.getAlphabet());

        Double airTime = calculateTime(flightDataDto.getTakeOffTime(), flightDataDto.getLandingTime());

        mapToEntityCommonPart(flightDataEntity, flightDataDto);

        if (amlDto.getAmlType().equals(AmlType.REGULAR) || amlDto.getAmlType().equals(AmlType.MAINT)) {
            updateNextAmlInfos(flightDataEntity, flightDataDto, airTime, aircraft.getId());
            updateFlightDataForRegularAml(flightDataEntity, flightDataDto, aircraft, airTime);

        } else {
            flightDataEntity.setGrandTotalLanding(flightDataDto.getTotalLanding());
            flightDataEntity.setGrandTotalAirTime(flightDataDto.getTotalAirTime());
        }
        updateAircraftData(flightDataDto, amlDto, aircraft, flightDataEntity);

        return flightDataEntity;
    }

    private void mapToEntityCommonPart(AmlFlightData amlFlightData, AmlFlightDataDto amlFlightDataDto) {
        amlFlightData.setTotalAirTime(amlFlightDataDto.getTotalAirTime());
        amlFlightData.setTotalLanding(amlFlightDataDto.getTotalLanding());
        amlFlightData.setTotalApuHours(amlFlightDataDto.getTotalApuHours());
        amlFlightData.setTotalApuCycles(amlFlightDataDto.getTotalApuCycles());
        amlFlightData.setCommencedTime(amlFlightDataDto.getCommencedTime());
        amlFlightData.setCompletedTime(amlFlightDataDto.getCompletedTime());
    }

    private void updateFlightDataForRegularAml(AmlFlightData amlFlightData, AmlFlightDataDto amlFlightDataDto,
                                               Aircraft aircraft, Double airTime) {

        Double blockTime = calculateTime(amlFlightDataDto.getBlockOffTime(), amlFlightDataDto.getBlockOnTime());

        aircraft.setAirFrameTotalTime(DateUtil.addTimes(DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(),
                NumberUtil.getDefaultIfNull(amlFlightData.getAirTime(), 0.0)), airTime));

        aircraft.setAirframeTotalCycle(aircraft.getAirframeTotalCycle() -
                NumberUtil.getDefaultIfNull(amlFlightData.getNoOfLanding(), 0)
                + NumberUtil.getDefaultIfNull(amlFlightDataDto.getNoOfLanding(), 0));

        aircraft.setBdTotalTime(Math.max(0.0,
                DateUtil.addTimes(DateUtil.subtractTimes(NumberUtil.getDefaultIfNull(aircraft.getBdTotalTime(), 0.0),
                        amlFlightData.getAirTime()), airTime)));

        aircraft.setBdTotalCycle(Math.max(0,
                NumberUtil.getDefaultIfNull(aircraft.getBdTotalCycle(), 0) -
                        NumberUtil.getDefaultIfNull(amlFlightData.getNoOfLanding(), 0)
                        + NumberUtil.getDefaultIfNull(amlFlightDataDto.getNoOfLanding(), 0)));

        amlFlightData.setBlockOnTime(amlFlightDataDto.getBlockOnTime());
        amlFlightData.setBlockOffTime(amlFlightDataDto.getBlockOffTime());
        amlFlightData.setLandingTime(amlFlightDataDto.getLandingTime());
        amlFlightData.setTakeOffTime(amlFlightDataDto.getTakeOffTime());
        amlFlightData.setBlockTime(blockTime);
        amlFlightData.setAirTime(airTime);
        amlFlightData.setNoOfLanding(amlFlightDataDto.getNoOfLanding());
        amlFlightData.setGrandTotalAirTime(DateUtil.addTimes(NumberUtil.
                getDefaultIfNull(amlFlightDataDto.getTotalAirTime(), 0.0), airTime));
        amlFlightData.setGrandTotalLanding(NumberUtil.getDefaultIfNull(amlFlightData.getNoOfLanding(), 0)
                + NumberUtil.getDefaultIfNull(amlFlightData.getTotalLanding(), 0));
    }

    private void updateAircraftData(AmlFlightDataDto amlFlightDataDto, AircraftMaintenanceLogDto amlDto,
                                    Aircraft aircraft, AmlFlightData flightDataEntity) {

        if (Objects.nonNull(amlFlightDataDto.getTotalApuHours())) {
            if (amlFlightDataDto.getTotalApuHours() >= aircraft.getTotalApuHours()) {
                flightDataEntity.setApuHours(amlFlightDataDto.getTotalApuHours() - aircraft.getTotalApuHours());
                aircraft.setTotalApuHours(amlFlightDataDto.getTotalApuHours());
            }
        }

        if (Objects.nonNull(amlFlightDataDto.getTotalApuCycles())) {
            if (amlFlightDataDto.getTotalApuCycles() >= aircraft.getTotalApuCycle()) {
                flightDataEntity.setApuCycles(amlFlightDataDto.getTotalApuCycles() - aircraft.getTotalApuCycle());
                aircraft.setTotalApuCycle(amlFlightDataDto.getTotalApuCycles());
            }
        }


        Page<AircraftMaintenanceViewModel> aircraftMaintenanceViewModel = amlRepository.findTopAml(aircraft.getId(),
                PageRequest.of(0, 1));

        if (CollectionUtils.isNotEmpty(aircraftMaintenanceViewModel.getContent())) {
            AircraftMaintenanceViewModel checkAircraftMaintenanceViewModel =
                    aircraftMaintenanceViewModel.getContent().get(0);
            if (amlDto.getPageNo() >= checkAircraftMaintenanceViewModel.getPageNo()) {
                aircraft.setUpdatedAt(amlDto.getDate());
            }
        }
    }

    private void updateNextAmlInfos(AmlFlightData entity, AmlFlightDataDto dto, Double airTime, Long aircraftId) {

        if (Objects.nonNull(entity.getId())) {
            int updatedLandingDiff = NumberUtil.getDefaultIfNull(dto.getNoOfLanding(), 0);

            if (Objects.nonNull(entity.getNoOfLanding())) {
                updatedLandingDiff -= entity.getNoOfLanding();
            }

            if (!Objects.equals(airTime, dto.getTotalAirTime()) || updatedLandingDiff != 0) {

                Optional<AircraftMaintenanceLog> currentAml = amlRepository.findByIdAndIsActiveTrue(entity.getAmlId());

                if (currentAml.isPresent()) {
                    List<AircraftMaintenanceLog> aircraftMaintenanceLogList = amlRepository.findAllNextAmlsWithCurrentAml(
                            currentAml.get().getPageNo(), currentAml.get().getAmlAircraftId());

                    if (aircraftMaintenanceLogList.size() > 1) {
                        int currentAmlIndex = aircraftMaintenanceLogList.indexOf(currentAml.get());

                        List<AircraftMaintenanceLog> nextAmlList = aircraftMaintenanceLogList.subList(currentAmlIndex + 1,
                                aircraftMaintenanceLogList.size());

                        updateNextAmlFlightDataInfo(entity, airTime, updatedLandingDiff, nextAmlList);
                    }
                }
//                TODO
            }

        } else if (Objects.isNull(entity.getId()) && Objects.nonNull(dto.getPageNo()) && Objects.nonNull(dto.getAlphabet())) {

            List<AircraftMaintenanceLog> nextAmlList = amlRepository.findAllNextAmls(dto.getPageNo(), aircraftId);

            if (CollectionUtils.isNotEmpty(nextAmlList)) {
                updateNextAmlFlightDataInfo(entity, airTime, dto.getNoOfLanding(), nextAmlList);
            }
        }
    }

    private void updateNextAmlFlightDataInfo(AmlFlightData amlFlightData, Double airTime, Integer landing,
                                             List<AircraftMaintenanceLog> needToUpdateLog) {
        Double airTimeToUpdate;
        if (Objects.nonNull(amlFlightData.getId())) {
            airTimeToUpdate = DateUtil.subtractTimes(airTime, amlFlightData.getAirTime());
        } else {
            airTimeToUpdate = airTime;
        }

        for (AircraftMaintenanceLog log : needToUpdateLog) {
            if (Objects.isNull(log.getFlightData())) {
                continue;
            }

            if (airTimeToUpdate < 0) {
                log.getFlightData().setTotalAirTime(
                        DateUtil.subtractTimes(log.getFlightData().getTotalAirTime(), airTimeToUpdate * (-1)));
                log.getFlightData().setGrandTotalAirTime(
                        DateUtil.subtractTimes(log.getFlightData().getGrandTotalAirTime(), airTimeToUpdate * (-1)));
            } else if (airTimeToUpdate > 0) {
                log.getFlightData().setTotalAirTime(DateUtil.addTimes(log.getFlightData().getTotalAirTime(), airTimeToUpdate));
                log.getFlightData().setGrandTotalAirTime(DateUtil.addTimes(log.getFlightData().getGrandTotalAirTime(),
                        airTimeToUpdate));
            }

            if (NumberUtil.getDefaultIfNull(landing, 0) != 0) {
                log.getFlightData().setTotalLanding(
                        NumberUtil.getDefaultIfNull(log.getFlightData().getTotalLanding(), 0) + landing);
                log.getFlightData().setGrandTotalLanding(
                        NumberUtil.getDefaultIfNull(log.getFlightData().getGrandTotalLanding(), 0) + landing);
            }
        }

        if (CollectionUtils.isNotEmpty(needToUpdateLog)) {
            this.saveItemList(needToUpdateLog);
        }
    }

    private void updateLdndWithFlightData(AmlFlightData amlFlightData, LocalDate amlDate, Aircraft aircraft) {
        if (Objects.nonNull(amlFlightData.getAirTime()) ||
                Objects.nonNull(amlFlightData.getLandingTime()) || Objects.nonNull(amlFlightData.getApuHours()) ||
                Objects.nonNull(amlFlightData.getApuCycles())) {
            try {
                ldndService.updateWithAmlFlightData(aircraft, amlFlightData, amlDate);
            } catch (Exception e) {
                throw new EngineeringManagementServerException(ErrorId.LDND_UPDATE_BY_ATL_IS_FAILED,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
    }

    private Double calculateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Long duration = this.duration(startDateTime, endDateTime);
        return Objects.isNull(duration) ? 0.00 : DateUtil.convertMinutesToHour(duration);
    }


    public Long duration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            LOGGER.info("start/end date time is null");
            return null;
        }
        return DateUtil.convertToMinutes(endDateTime) - DateUtil.convertToMinutes(startDateTime);
    }

    private void validateAndSaveAcStatData(AircraftMaintenanceLog aml) {
        if (aml.getAmlType().equals(AmlType.REGULAR) &&
                DateUtil.isBeforeCurrentMonth(aml.getDate())) {
            acStatisticsService.updateOrSaveAcStateWithOldAml(aml.getAircraft().getAircraftModelId(), aml.getDate());
        }
    }


    public List<AircraftMaintenanceLog> saveItemList(List<AircraftMaintenanceLog> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
            return amlRepository.saveAll(entityList);
        } catch (Exception e) {
            String entityName = entityList.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    entityName));
        }
    }

}
