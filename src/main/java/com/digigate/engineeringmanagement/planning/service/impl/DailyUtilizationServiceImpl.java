package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.NumberConstant;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.AircraftInfoViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.entity.DailyUtilization;
import com.digigate.engineeringmanagement.planning.entity.MonthlyUtilization;
import com.digigate.engineeringmanagement.planning.payload.request.DailyAirtimeCycle;
import com.digigate.engineeringmanagement.planning.payload.request.DailyUtilizationReqDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftBuildRepository;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.repository.DailyUtilizationRepository;
import com.digigate.engineeringmanagement.planning.repository.MonthlyUtilizationRepository;
import com.digigate.engineeringmanagement.planning.service.DailyUtilizationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DailyUtilizationServiceImpl implements DailyUtilizationService {

    private final DailyUtilizationRepository dailyUtilizationRepository;
    private final MonthlyUtilizationRepository monthlyUtilizationRepository;
    private final AircraftBuildRepository aircraftBuildRepository;
    private final AircraftMaintenanceLogRepository amlRepository;

    private final AircraftService aircraftService;
    private final AircraftRepository aircraftRepository;

    protected static final Logger LOGGER = LoggerFactory.getLogger(AircraftMaintenanceLogServiceImpl.class);

    private static final String INITIAL_DELAY_VAL = "80000";

    private static final String FIXED_DELAY_VAL = "86400000";

    private static final String LH_POSITION = "LH";

    private static final String RH_POSITION = "RH";


    @Autowired
    public DailyUtilizationServiceImpl(DailyUtilizationRepository dailyUtilizationRepository,
                                       MonthlyUtilizationRepository monthlyUtilizationRepository,
                                       AircraftBuildRepository aircraftBuildRepository,
                                       AircraftMaintenanceLogRepository amlRepository, AircraftService aircraftService,
                                       AircraftRepository aircraftRepository) {
        this.dailyUtilizationRepository = dailyUtilizationRepository;
        this.monthlyUtilizationRepository = monthlyUtilizationRepository;
        this.aircraftBuildRepository = aircraftBuildRepository;
        this.amlRepository = amlRepository;
        this.aircraftService = aircraftService;
        this.aircraftRepository = aircraftRepository;
    }


    @Override
    public void createDailyUtilization(DailyUtilizationReqDto reqDto) {

        Optional<DailyUtilization> exUtilization = dailyUtilizationRepository.findByAircraftIdAndDate(
                reqDto.getAircraftId(), reqDto.getDate());

        if (exUtilization.isPresent()) {
            updateUtilization(reqDto, exUtilization.get());
        } else {
            createNew(reqDto);
        }
    }

    @Override
    public void createNew(DailyUtilizationReqDto reqDto) {
        DailyUtilization dailyUtilization = new DailyUtilization();
        Aircraft aircraft = aircraftService.findById(reqDto.getAircraftId());
        dailyUtilization.setAircraft(aircraft);
        dailyUtilization.setDate(reqDto.getDate());

        dailyUtilization.setUsedHours(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getHour(), 0.0));

        dailyUtilization.setUsedCycle(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getCycle(), 0));

        //if apu hour available for this aircraft
        if (Objects.nonNull(dailyUtilization.getAircraft().getTotalApuHours())) {
            dailyUtilization.setApuUsedHrs(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getApuHour(),
                    0.0));
        }

        //if apu cycle available for this aircraft
        if (Objects.nonNull(dailyUtilization.getAircraft().getTotalApuCycle())) {
            dailyUtilization.setApuUsedCycle(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getApuCycle(),
                    0));
        }

        dailyUtilization.setTat(reqDto.getNewAirTimeCycle().getTat());
        dailyUtilization.setTac(reqDto.getNewAirTimeCycle().getTac());

        dailyUtilization.setEng1OilUplift(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getEngineOil1(), 0.0));
        dailyUtilization.setEng2OilUplift(NumberUtil.getDefaultIfNull(reqDto.getNewAirTimeCycle().getEngineOil2(), 0.0));

        saveItem(dailyUtilization);
    }

    @Override
    public void updateUtilization(DailyUtilizationReqDto reqDto, DailyUtilization dailyUtilization) {

        DailyAirtimeCycle newAirTimeCycle = reqDto.getNewAirTimeCycle();

        DailyAirtimeCycle exAirTimeCycle = reqDto.getExAirtimeCycle();

        Double usedHour = NumberUtil.getDefaultIfNull(newAirTimeCycle.getHour(), 0.0);
        Integer usedCycle = NumberUtil.getDefaultIfNull(newAirTimeCycle.getCycle(), 0);

        Double engOil1 = NumberUtil.getDefaultIfNull(newAirTimeCycle.getEngineOil1(), 0.0);
        Double engOil2 = NumberUtil.getDefaultIfNull(newAirTimeCycle.getEngineOil2(), 0.0);

        Double apuUsedHour = NumberUtil.getDefaultIfNull(newAirTimeCycle.getApuHour(), 0.0);
        Integer apuUsedCycle = NumberUtil.getDefaultIfNull(newAirTimeCycle.getApuCycle(), 0);

        Double airTimeToUpdate = usedHour;

        Integer cycleToUpdate = usedCycle;

        if (Objects.nonNull(exAirTimeCycle)) {

            if (Objects.nonNull(exAirTimeCycle.getHour())) {
                airTimeToUpdate = DateUtil.subtractTimes(usedHour, NumberUtil.getDefaultIfNull(exAirTimeCycle.getHour(),
                        0.0));
            }

            if (Objects.nonNull(exAirTimeCycle.getCycle())) {
                cycleToUpdate = usedCycle - NumberUtil.getDefaultIfNull(exAirTimeCycle.getCycle(), 0);
            }

            if (Objects.nonNull(exAirTimeCycle.getEngineOil1())) {
                engOil1 = engOil1 - NumberUtil.getDefaultIfNull(exAirTimeCycle.getEngineOil1(), 0.0);
            }

            if (Objects.nonNull(exAirTimeCycle.getEngineOil2())) {
                engOil2 = engOil2 - NumberUtil.getDefaultIfNull(exAirTimeCycle.getEngineOil2(), 0.0);
            }
        }

        if (airTimeToUpdate > 0) {
            dailyUtilization.setUsedHours(DateUtil.addTimes(dailyUtilization.getUsedHours(), airTimeToUpdate));
        } else {
            dailyUtilization.setUsedHours(DateUtil.subtractTimes(dailyUtilization.getUsedHours(), airTimeToUpdate));
        }

        dailyUtilization.setUsedCycle(dailyUtilization.getUsedCycle() + cycleToUpdate);

        if (Objects.nonNull(dailyUtilization.getEng1OilUplift())) {
            dailyUtilization.setEng1OilUplift(dailyUtilization.getEng1OilUplift() + engOil1);
        }

        if (Objects.nonNull(dailyUtilization.getEng1OilUplift())) {
            dailyUtilization.setEng2OilUplift(dailyUtilization.getEng2OilUplift() + engOil2);
        }

        //if apu hour available for this aircraft
        if (Objects.nonNull(dailyUtilization.getAircraft().getTotalApuHours())) {
            dailyUtilization.setApuUsedHrs(NumberUtil.getDefaultIfNull(dailyUtilization.getApuUsedHrs(), 0.0)
                    + apuUsedHour);
        }

        //if apu cycle available for this aircraft
        if (Objects.nonNull(dailyUtilization.getAircraft().getTotalApuCycle())) {
            dailyUtilization.setApuUsedCycle(NumberUtil.getDefaultIfNull(dailyUtilization.getApuUsedCycle(), 0)
                    + apuUsedCycle);
        }

        AmlTatTacDto tatTacDto = amlRepository.finMaxTatTacByDate(reqDto.getAircraftId(), reqDto.getDate());
        dailyUtilization.setTat(tatTacDto.getTat());
        dailyUtilization.setTac(tatTacDto.getTac());
        dailyUtilization.setDate(reqDto.getDate());

        saveItem(dailyUtilization);

        updateNextUtilization(airTimeToUpdate, cycleToUpdate, reqDto.getDate(),
                reqDto.getAircraftId());
    }

    private void updateNextUtilization(Double airTimeToUpdate, Integer cycleToUpdate, LocalDate date, Long aircraftId) {

        List<DailyUtilization> needToUpdateDailyUtils = dailyUtilizationRepository.findNextDailyUtils(aircraftId, date);

        needToUpdateDailyUtils.forEach(d -> {

            if (Objects.nonNull(d.getTat())) {

                if (airTimeToUpdate > 0) {
                    d.setTat(DateUtil.addTimes(d.getTat(), airTimeToUpdate));
                } else {
                    d.setTat(DateUtil.subtractTimes(d.getTat(), airTimeToUpdate));
                }
            }

            if (Objects.nonNull(d.getTac())) {
                d.setTac(d.getTac() + cycleToUpdate);
            }
        });

        dailyUtilizationRepository.saveAll(needToUpdateDailyUtils);
    }


    @Override
    public DailyUtilization saveItem(DailyUtilization dailyUtilization) {
        try {
            return dailyUtilizationRepository.save(dailyUtilization);
        } catch (Exception e) {
            String name = dailyUtilization.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    @Override
    public DailyUtilizationReportList getDailyUtilizationReport(LocalDate fromDate, LocalDate toDate, Long aircraftId) {

        List<DailyUtilizationReport> dailyUtilizationReports =
                dailyUtilizationRepository.findDailyUtilizationReports(aircraftId, fromDate, toDate);

        List<AcBuildInHourInfo> acBuildInHourInfos = aircraftBuildRepository.findInHourInfoByModelTypes(
                aircraftId, ModelType.getEngineLandingGearApu(), fromDate, toDate);

        List<AcBuildInHourInfo> engineLeftList = new LinkedList<>();
        List<AcBuildInHourInfo> engineRightList = new LinkedList<>();
        List<AcBuildInHourInfo> mlgRightList = new LinkedList<>();
        List<AcBuildInHourInfo> mlgLeftList = new LinkedList<>();
        List<AcBuildInHourInfo> nlgList = new LinkedList<>();
        List<AcBuildInHourInfo> apuList = new LinkedList<>();


        acBuildInHourInfos.forEach(acBuildInHourInfo -> {
            if (acBuildInHourInfo.getModelType().equals(ModelType.ENGINE)) {
                if (acBuildInHourInfo.getPosition().startsWith(LH_POSITION)) {
                    engineLeftList.add(acBuildInHourInfo);
                } else if (acBuildInHourInfo.getPosition().startsWith(RH_POSITION)) {
                    engineRightList.add(acBuildInHourInfo);
                }

            } else if (acBuildInHourInfo.getModelType().equals(ModelType.NLG)) {
                nlgList.add(acBuildInHourInfo);

            } else if (acBuildInHourInfo.getModelType().equals(ModelType.MLG)) {
                if (acBuildInHourInfo.getPosition().startsWith(LH_POSITION)) {
                    mlgLeftList.add(acBuildInHourInfo);
                } else if (acBuildInHourInfo.getPosition().startsWith(RH_POSITION)) {
                    mlgRightList.add(acBuildInHourInfo);
                }
            } else if (acBuildInHourInfo.getModelType().equals(ModelType.APU)) {
                apuList.add(acBuildInHourInfo);
            }
        });

        DailyUtilizationReportList dailyUtilizationReportList = new DailyUtilizationReportList();
        dailyUtilizationReportList.setDailyUtilizationReport(dailyUtilizationReports);
        AircraftInfoViewModel aircraftInfoViewModel = aircraftService.findAircraftInfoData(aircraftId);
        dailyUtilizationReportList.setTotalTat(aircraftInfoViewModel.getAcHour());
        dailyUtilizationReportList.setTotalTac(aircraftInfoViewModel.getAcCycle());
        dailyUtilizationReportList.setAsOfDate(aircraftInfoViewModel.getUpdatedTime());
        dailyUtilizationReportList.setAircraftName(aircraftInfoViewModel.getAircraftName());
        dailyUtilizationReportList.setAircraftSerial(aircraftInfoViewModel.getAirframeSerial());


        int engLeftIndex = 0;
        int engRightIndex = 0;
        int mlgLeftIndex = 0;
        int mlgRightIndex = 0;
        int nlgIndex = 0;
        int apuIndex = 0;

        AcBuildInHourInfo engineLeftCurrentData = new AcBuildInHourInfo();
        AcBuildInHourInfo engineRightCurrentData = new AcBuildInHourInfo();
        AcBuildInHourInfo mlgLeftCurrentData = new AcBuildInHourInfo();
        AcBuildInHourInfo mlgRightCurrentData = new AcBuildInHourInfo();
        AcBuildInHourInfo nlgCurrentData = new AcBuildInHourInfo();
        AcBuildInHourInfo apuCurrentData = new AcBuildInHourInfo();


        if (CollectionUtils.isNotEmpty(engineLeftList)) {
            engineLeftCurrentData = engineLeftList.get(engLeftIndex);
        }

        if (CollectionUtils.isNotEmpty(engineRightList)) {
            engineRightCurrentData = engineRightList.get(engRightIndex);
        }

        if (CollectionUtils.isNotEmpty(mlgLeftList)) {
            mlgLeftCurrentData = mlgLeftList.get(mlgLeftIndex);
        }

        if (CollectionUtils.isNotEmpty(mlgRightList)) {
            mlgRightCurrentData = mlgRightList.get(mlgRightIndex);
        }

        if (CollectionUtils.isNotEmpty(nlgList)) {
            nlgCurrentData = nlgList.get(nlgIndex);
        }

        if (CollectionUtils.isNotEmpty(apuList)) {
            apuCurrentData = apuList.get(apuIndex);
        }

        if (CollectionUtils.isNotEmpty(dailyUtilizationReports)) {
            double totalHour = 0;
            int totalCycle = 0;

            Double bfTat = NumberUtil.parseValue(NumberUtil.formatDecimalValue(
                    DateUtil.subtractTimes(NumberUtil.getDefaultIfNull(dailyUtilizationReports.get(0).getTat(), 0.0),
                            NumberUtil.getDefaultIfNull(dailyUtilizationReports.get(0).getUsedHrs(), 0.0)),
                    NumberConstant.TWO_DECIMAL_FORMAT), Double.class);

            Integer bfTac = NumberUtil.getDefaultIfNull(dailyUtilizationReports.get(0).getTac(), 0)
                    - NumberUtil.getDefaultIfNull(dailyUtilizationReports.get(0).getUsedCyc(), 0);

            setInitialTsnCsn(engineLeftCurrentData, bfTat, bfTac);

            setInitialTsnCsn(engineRightCurrentData, bfTat, bfTac);

            setInitialTsnCsn(mlgLeftCurrentData, bfTat, bfTac);

            setInitialTsnCsn(mlgRightCurrentData, bfTat, bfTac);

            setInitialTsnCsn(nlgCurrentData, bfTat, bfTac);

            setInitialApuTsnCsn(apuCurrentData, bfTat, bfTac);

            for (DailyUtilizationReport data : dailyUtilizationReports) {

                engineLeftCurrentData = checkAndUpdate(engineLeftCurrentData, data, engineLeftList, engLeftIndex);

                engineRightCurrentData = checkAndUpdate(engineRightCurrentData, data, engineRightList, engRightIndex);

                mlgLeftCurrentData = checkAndUpdate(mlgLeftCurrentData, data, mlgLeftList, mlgLeftIndex);

                mlgRightCurrentData = checkAndUpdate(mlgRightCurrentData, data, mlgRightList, mlgRightIndex);

                nlgCurrentData = checkAndUpdate(nlgCurrentData, data, nlgList, nlgIndex);

                apuCurrentData = checkAndUpdate(apuCurrentData, data, apuList, apuIndex);

                // TSN Add
                if (Objects.nonNull(engineLeftCurrentData.getTsn())) {
                    engineLeftCurrentData.setTsn(DateUtil.addTimes(engineLeftCurrentData.getTsn(), data.getUsedHrs()));
                }

                if (Objects.nonNull(engineRightCurrentData.getTsn())) {
                    engineRightCurrentData.setTsn(DateUtil.addTimes(engineRightCurrentData.getTsn(), data.getUsedHrs()));
                }

                if (Objects.nonNull(nlgCurrentData.getTsn())) {
                    nlgCurrentData.setTsn(DateUtil.addTimes(nlgCurrentData.getTsn(), data.getUsedHrs()));
                }

                if (Objects.nonNull(mlgLeftCurrentData.getTsn())) {
                    mlgLeftCurrentData.setTsn(DateUtil.addTimes(mlgLeftCurrentData.getTsn(), data.getUsedHrs()));
                }

                if (Objects.nonNull(mlgRightCurrentData.getTsn())) {
                    mlgRightCurrentData.setTsn(DateUtil.addTimes(mlgRightCurrentData.getTsn(), data.getUsedHrs()));
                }

                if (Objects.nonNull(apuCurrentData.getTsn()) && Objects.nonNull(data.getApuUsedCycle())) {
                    apuCurrentData.setTsn(apuCurrentData.getTsn() + data.getApuUsedHrs());
                }

               // CSN Add
                if (Objects.nonNull(engineLeftCurrentData.getCsn())) {
                    engineLeftCurrentData.setCsn(engineLeftCurrentData.getCsn() + data.getUsedCyc());
                }

                if (Objects.nonNull(engineRightCurrentData.getCsn())) {
                    engineRightCurrentData.setCsn(engineRightCurrentData.getCsn() + data.getUsedCyc());
                }

                if (Objects.nonNull(nlgCurrentData.getCsn())) {
                    nlgCurrentData.setCsn(nlgCurrentData.getCsn() + data.getUsedCyc());
                }

                if (Objects.nonNull(mlgLeftCurrentData.getCsn())) {
                    mlgLeftCurrentData.setCsn(mlgLeftCurrentData.getCsn() + data.getUsedCyc());
                }

                if (Objects.nonNull(mlgRightCurrentData.getCsn())) {
                    mlgRightCurrentData.setCsn(mlgRightCurrentData.getCsn() + data.getUsedCyc());
                }

                if (Objects.nonNull(apuCurrentData.getCsn()) && Objects.nonNull(data.getApuUsedCycle())) {
                    apuCurrentData.setCsn(apuCurrentData.getCsn() + data.getApuUsedCycle());
                }

//                if(Objects.isNull(data.getEng1OilUplift())){
//                    data.setEng1OilUplift(0.0);
//                }
//
//                if(Objects.isNull(data.getEng2OilUplift())){
//                    data.setEng2OilUplift(0.0);
//                }
//
//
//                if(Objects.isNull(data.getApuUsedHrs())){
//                    data.setApuUsedHrs(0.0);
//                }
//
//                if(Objects.isNull(data.getApuUsedCycle())){
//                    data.setApuUsedCycle(0);
//                }

                data.setEng1Tsn(engineLeftCurrentData.getTsn());
                data.setEng2Tsn(engineRightCurrentData.getTsn());
                data.setNlgTsn(nlgCurrentData.getTsn());
                data.setLhMlgTsn(mlgLeftCurrentData.getTsn());
                data.setRhMlgTsn(mlgRightCurrentData.getTsn());
                data.setApuTsn(apuCurrentData.getTsn());

                data.setEng1Csn(engineLeftCurrentData.getCsn());
                data.setEng2Csn(engineRightCurrentData.getCsn());
                data.setNlgCsn(nlgCurrentData.getCsn());
                data.setLhMlgCsn(mlgLeftCurrentData.getCsn());
                data.setRhMlgCsn(mlgRightCurrentData.getCsn());
                data.setApuCsn(apuCurrentData.getCsn());

                totalHour = DateUtil.addTimes(NumberUtil.getDefaultIfNull(data.getUsedHrs(), 0.0), totalHour);
                totalCycle = NumberUtil.getDefaultIfNull(data.getUsedCyc(), 0) + totalCycle;
            }
            dailyUtilizationReportList.setTotalFH(NumberUtil.parseValue((NumberUtil.formatDecimalValue(totalHour, NumberConstant.TWO_DECIMAL_FORMAT)), Double.class));
            dailyUtilizationReportList.setTotalFC(totalCycle);
        }
        return dailyUtilizationReportList;
    }

    private AcBuildInHourInfo checkAndUpdate(AcBuildInHourInfo currentData, DailyUtilizationReport data,
                                             List<AcBuildInHourInfo> dataList, int index) {

        if (Objects.nonNull(currentData.getOutDate()) &&
                currentData.getOutDate().isBefore(data.getDate())) {

            currentData = dataList.get(++index);

            currentData.setTsn(DateUtil.addTimes(currentData.getTsn(), DateUtil.subtractTimes(
                    data.getTat(), currentData.getAcInHour())));

            currentData.setCsn(currentData.getCsn() + data.getTac() -
                    currentData.getAcInCycle());
        }
        return currentData;
    }


    private void setInitialTsnCsn(AcBuildInHourInfo currentData, Double bfTat, Integer bfTac) {
        // TSN
        if (Objects.nonNull(currentData.getAcInHour())) {
            currentData.setTsn(DateUtil.addTimes(currentData.getTsn(), DateUtil.subtractTimes(bfTat, currentData.getAcInHour())));
        }

        // CSN
        if (Objects.nonNull(currentData.getAcInCycle())) {
            currentData.setCsn(currentData.getCsn() + bfTac - currentData.getAcInCycle());
        }
    }

    private void setInitialApuTsnCsn(AcBuildInHourInfo currentData, Double bfTat, Integer bfTac) {
        // TSN
        if (Objects.nonNull(currentData.getAcInHour())) {
            currentData.setTsn(currentData.getTsn() +  bfTat - currentData.getAcInHour());
        }

        // CSN
        if (Objects.nonNull(currentData.getAcInCycle())) {
            currentData.setCsn(currentData.getCsn() + bfTac - currentData.getAcInCycle());
        }
    }


    @Override
    public List<MonthlyUtilizationReport> getMonthlyUtilizationReport(LocalDate fromDate, LocalDate toDate, Long
            aircraftId) {

        YearMonth from = YearMonth.of(fromDate.getYear(), fromDate.getMonth());
        YearMonth to = YearMonth.of(toDate.getYear(), toDate.getMonth());
        return monthlyUtilizationRepository.findMonthlyUtilizationReports(aircraftId, from, to);
    }

    @Override
    public List<YearlyUtilizationReport> getYearlyUtilizationReport(LocalDate fromDate, LocalDate toDate, Long
            aircraftId) {

        YearMonth from = YearMonth.of(fromDate.getYear(), fromDate.getMonth());
        YearMonth to = YearMonth.of(toDate.getYear(), toDate.getMonth());
        List<MonthlyUtilizationReport> monthlyReports = monthlyUtilizationRepository.findMonthlyUtilizationReports(
                aircraftId, from, to);

        Map<Integer, List<MonthlyUtilizationReport>> listMap = monthlyReports.stream().collect(
                Collectors.groupingBy(m -> m.getYearMonth().getYear()));

        List<YearlyUtilizationReport> yearlyUtilizationReports = new ArrayList<>();

        listMap.forEach((key, value) -> {

            AtomicReference<Double> acHour = new AtomicReference<>(0.0);
            AtomicReference<Integer> acCycle = new AtomicReference<>(0);
            AtomicReference<Double> apuHour = new AtomicReference<>(0.0);
            AtomicReference<Integer> apuCycle = new AtomicReference<>(0);

            value.forEach(d -> {
                acHour.set(DateUtil.addTimes(acHour.get(), NumberUtil.getDefaultIfNull(d.getAcHours(), 0.0)));
                apuHour.set(DateUtil.addTimes(apuHour.get(), NumberUtil.getDefaultIfNull(d.getApuHours(), 0.0)));
                acCycle.set(acCycle.get() + NumberUtil.getDefaultIfNull(d.getAcCycle(), 0));
                apuCycle.set(apuCycle.get() + NumberUtil.getDefaultIfNull(d.getApuCycle(), 0));
            });

            yearlyUtilizationReports.add(YearlyUtilizationReport.builder()
                    .acHours(acHour.get())
                    .acCycle(acCycle.get())
                    .apuHours(apuHour.get())
                    .apuCycle(apuCycle.get())
                    .year(key)
                    .ratio(acHour.get() / acCycle.get())
                    .build());
        });
        return yearlyUtilizationReports;
    }

    @Override
    @Scheduled(initialDelayString = INITIAL_DELAY_VAL, fixedDelayString = FIXED_DELAY_VAL)
    @Transactional
    public void generateAndSaveMonthlyUtilization() {


        List<Aircraft> aircraftList = aircraftRepository.findAllByIsActive(true);

        List<MonthlyUtilization> monthlyUtilizationList = new ArrayList<>();

        aircraftList.forEach(aircraft -> {

            List<MonthlyUtilizationReport> monthlyUtilizationReportList = dailyUtilizationRepository
                    .findDailyUtilizationByDate(aircraft.getId(), DateUtil.getCurrentUTCDate());


            Map<YearMonth, List<MonthlyUtilizationReport>> listMap = monthlyUtilizationReportList.stream().collect(
                    Collectors.groupingBy(m -> YearMonth.of(m.getDate().getYear(), m.getDate().getMonth())));

            listMap.forEach((key, value) -> {

                AtomicReference<Double> acHour = new AtomicReference<>(0.0);
                AtomicReference<Integer> acCycle = new AtomicReference<>(0);
                AtomicReference<Double> apuHour = new AtomicReference<>(0.0);
                AtomicReference<Integer> apuCycle = new AtomicReference<>(0);

                value.forEach(d -> {
                    acHour.set(DateUtil.addTimes(acHour.get(), NumberUtil.getDefaultIfNull(d.getAcHours(), 0.0)));
                    apuHour.set(DateUtil.addTimes(apuHour.get(), NumberUtil.getDefaultIfNull(d.getApuHours(), 0.0)));
                    acCycle.set(acCycle.get() + NumberUtil.getDefaultIfNull(d.getAcCycle(), 0));
                    apuCycle.set(apuCycle.get() + NumberUtil.getDefaultIfNull(d.getApuCycle(), 0));
                });

                double ratio = 0.0;
                if (Objects.nonNull(acHour.get()) && Objects.nonNull(acCycle.get()) && acHour.get() != 0.0 && acCycle.get() != 0) {
                    ratio = acHour.get() / acCycle.get();
                }
                monthlyUtilizationList.add(MonthlyUtilization.builder()
                        .acHours(acHour.get())
                        .acCycle(acCycle.get())
                        .apuHrs(apuHour.get())
                        .apuCycle(apuCycle.get())
                        .aircraft(aircraft)
                        .yearMonth(key)
                        .ratio(ratio)
                        .build());
            });
        });

        List<MonthlyUtilization> sortedList = monthlyUtilizationList.stream()
                .sorted(Comparator.comparing(MonthlyUtilization::getYearMonth))
                .collect(Collectors.toList());

        saveItemList(sortedList);

    }


    public List<MonthlyUtilization> saveItemList(List<MonthlyUtilization> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
            monthlyUtilizationRepository.deleteAllInBatch();
            return monthlyUtilizationRepository.saveAll(entityList);
        } catch (Exception e) {
            String entityName = entityList.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
                    ErrorId.DATA_NOT_SAVED_DYNAMIC, entityName));
        }
    }

    @Override
    public void deleteItem(DailyUtilization exUtilization) {
        dailyUtilizationRepository.deleteById(exUtilization.getId());
    }


}
