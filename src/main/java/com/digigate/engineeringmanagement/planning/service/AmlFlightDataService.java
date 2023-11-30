package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftRepository;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.entity.DailyUtilization;
import com.digigate.engineeringmanagement.planning.payload.request.AmlFlightDataDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightDataForOilUpliftReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DailyUtilizationReport;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.repository.AmlFlightDataRepository;
import com.digigate.engineeringmanagement.planning.repository.DailyUtilizationRepository;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * AML flight data service
 *
 * @author ashinisingha
 */
@Service
public class AmlFlightDataService
        extends AbstractService<AmlFlightData, AmlFlightDataDto> implements AmlFlightDataIService {

    private final AmlFlightDataRepository amlFlightDataRepository;

    private final AircraftMaintenanceLogRepository aircraftMaintenanceLogRepository;

    private final AircraftRepository aircraftRepository;

    private final DailyUtilizationRepository dailyUtilizationRepository;

    /**
     * parameterized constructor
     *
     * @param repository                 {@link AmlFlightDataRepository}
     * @param amlFlightDataRepository    {@link AmlFlightDataRepository}
     * @param dailyUtilizationRepository
     */
    public AmlFlightDataService(AbstractRepository<AmlFlightData> repository,
                                AmlFlightDataRepository amlFlightDataRepository,
                                AircraftMaintenanceLogRepository aircraftMaintenanceLogRepository,
                                AircraftRepository aircraftRepository, DailyUtilizationRepository dailyUtilizationRepository) {
        super(repository);
        this.aircraftRepository = aircraftRepository;
        this.amlFlightDataRepository = amlFlightDataRepository;
        this.aircraftMaintenanceLogRepository = aircraftMaintenanceLogRepository;
        this.dailyUtilizationRepository = dailyUtilizationRepository;
    }

    /**
     * This method is responsible getting AmlFlightViewModel by aml id
     *
     * @param amlId {@link  Long}
     * @return {@link  AmlFlightViewModel}
     */
    @Override
    public AmlFlightViewModel findByAmlId(Long amlId) {
        Optional<AmlFlightData> amlFlightData = amlFlightDataRepository.findAmlFlightDataByAmlId(amlId);
        return amlFlightData.map(this::convertToResponseDto).orElse(null);
    }

    @Override
    protected AmlFlightViewModel convertToResponseDto(AmlFlightData amlFlightData) {
        AmlFlightViewModel amlFlightViewModel = new AmlFlightViewModel();

        amlFlightViewModel.setId(amlFlightData.getId());
        amlFlightViewModel.setAmlId(amlFlightData.getAircraftMaintenanceLog().getId());
        amlFlightViewModel.setPageNo(amlFlightData.getAircraftMaintenanceLog().getPageNo());

        amlFlightViewModel.setBlockOnTime(amlFlightData.getBlockOnTime());
        amlFlightViewModel.setBlockOffTime(amlFlightData.getBlockOffTime());
        amlFlightViewModel.setBlockTime(amlFlightData.getBlockTime());

        amlFlightViewModel.setLandingTime(amlFlightData.getLandingTime());
        amlFlightViewModel.setTakeOffTime(amlFlightData.getTakeOffTime());
        amlFlightViewModel.setAirTime(amlFlightData.getAirTime());
        amlFlightViewModel.setTotalAirTime(amlFlightData.getTotalAirTime());
        amlFlightViewModel.setGrandTotalAirTime(amlFlightData.getGrandTotalAirTime());

        amlFlightViewModel.setNoOfLanding(amlFlightData.getNoOfLanding());
        amlFlightViewModel.setTotalLanding(amlFlightData.getTotalLanding());
        amlFlightViewModel.setGrandTotalLanding(amlFlightData.getGrandTotalLanding());

        amlFlightViewModel.setCommencedTime(amlFlightData.getCommencedTime());
        amlFlightViewModel.setCompletedTime(amlFlightData.getCompletedTime());
        amlFlightViewModel.setTotalApuHours(amlFlightData.getTotalApuHours());
        amlFlightViewModel.setTotalApuCycles(amlFlightData.getTotalApuCycles());
        amlFlightViewModel.setIsActive(amlFlightData.getIsActive());

        return amlFlightViewModel;
    }


    /**
     * This method is responsible for validate client inputted data
     *
     * @param amlId {@link  Long}
     */
    public void validateFlightData(Long amlId) {

        Optional<AmlFlightData> flightData = amlFlightDataRepository.findByAmlId(amlId);
        if (flightData.isEmpty()) {
            return;
        }
        throw new EngineeringManagementServerException(
                ErrorId.AML_ID_ALREADY_EXISTS,
                HttpStatus.BAD_REQUEST,
                MDC.get(ApplicationConstant.TRACE_ID)
        );
    }

    /**
     * This method is responsible for calculating duration time(HH.MM)
     * between to LocalDateTime
     *
     * @param startDateTime {@link  LocalDateTime}
     * @param endDateTime   {@link  LocalDateTime}
     * @return {@link  Double}
     */
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

    /**
     * This function is responsible for get all air time by amlIds
     *
     * @param amlIds {@link Set<Long>}
     * @return {@link AmlFlightDataForOilUpliftReportViewModel}
     */
    @Override
    public List<AmlFlightDataForOilUpliftReportViewModel> getAllFlightDataByAmlIdIn(Set<Long> amlIds) {
        return this.amlFlightDataRepository.findAllByAmlIdIn(amlIds);
    }

    @Override
    public List<AmlFlightData> findAllByIds(Set<Long> ids) {
        return amlFlightDataRepository.findAllByIdIn(ids);
    }

    @Override
    protected AmlFlightData convertToEntity(AmlFlightDataDto amlFlightDataDto) {
        return null;
    }

    @Override
    protected AmlFlightData updateEntity(AmlFlightDataDto dto, AmlFlightData entity) {
        return null;
    }


    @Override
    @Transactional
    public void migrateFlightDataIntoDailyUtilizationTable(LocalDate localDate) {


        List<Aircraft> aircraftList = aircraftRepository.findAllByIsActive(true);

        List<DailyUtilization> dailyUtilizationList = new ArrayList<>();

        aircraftList.forEach(aircraft -> {


            List<DailyUtilizationReport> dailyUtilizationReports = amlFlightDataRepository.findDailyUtilizationReports(
                    aircraft.getId(), DateUtil.getCurrentUTCDate());


            Map<LocalDate, List<DailyUtilizationReport>> listMap = dailyUtilizationReports.stream()
                    .collect(Collectors.groupingBy(DailyUtilizationReport::getDate));

            listMap.forEach((key, value) -> {

                AtomicReference<Double> usedHour = new AtomicReference<>(0.0);
                AtomicReference<Integer> usedCycle = new AtomicReference<>(0);
                AtomicReference<Double> apuHour = new AtomicReference<>(0.0);
                AtomicReference<Integer> apuCycle = new AtomicReference<>(0);
                AtomicReference<Double> tat = new AtomicReference<>(0.0);
                AtomicReference<Integer> tac = new AtomicReference<>(0);
                AtomicReference<Double> engineOil1 = new AtomicReference<>(0.0);
                AtomicReference<Double> engineOil2 = new AtomicReference<>(0.0);


                value.forEach(d -> {
                    usedHour.set(DateUtil.addTimes(usedHour.get(), NumberUtil.getDefaultIfNull(d.getUsedHrs(), 0.0)));
                    apuHour.set(DateUtil.addTimes(apuHour.get(), NumberUtil.getDefaultIfNull(d.getApuUsedHrs(), 0.0)));
                    usedCycle.set(usedCycle.get() + NumberUtil.getDefaultIfNull(d.getUsedCyc(), 0));
                    apuCycle.set(apuCycle.get() + NumberUtil.getDefaultIfNull(d.getApuUsedCycle(), 0));
                    tat.set(d.getTat());
                    tac.set(d.getTac());
                    engineOil1.set(d.getEng1OilUplift());
                    engineOil2.set(d.getEng2OilUplift());
                });

                dailyUtilizationList.add(DailyUtilization.builder()
                        .usedHours(usedHour.get())
                        .usedCycle(usedCycle.get())
                        .apuUsedHrs(aircraft.getTotalApuHours() >= ApplicationConstant.DOUBLE_VALUE_ZERO ? apuHour.get() : null)
                        .apuUsedCycle(aircraft.getTotalApuHours() >= ApplicationConstant.VALUE_ZERO ? apuCycle.get() : null)
                        .tat(tat.get())
                        .tac(tac.get())
                        .aircraft(aircraft)
                        .eng1OilUplift(engineOil1.get())
                        .eng2OilUplift(engineOil2.get())
                        .date(key)
                        .build());
            });
        });

        List<DailyUtilization> sortedList = dailyUtilizationList.stream()
                .sorted(Comparator.comparing(DailyUtilization::getDate))
                .collect(Collectors.toList());

        dailyUtilizationRepository.deleteAllInBatch();
        dailyUtilizationRepository.saveAll(sortedList);

    }
}
